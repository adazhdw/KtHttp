package com.adazhdw.kthttp.request

import com.adazhdw.kthttp.OkExt
import com.adazhdw.kthttp.callback.OkHttpCallback
import com.adazhdw.kthttp.callback.RequestCallback
import com.adazhdw.kthttp.constant.Method
import com.adazhdw.kthttp.entity.Param
import com.adazhdw.kthttp.util.RequestUrlUtil
import okhttp3.Call
import okhttp3.Request
import okhttp3.RequestBody

/**
 * author：adazhdw
 * date-time：2021/1/21 15:21
 * description：
 **/
class ParamRequest(val param: Param) : IRequest<ParamRequest> {
    private var mCallProxy: CallProxy? = null
    private var mCall: Call? = null
    private var tag = ""

    override fun getRawCall(): Call {
        val requestBody = getRequestBody()
        val mRequest = getRequest(requestBody)
        mCall = OkExt.mOkHttpClient.newCall(mRequest)
        return mCall!!
    }

    override fun getRequest(requestBody: RequestBody): Request {
        return when (param.method) {
            Method.GET -> requestBuilder().url(getRealUrl()).get().tag(tag).build()
            Method.DELETE -> requestBuilder().url(getRealUrl()).delete().tag(tag).build()
            Method.HEAD -> requestBuilder().url(getRealUrl()).head().tag(tag).build()
            Method.POST -> requestBuilder().url(getRealUrl()).post(requestBody).tag(tag).build()
            Method.PATCH -> requestBuilder().url(getRealUrl()).patch(requestBody).tag(tag).build()
            Method.PUT -> requestBuilder().url(getRealUrl()).put(requestBody).tag(tag).build()
        }
    }

    override fun getRequestBody(): RequestBody {
        return param.getRequestBody()
    }

    override fun getRealUrl(): String {
        return when (param.method) {
            Method.GET, Method.DELETE, Method.HEAD -> RequestUrlUtil.getFullUrl2(param.url, param.params(), param.urlEncoder)
            else -> param.url
        }
    }

    override fun requestBuilder(): Request.Builder {
        val builder = Request.Builder()
        if (param.needHeaders) {
            for ((key, value) in param.headers()) {
                builder.addHeader(key, value)
            }
        }
        return builder
    }

    /**
     * 执行网络请求
     */
    override fun enqueue(callback: RequestCallback?) {
        mCallProxy = CallProxy(getRawCall())
        mCallProxy!!.enqueue(OkHttpCallback(mCallProxy!!, callback))
    }

    /**
     * 取消网络请求
     */
    override fun cancel() {
        mCallProxy?.cancel()
    }

    override fun tag(tag: Any?): ParamRequest {
        this.tag(tag.toString())
        return this
    }

    override fun tag(tag: String): ParamRequest {
        this.tag = tag
        return this
    }

}