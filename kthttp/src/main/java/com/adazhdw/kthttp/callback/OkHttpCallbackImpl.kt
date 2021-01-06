package com.adazhdw.kthttp.callback

import com.adazhdw.kthttp.request.CallProxy
import com.adazhdw.kthttp.util.HttpLifecycleObserver
import com.adazhdw.kthttp.util.KtExecutors
import okhttp3.Call
import okhttp3.Response

/**
 * author：daguozhu
 * date-time：2020/11/18 9:30
 * description：
 **/

open class OkHttpCallbackImpl constructor(
    callProxy: CallProxy,
    private val requestCallback: RequestCallback?
) : OkHttpCallback(callProxy, requestCallback?.mLifecycleOwner) {
    init {
        KtExecutors.mainThread.execute {
            if (isLifecycleActive()) {
                requestCallback?.onStart(mCallProxy.call)
            }
        }
    }

    override fun response(response: Response) {
        val body = response.body ?: throw Exception("okhttp3.Response's body is null")
        val result = body.string()
        if (isLifecycleActive() && requestCallback != null) {
            requestCallback.onResult(body, result)
        }
    }

    override fun failure(e: Exception, call: Call) {
        e.printStackTrace()
        if (isLifecycleActive() && requestCallback != null) {
            KtExecutors.mainThread.execute {
                requestCallback.onFailure(e, call)
                requestCallback.onFinish()
            }
        }
    }

    /**
     * 判断当前宿主是否处于活动状态
     */
    private fun isLifecycleActive() = HttpLifecycleObserver.isLifecycleActive(mLifecycleOwner)

}