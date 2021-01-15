package com.adazhdw.kthttp.request.base

import com.adazhdw.kthttp.KtConfig
import com.adazhdw.kthttp.callback.OkHttpCallbackImpl
import com.adazhdw.kthttp.callback.RequestCallback
import com.adazhdw.kthttp.entity.Param
import com.adazhdw.kthttp.request.CallProxy
import com.adazhdw.kthttp.request.IRequest
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * author：daguozhu
 * date-time：2020/9/3 10:11
 * description：
 **/
abstract class BaseRequest(val param: Param) : IRequest {
    private val okHttpClient: OkHttpClient = KtConfig.mOkHttpClient
    private var mCallProxy: CallProxy? = null
    private var mCall: Call? = null
    protected var tag = ""

    /**
     * 执行网络请求
     */
    fun enqueue(callback: RequestCallback?) {
        mCallProxy = CallProxy(getRawCall())
        mCallProxy!!.enqueue(OkHttpCallbackImpl(mCallProxy!!, callback))
    }

    /**
     * 取消网络请求
     */
    fun cancel() {
        mCallProxy?.cancel()
    }

    /**
     * 获取当前请求的 okhttp.Call
     */
    override fun getRawCall(): Call {
        if (mCall == null) {
            val requestBody = getRequestBody()
            val mRequest = getRequest(requestBody)
            mCall = okHttpClient.newCall(mRequest)
        }
        return mCall!!
    }

    /**
     * 生成一个 Request.Builder，并且给当前请求 Request 添加 headers
     */
    override fun requestBuilder(): Request.Builder {
        val builder = Request.Builder()
        if (param.needHeaders) {
            for ((key, value) in param.headers()) {
                builder.addHeader(key, value)
            }
        }
        return builder
    }

    override fun getRealUrl() = param.url

    open fun tag(tag: Any?): BaseRequest {
        this.tag(tag.toString())
        return this
    }

    open fun tag(tag: String): BaseRequest {
        this.tag = tag
        return this
    }

}