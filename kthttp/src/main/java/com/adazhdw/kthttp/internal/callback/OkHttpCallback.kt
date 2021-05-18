package com.adazhdw.kthttp.internal.callback

import com.adazhdw.kthttp.HttpClient
import com.adazhdw.kthttp.internal.HttpCallProxy
import com.adazhdw.kthttp.internal.HttpLifecycleObserver
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
    private val httpClient: HttpClient,
    private val mCallProxy: HttpCallProxy,
    private val requestCallback: RequestCallback?
) : Callback {

    init {
        HttpLifecycleObserver.bind(requestCallback?.mLifecycleOwner, onDestroy = { mCallProxy.cancel() })
        execute(Runnable {
            if (isLifecycleActive()) {
                requestCallback?.onStart(mCallProxy.call)
            }
        }, false)
    }

    override fun onResponse(call: Call, response: Response) {
        try {
            response(response, call)
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
        if (isLifecycleActive() && requestCallback != null) {
            execute(Runnable {
                response.use {
                    requestCallback.onResult(it, call)
                }
            }, true)
        }
    }

    open fun failure(e: Exception, call: Call) {
        e.printStackTrace()
        if (isLifecycleActive() && requestCallback != null) {
            execute(Runnable {
                requestCallback.onFailure(e, call)
                requestCallback.onFinish()
            }, false)
        }
    }

    private fun execute(runnable: Runnable, onIO: Boolean) {
        httpClient.execute(runnable, onIO)
    }


    /**
     * 判断当前宿主是否处于活动状态
     */
    private fun isLifecycleActive() = HttpLifecycleObserver.isLifecycleActive(requestCallback?.mLifecycleOwner)

}
