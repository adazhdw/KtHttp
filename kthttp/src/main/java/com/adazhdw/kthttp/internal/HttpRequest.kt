package com.adazhdw.kthttp.internal

import com.adazhdw.kthttp.HttpClient
import com.adazhdw.kthttp.internal.callback.OkHttpCallback
import com.adazhdw.kthttp.internal.callback.RequestCallback
import com.adazhdw.kthttp.util.IOUtils
import com.adazhdw.kthttp.util.MimeUtils
import com.adazhdw.kthttp.util.RequestUrlUtils
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http.HttpMethod
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * author：daguozhu
 * date-time：2020/9/3 10:11
 * description：HttpRequest
 **/
open class HttpRequest(val httpClient: HttpClient) {
    /**
     * ---HTTP 相关参数和方法--------------------------------------------------------------------------------
     */

    private var url: String = ""
    private var method: Method = Method.GET
    private val headers by lazy { mutableMapOf<String, String>() }// 请求任务的头信息
    private val queryParams by lazy { mutableMapOf<String, String>() }// URL参数（查询参数）
    private val bodyParams by lazy { mutableMapOf<String, String>() }// 报文体参数
    private val fileParams by lazy { mutableListOf<Part>() } // 文件上传参数
    private var requestBody: String = ""
    private var bodyType: HttpBodyType = HttpBodyType.FORM

    /**
     * URL编码，只对GET,DELETE,HEAD有效
     */
    private var urlEncoder: Boolean = false
    private var needHeader: Boolean = false

    protected var mCallProxy: HttpCallProxy? = null
    private var mCall: okhttp3.Call? = null
    private var tag = ""

    fun sync() = SyncHttpRequest(httpClient)
    fun async() = AsyncHttpRequest(httpClient)

    fun url(url: String): HttpRequest = apply {
        this.url = url
    }

    fun method(method: Method): HttpRequest = apply {
        this.method = method
    }

    /**
     * Param 设置请求方式的扩展方法
     */
    fun get(): HttpRequest = apply { method(Method.GET) }
    fun post(): HttpRequest = apply { method(Method.POST) }
    fun delete(): HttpRequest = apply { method(Method.DELETE) }
    fun head(): HttpRequest = apply { method(Method.HEAD) }
    fun patch(): HttpRequest = apply { method(Method.PATCH) }
    fun put(): HttpRequest = apply { method(Method.PUT) }

    fun bodyType(bodyType: HttpBodyType): HttpRequest = apply {
        this.bodyType = bodyType
    }

    fun urlEncoder(urlEncoder: Boolean): HttpRequest = apply {
        this.urlEncoder = urlEncoder
    }

    fun needHeader(needHeader: Boolean): HttpRequest = apply {
        this.needHeader = needHeader
    }

    fun addHeaders(headers: Map<String, String>): HttpRequest = apply {
        this.headers.putAll(headers)
    }

    fun addHeader(key: String, value: String): HttpRequest = apply {
        if (key.isNotBlank()) {
            headers[key] = value
        }
    }

    fun queryParams(key: String, value: String): HttpRequest = apply {
        if (key.isNotBlank()) {
            queryParams[key] = value
        }
    }

    fun queryParams(paramMap: Map<String, String>): HttpRequest = apply {
        queryParams.putAll(paramMap)
    }

    fun bodyParams(key: String, value: String): HttpRequest = apply {
        if (key.isNotBlank()) {
            bodyParams[key] = value
        }
    }

    fun bodyParams(paramMap: Map<String, String>): HttpRequest = apply {
        bodyParams.putAll(paramMap)
    }

    fun requestBody(requestBody: String): HttpRequest = apply {
        this.requestBody = requestBody
    }

    fun addFileParam(key: String, file: File): HttpRequest {
        if (key.isBlank() || !file.exists() || file.length() == 0L) return this
        val isPng = file.name.indexOf("png") > 0 || file.name.indexOf("PNG") > 0
        if (isPng) {
            this.fileParams.add(Part(key, Part.FileWrapper(file, HttpHeaders.PNG)))
            return this
        }
        val isJpg = file.name.indexOf("jpg") > 0 || file.name.indexOf("JPG") > 0
                || file.name.indexOf("jpeg") > 0 || file.name.indexOf("JPEG") > 0
        if (isJpg) {
            this.fileParams.add(Part(key, Part.FileWrapper(file, HttpHeaders.JPG)))
            return this
        }
        if (!isPng && !isJpg) {
            this.fileParams.add(Part(key, Part.FileWrapper(file, MimeUtils.getMediaType(file.path))))
        }
        return this
    }

    fun addFileParam(map: Map<String, File>) = apply {
        for ((key, file) in map) addFileParam(key, file)
    }

    fun tag(tag: Any?): HttpRequest {
        tag(tag.toString())
        return this
    }

    fun tag(tag: String): HttpRequest {
        this.tag = tag
        return this
    }

    /**
     * 获取当前请求的 okhttp.Call
     */
    fun getRawCall(): okhttp3.Call {
        if (mCall == null) {
            val mRequest = getRequest()
            mCall = httpClient.client.newCall(mRequest)
        }
        return mCall!!
    }

    /**
     * 根据请求方法类型获取 Request
     */
    private fun getRequest(): okhttp3.Request {
        val method = this.method.name.toUpperCase(Locale.getDefault())
        val bodyCanUsed = HttpMethod.permitsRequestBody(method)//okhttp3.RequestBody可以用在请求体中
        val requireBody = HttpMethod.requiresRequestBody(method)//请求体中必须设置okhttp3.RequestBody
        assertNotConflict(bodyCanUsed)
        if (!requireBody) {
            queryParams.putAll(httpClient.commonParams)
        }
        val realUrl = RequestUrlUtils.getFullUrl2(url, queryParams, urlEncoder)
        val requestBuilder = okhttp3.Request.Builder().url(realUrl).tag(tag)

        if (needHeader) {
            for ((key, value) in headers) {//专属请求header
                requestBuilder.addHeader(key, value)
            }
            for ((key, value) in httpClient.commonHeaders) {//接口通用header
                requestBuilder.addHeader(key, value)
            }
        }
        if (bodyCanUsed) {
            val requestBody: okhttp3.RequestBody? = getRequestBody()
            requestBuilder.method(method, requestBody)
        } else {
            requestBuilder.method(method, null)
        }
        return requestBuilder.build()
    }

    private fun assertNotConflict(bodyCanUsed: Boolean) {
        if (!bodyCanUsed) {
            when {
                requestBody.isNotBlank() -> {
                    throw HttpException("GET | HEAD request cannot call the requestBody() method!")
                }
                bodyParams.isNotEmpty() -> {
                    throw HttpException("GET | HEAD request cannot call the bodyParams() method!")
                }
                fileParams.isNotEmpty() -> {
                    throw HttpException("GET | HEAD request cannot call the addFileParam() method!")
                }
            }
        }
        if (requestBody.isNotBlank()) {
            when {
                bodyParams.isNotEmpty() -> {
                    throw HttpException("The methods bodyParams() and requestBody() cannot be called at the same time")
                }
                fileParams.isNotEmpty() -> {
                    throw HttpException("The methods addFileParam() and requestBody() cannot be called at the same time")
                }
            }
        }
    }

    private fun getRequestBody(): okhttp3.RequestBody? {
        return if (bodyType == HttpBodyType.JSON) {
            getJsonRequestBody()
        } else {
            getFormBody()
        }
    }

    private fun getJsonRequestBody(): okhttp3.RequestBody? {
        return when {
            requestBody.isNotBlank() -> {
                requestBody.toRequestBody(HttpHeaders.MEDIA_TYPE_JSON)
            }
            bodyParams.isNotEmpty() -> {
                val jsonObject = JSONObject()
                for ((key, value) in bodyParams) jsonObject.put(key, value)
                jsonObject.toString().toRequestBody(HttpHeaders.MEDIA_TYPE_JSON)
            }
            else -> {
                null
            }
        }
    }

    private fun getFormBody(): okhttp3.RequestBody {
        when {
            fileParams.isNotEmpty() -> {
                val builder = okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM)
                for (part in fileParams) {
                    builder.addFormDataPart(part.key, part.wrapper.fileName, part.wrapper.file.asRequestBody(part.wrapper.mediaType))
                }
                for ((key, value) in bodyParams) {
                    builder.addFormDataPart(key, value)
                }
                return builder.build()
            }
            bodyParams.isNotEmpty() -> {
                return okhttp3.FormBody.Builder().apply {
                    for ((name, value) in bodyParams) {
                        add(name, value)
                    }
                }.build()
            }
            else -> {
                return okhttp3.FormBody.Builder().build()
            }
        }
    }

    /**
     * 取消网络请求
     */
    fun cancel() {
        mCallProxy?.cancel()
    }

    class TimeoutHolder(val timeOut: Long, val timeUnit: TimeUnit = TimeUnit.SECONDS)

}

class SyncHttpRequest(httpClient: HttpClient) : HttpRequest(httpClient) {

    /**
     * 同步网络请求
     */
    fun executeRequest(): HttpResponse {
        val call = getRawCall()
        mCallProxy = HttpCallProxy(call)
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
                if (!httpResponse.isSuccessful) {
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

}

class AsyncHttpRequest(httpClient: HttpClient) : HttpRequest(httpClient) {

    /**
     * 异步执行网络请求
     */
    fun enqueueRequest(callback: RequestCallback?) {
        val call = getRawCall()
        mCallProxy = HttpCallProxy(call)
        mCallProxy!!.enqueue(OkHttpCallback(httpClient, mCallProxy!!, callback))
    }

}