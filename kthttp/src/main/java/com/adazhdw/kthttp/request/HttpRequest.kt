package com.adazhdw.kthttp.request

import com.adazhdw.kthttp.OkConfig
import com.adazhdw.kthttp.callback.OkHttpCallback
import com.adazhdw.kthttp.callback.RequestCallback
import com.adazhdw.kthttp.coder.UrlCoder
import com.adazhdw.kthttp.constant.BodyType
import com.adazhdw.kthttp.constant.HttpConstant
import com.adazhdw.kthttp.constant.Method
import com.adazhdw.kthttp.request.entity.HttpHeaders
import com.adazhdw.kthttp.request.entity.HttpParams
import com.adazhdw.kthttp.request.exception.HttpException
import com.adazhdw.kthttp.util.IOUtils
import com.adazhdw.kthttp.util.RequestUrlUtil
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * author：daguozhu
 * date-time：2020/9/3 10:11
 * description：HttpRequest
 **/
open class HttpRequest(isMultipart: Boolean = false) : IRequest<HttpRequest> {
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
    private var connectTimeout: TimeoutHolder = TimeoutHolder(0)
    private var readTimeout: TimeoutHolder = TimeoutHolder(0)
    private var writeTimeout: TimeoutHolder = TimeoutHolder(0)


    private var mCallProxy: CallProxy? = null
    private var mCall: okhttp3.Call? = null
    private var tag = ""

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

    override fun connectTimeout(connectTimeout: Int): HttpRequest {
        if (connectTimeout < 0) return this
        return connectTimeout(connectTimeout, TimeUnit.SECONDS)
    }

    override fun connectTimeout(connectTimeout: Int, timeUnit: TimeUnit): HttpRequest {
        if (connectTimeout < 0) return this
        this.connectTimeout = TimeoutHolder(connectTimeout, timeUnit)
        return this
    }

    override fun readTimeout(readTimeout: Int): HttpRequest {
        if (readTimeout < 0) return this
        return readTimeout(readTimeout, TimeUnit.SECONDS)
    }

    override fun readTimeout(readTimeout: Int, timeUnit: TimeUnit): HttpRequest {
        if (readTimeout < 0) return this
        this.readTimeout = TimeoutHolder(readTimeout, timeUnit)
        return this
    }

    override fun writeTimeout(writeTimeout: Int): HttpRequest {
        if (writeTimeout < 0) return this
        return writeTimeout(writeTimeout, TimeUnit.SECONDS)
    }

    override fun writeTimeout(writeTimeout: Int, timeUnit: TimeUnit): HttpRequest {
        if (writeTimeout < 0) return this
        this.writeTimeout = TimeoutHolder(writeTimeout, timeUnit)
        return this
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
     * Param 设置请求方式的扩展方法
     */
    fun get(): HttpRequest = this.apply { this.method(Method.GET) }
    fun post(): HttpRequest = this.apply { this.method(Method.POST) }
    fun delete(): HttpRequest = this.apply { this.method(Method.DELETE) }
    fun head(): HttpRequest = this.apply { this.method(Method.HEAD) }
    fun patch(): HttpRequest = this.apply { this.method(Method.PATCH) }
    fun put(): HttpRequest = this.apply { this.method(Method.PUT) }

    /**
     * 获取当前请求的 okhttp.Call
     */
    override fun getRawCall(): okhttp3.Call {
        if (mCall == null) {
            val requestBody = getRequestBody()
            val mRequest = getRequest(requestBody)
            mCall = OkConfig.config.mOkHttpClient.newCall(mRequest)
        }
        return mCall!!
    }

    override fun getRequestBody(): okhttp3.RequestBody {
        return if (bodyType() == BodyType.JSON) {
            getJsonRequestBody()
        } else {
            getFormBody()
        }
    }

    private fun getJsonRequestBody(): okhttp3.RequestBody {
        return if (jsonBody.isNotBlank()) {
            jsonBody.toRequestBody(HttpConstant.MEDIA_TYPE_JSON)
        } else {
            val jsonObject = JSONObject()
            for ((key, value) in params.contents) jsonObject.put(key, value)
            jsonObject.toString().toRequestBody(HttpConstant.MEDIA_TYPE_JSON)
        }
    }

    private fun getFormBody(): okhttp3.RequestBody {
        if (params.isMultipart && params.files.isNotEmpty()) {
            val builder = okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM)
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
            return okhttp3.FormBody.Builder().apply {
                for ((name, value) in params.contents) {
                    add(name, value.toString())
                }
            }.build()
        }
    }

    /**
     * 生成一个 Request.Builder，并且给当前请求 Request 添加 headers
     */
    override fun requestBuilder(): okhttp3.Request.Builder {
        val builder = okhttp3.Request.Builder()
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
    override fun getRequest(requestBody: okhttp3.RequestBody): okhttp3.Request {
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
     * 同步网络请求
     */
    override fun execute(): HttpResponse {
        mCallProxy = CallProxy(getRawCall())
        var response: okhttp3.Response? = null
        try {
            response = mCallProxy!!.execute()
            val body = response.body
            if (body != null) {
                val byteData = body.bytes()

                val newResponse = response.newBuilder()
                    .body(byteData.toResponseBody(body.contentType()))
                    .build()

                val httpResponse = HttpResponse(newResponse)
                if (!httpResponse.succeed) {
                    if (!mCallProxy!!.isCanceled()) {
                        mCallProxy?.cancel()
                    }
                }
                return httpResponse
            } else {
                throw HttpException("okhttp3.Response's body is null")
            }
        } catch (e: IOException) {
            throw HttpException(e)
        } finally {
            IOUtils.closeQuietly(response)
        }

    }

    /**
     * 异步执行网络请求
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

    class TimeoutHolder(val timeOut: Int, val timeUnit: TimeUnit = TimeUnit.SECONDS)

}