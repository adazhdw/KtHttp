package com.adazhdw.kthttp.request

import com.adazhdw.kthttp.OkConfig
import com.adazhdw.kthttp.callback.OkHttpCallback
import com.adazhdw.kthttp.callback.RequestCallback
import com.adazhdw.kthttp.coder.UrlCoder
import com.adazhdw.kthttp.constant.BodyType
import com.adazhdw.kthttp.constant.HttpConstant
import com.adazhdw.kthttp.constant.Method
import com.adazhdw.kthttp.entity.HttpHeaders
import com.adazhdw.kthttp.entity.HttpParams
import com.adazhdw.kthttp.util.RequestUrlUtil
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

/**
 * author：daguozhu
 * date-time：2020/9/3 10:11
 * description：Param
 **/
open class HttpRequest(isMultipart: Boolean = false) : IRequest<HttpRequest>, IParams<HttpRequest> {
    private var mCallProxy: CallProxy? = null
    private var mCall: Call? = null
    private var tag = ""

    /**
     * 获取当前请求的 okhttp.Call
     */
    override fun getRawCall(): Call {
        if (mCall == null) {
            val requestBody = getRequestBody()
            val mRequest = getRequest(requestBody)
            mCall = OkConfig.config.mOkHttpClient.newCall(mRequest)
        }
        return mCall!!
    }

    /**
     * 生成一个 Request.Builder，并且给当前请求 Request 添加 headers
     */
    override fun requestBuilder(): Request.Builder {
        val builder = Request.Builder()
        if (needHeaders) {
            for ((key, value) in headers()) {
                builder.addHeader(key, value)
            }
        }
        return builder
    }

    override fun getRealUrl(): String {
        return when (method) {
            Method.GET, Method.DELETE, Method.HEAD -> RequestUrlUtil.getFullUrl2(url, params(), urlEncoder)
            else -> url
        }
    }

    /**
     * 根据请求方法类型获取 Request
     */
    override fun getRequest(requestBody: RequestBody): Request {
        return when (method) {
            Method.GET -> requestBuilder().url(getRealUrl()).get().tag(tag).build()
            Method.DELETE -> requestBuilder().url(getRealUrl()).delete().tag(tag).build()
            Method.HEAD -> requestBuilder().url(getRealUrl()).head().tag(tag).build()
            Method.POST -> requestBuilder().url(getRealUrl()).post(requestBody).tag(tag).build()
            Method.PATCH -> requestBuilder().url(getRealUrl()).patch(requestBody).tag(tag).build()
            Method.PUT -> requestBuilder().url(getRealUrl()).put(requestBody).tag(tag).build()
        }
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

    override fun tag(tag: Any?): HttpRequest {
        this.tag(tag.toString())
        return this
    }

    override fun tag(tag: String): HttpRequest {
        this.tag = tag
        return this
    }


    /**
     * ---HTTP 相关参数和方法--------------------------------------------------------------------------------
     */

    private var url: String = ""
    private var method: Method = Method.GET
    private val headers: HttpHeaders = HttpHeaders()
    private val params: HttpParams = HttpParams(isMultipart)
    private var jsonBody: String = ""
    private var bodyType: BodyType = BodyType.FORM
    private val urlCoder = UrlCoder.create()

    /**
     * URL编码，只对GET,DELETE,HEAD有效
     */
    private var urlEncoder: Boolean = false
    private var needHeaders: Boolean = false

    override fun getRequestBody(): RequestBody {
        return if (bodyType() == BodyType.JSON) {
            getJsonRequestBody()
        } else {
            getFormBody()
        }
    }

    private fun getJsonRequestBody(): RequestBody {
        return if (jsonBody.isNotBlank()) {
            jsonBody.toRequestBody(HttpConstant.MEDIA_TYPE_JSON)
        } else {
            val jsonObject = JSONObject()
            for ((key, value) in params.contents) jsonObject.put(key, value)
            jsonObject.toString().toRequestBody(HttpConstant.MEDIA_TYPE_JSON)
        }
    }

    private fun getFormBody(): RequestBody {
        if (params.isMultipart && params.files.isNotEmpty()) {
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            for (part in params.files) {
                builder.addFormDataPart(
                    part.key,
                    urlCoder.encode(part.wrapper.fileName),
                    part.wrapper.file.asRequestBody(part.wrapper.mediaType)
                )
            }
            for ((key, value) in params.contents) {
                builder.addFormDataPart(key, value.toString())
            }
            return builder.build()
        } else {
            return FormBody.Builder().apply {
                for ((name, value) in params.contents) {
                    add(name, value.toString())
                }
            }.build()
        }
    }

    override fun url(url: String): HttpRequest = apply {
        this.url = url
    }

    override fun method(method: Method): HttpRequest = apply {
        this.method = method
    }

    override fun bodyType(bodyType: BodyType): HttpRequest = apply {
        this.bodyType = bodyType
    }

    fun bodyType() = this.bodyType

    override fun setUrlEncoder(urlEncoder: Boolean): HttpRequest = apply {
        this.urlEncoder = urlEncoder
    }

    override fun setNeedHeaders(needHeaders: Boolean): HttpRequest = apply {
        this.needHeaders = needHeaders
    }

    override fun setJsonBody(jsonBody: String): HttpRequest = apply {
        this.jsonBody = jsonBody
    }

    override fun addHeaders(headers: Map<String, String>): HttpRequest = apply {
        this.headers.putAll(headers)
    }

    override fun addHeader(key: String, value: String): HttpRequest = apply {
        this.headers.put(key, value)
    }

    fun headers(): HashMap<String, String> {
        return this.headers.contents
    }

    override fun addParam(key: String, value: String): HttpRequest = apply {
        this.params.put(key, value)
    }

    override fun addParams(paramMap: Map<String, String>): HttpRequest = apply {
        this.params.putAll(paramMap)
    }

    fun params(): HashMap<String, Any> {
        return this.params.contents
    }

    override fun addFormDataPart(key: String, file: File) = apply {
        this.params.addFormDataPart(key, file)
    }

    override fun addFormDataPart(map: Map<String, File>) = apply {
        this.params.addFormDataPart(map)
    }

    /**
     * Param 设置请求方式的扩展方法
     */
    fun get(): HttpRequest = this.apply { this.method(Method.GET) }
    fun post(): HttpRequest = this.apply { this.method(Method.POST) }
    fun delete(): HttpRequest = this.apply { this.method(Method.DELETE) }
    fun head(): HttpRequest = this.apply { this.method(Method.HEAD) }
    fun patch(): HttpRequest = this.apply { this.method(Method.PATCH) }
    fun put(): HttpRequest = this.apply { this.method(Method.PUT) }

}