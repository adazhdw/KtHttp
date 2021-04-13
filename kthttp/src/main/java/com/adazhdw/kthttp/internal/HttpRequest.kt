package com.adazhdw.kthttp.internal

import com.adazhdw.kthttp.HttpClient
import com.adazhdw.kthttp.callback.OkHttpCallback
import com.adazhdw.kthttp.callback.RequestCallback
import com.adazhdw.kthttp.util.IOUtils
import com.adazhdw.kthttp.util.RequestUrlUtils
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
open class HttpRequest(private val httpClient: HttpClient) {
    /**
     * ---HTTP 相关参数和方法--------------------------------------------------------------------------------
     */

    private var url: String = ""
    private var method: Method = Method.GET
    private val headers: HttpHeaders = HttpHeaders(httpClient)
    private val params: HttpParams = HttpParams(httpClient)
    private var jsonBody: String = ""
    private var bodyType: HttpBodyType = HttpBodyType.FORM

    /**
     * URL编码，只对GET,DELETE,HEAD有效
     */
    private var urlEncoder: Boolean = false
    private var needHeaders: Boolean = false

    private var mCallProxy: HttpCallProxy? = null
    private var mCall: okhttp3.Call? = null
    private var tag = ""

    fun url(url: String): HttpRequest = apply {
        this.url = url
    }

    fun method(method: Method): HttpRequest = apply {
        this.method = method
    }

    fun bodyType(bodyType: HttpBodyType): HttpRequest = apply {
        this.bodyType = bodyType
    }

    fun bodyType() = this.bodyType

    fun setUrlEncoder(urlEncoder: Boolean): HttpRequest = apply {
        this.urlEncoder = urlEncoder
    }

    fun setNeedHeaders(needHeaders: Boolean): HttpRequest = apply {
        this.needHeaders = needHeaders
    }

    fun setJsonBody(jsonBody: String): HttpRequest = apply {
        this.jsonBody = jsonBody
    }

    fun addHeaders(headers: Map<String, String>): HttpRequest = apply {
        this.headers.putAll(headers)
    }

    fun addHeader(key: String, value: String): HttpRequest = apply {
        this.headers.put(key, value)
    }

    fun headers(): HashMap<String, String> {
        return this.headers.contents
    }

    fun addParam(key: String, value: String): HttpRequest = apply {
        this.params.put(key, value)
    }

    fun addParams(paramMap: Map<String, String>): HttpRequest = apply {
        this.params.putAll(paramMap)
    }

    fun params(): HashMap<String, Any> {
        return this.params.contents
    }

    fun addFormDataPart(key: String, file: File) = apply {
        this.params.addFormDataPart(key, file)
    }

    fun addFormDataPart(map: Map<String, File>) = apply {
        this.params.addFormDataPart(map)
    }

    fun tag(tag: Any?): HttpRequest {
        this.tag(tag.toString())
        return this
    }

    fun tag(tag: String): HttpRequest {
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
    fun getRawCall(): okhttp3.Call {
        if (mCall == null) {
            val requestBody = getRequestBody()
            val mRequest = getRequest(requestBody)
            mCall = httpClient.client.newCall(mRequest)
        }
        return mCall!!
    }

    fun getRequestBody(): okhttp3.RequestBody {
        return if (bodyType() == HttpBodyType.JSON) {
            getJsonRequestBody()
        } else {
            getFormBody()
        }
    }

    private fun getJsonRequestBody(): okhttp3.RequestBody {
        return if (jsonBody.isNotBlank()) {
            jsonBody.toRequestBody(HttpHeaders.MEDIA_TYPE_JSON)
        } else {
            val jsonObject = JSONObject()
            for ((key, value) in params.contents) jsonObject.put(key, value)
            jsonObject.toString().toRequestBody(HttpHeaders.MEDIA_TYPE_JSON)
        }
    }

    private fun getFormBody(): okhttp3.RequestBody {
        if (params.files.isNotEmpty()) {
            val builder = okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM)
            for (part in params.files) {
                builder.addFormDataPart(part.key, part.wrapper.fileName, part.wrapper.file.asRequestBody(part.wrapper.mediaType))
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
    fun requestBuilder(): okhttp3.Request.Builder {
        val builder = okhttp3.Request.Builder()
        if (needHeaders) {
            for ((key, value) in headers()) {
                builder.addHeader(key, value)
            }
        }
        return builder
    }

    fun getRealUrl(): String {
        return when (method) {
            Method.GET, Method.DELETE, Method.HEAD -> RequestUrlUtils.getFullUrl2(url, params(), urlEncoder)
            else -> url
        }
    }

    /**
     * 根据请求方法类型获取 Request
     */
    fun getRequest(requestBody: okhttp3.RequestBody): okhttp3.Request {
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
    fun execute(): HttpResponse {
        mCallProxy = HttpCallProxy(getRawCall())
        var response: okhttp3.Response? = null
        try {
            response = mCallProxy!!.execute()
            val body = response.body
            if (body != null) {
                val byteData = body.bytes()

                val newResponse = response.newBuilder()
                    .body(byteData.toResponseBody(body.contentType()))
                    .build()

                val httpResponse = HttpResponse(newResponse, httpClient)
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
    fun enqueue(callback: RequestCallback?) {
        mCallProxy = HttpCallProxy(getRawCall())
        mCallProxy!!.enqueue(OkHttpCallback(mCallProxy!!, callback))
    }

    /**
     * 取消网络请求
     */
    fun cancel() {
        mCallProxy?.cancel()
    }

    class TimeoutHolder(val timeOut: Long, val timeUnit: TimeUnit = TimeUnit.SECONDS)

}