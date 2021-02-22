package com.adazhdw.kthttp.callback

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.ext.logD
import com.adazhdw.kthttp.ext.logE
import okhttp3.Call
import okhttp3.Response

/**
 * author：daguozhu
 * date-time：2020/12/1 14:58
 * description：
 **/
open class RequestCallbackImpl(private val owner: LifecycleOwner?) : RequestCallback {
    final override val mLifecycleOwner: LifecycleOwner?
        get() = owner

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

}