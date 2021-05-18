package com.adazhdw.kthttp.internal.callback

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.HttpClient
import com.adazhdw.kthttp.util.logD
import com.adazhdw.kthttp.util.logE
import okhttp3.Call
import okhttp3.Response

/**
 * author：daguozhu
 * date-time：2020/12/1 14:58
 * description：
 **/
open class RequestCallbackImpl(private val owner: LifecycleOwner?, private val client: HttpClient) : RequestCallback {
    final override val mLifecycleOwner: LifecycleOwner?
        get() = owner
    override val httpClient: HttpClient
        get() = client

    companion object {
        const val TAG = "RequestCallbackImpl"
    }

    override fun onStart(call: Call) {
        "onStart".logD(TAG)
    }

    override fun onResult(response: Response, call: Call) {
        "onHttpResponse".logD(TAG)
    }

    override fun onFailure(e: Exception, call: Call) {
        "onFailure:$e".logE(TAG)
    }

    override fun onFinish() {
        "onFinish".logD(TAG)
    }

    protected fun execute(runnable: Runnable, onIO: Boolean) {
        httpClient.execute(runnable, onIO)
    }

}