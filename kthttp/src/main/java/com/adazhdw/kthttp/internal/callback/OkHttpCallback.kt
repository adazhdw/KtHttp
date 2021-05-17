package com.adazhdw.kthttp.internal.callback

import com.adazhdw.kthttp.internal.HttpCallProxy
import com.adazhdw.kthttp.internal.HttpLifecycleObserver
import com.adazhdw.kthttp.util.ExecutorUtils
import com.google.gson.JsonParseException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * author：daguozhu
 * date-time：2020/11/17 20:25
 * description：
 **/

open class OkHttpCallback(
    private val mCallProxy: HttpCallProxy,
    private val requestCallback: RequestCallback?
) : Callback {

    init {
        HttpLifecycleObserver.bind(requestCallback?.mLifecycleOwner, onDestroy = { mCallProxy.cancel() })
        ExecutorUtils.mainThread.execute {
            if (isLifecycleActive()) {
                requestCallback?.onStart(mCallProxy.call)
            }
        }
    }

    override fun onResponse(call: Call, response: Response) {
        try {
            ExecutorUtils.networkIO.submit {
                response.use { response(it, call) }
            }
        } catch (e: JsonParseException) {
            failure(e, call)
        } catch (e: Exception) {
            failure(e, call)
        }
    }

    override fun onFailure(call: Call, e: IOException) {
        failure(e, call)
    }

    open fun response(response: Response, call: Call) {
//        val body = response.body ?: throw Exception("okhttp3.Response's body is null")
        if (isLifecycleActive() && requestCallback != null) {
            requestCallback.onResult(response, call)
        }
    }

    open fun failure(e: Exception, call: Call) {
        e.printStackTrace()
        if (isLifecycleActive() && requestCallback != null) {
            ExecutorUtils.mainThread.execute {
                requestCallback.onFailure(e, call)
                requestCallback.onFinish()
            }
        }
    }


    /**
     * 判断当前宿主是否处于活动状态
     */
    private fun isLifecycleActive() = HttpLifecycleObserver.isLifecycleActive(requestCallback?.mLifecycleOwner)

}