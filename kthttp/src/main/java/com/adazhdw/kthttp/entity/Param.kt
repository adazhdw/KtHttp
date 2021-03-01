package com.adazhdw.kthttp.entity

import com.adazhdw.kthttp.coder.UrlCoder
import com.adazhdw.kthttp.constant.BodyType
import com.adazhdw.kthttp.constant.HttpConstant
import com.adazhdw.kthttp.constant.Method
import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

/**
 * Author: dgz
 * Date: 2020/8/21 14:50
 * Description:请求参数工具类
 */
/** @param isMultipart 是否有流参数 */
class Param private constructor(isMultipart: Boolean) {

    internal var url: String = ""
        private set
    internal var method: Method = Method.GET
        private set
    private val headers: HttpHeaders = HttpHeaders()
    private val params: HttpParams = HttpParams(isMultipart)
    private var jsonBody: String = ""
    private var bodyType: BodyType = BodyType.FORM
    private val urlCoder = UrlCoder.create()

    /**
     * URL编码，只对GET,DELETE,HEAD有效
     */
    internal var urlEncoder: Boolean = false
    internal var needHeaders: Boolean = false

    internal fun getRequestBody(): RequestBody {
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

    fun url(url: String): Param {
        this.url = url
        return this
    }

    fun method(method: Method): Param {
        this.method = method
        return this
    }

    fun bodyType(bodyType: BodyType): Param {
        this.bodyType = bodyType
        return this
    }

    fun bodyType() = this.bodyType

    fun setUrlEncoder(urlEncoder: Boolean): Param {
        this.urlEncoder = urlEncoder
        return this
    }

    fun setNeedHeaders(needHeaders: Boolean): Param {
        this.needHeaders = needHeaders
        return this
    }

    fun setJsonBody(jsonBody: String): Param {
        this.jsonBody = jsonBody
        return this
    }

    fun addHeaders(headers: Map<String, String>): Param {
        this.headers.putAll(headers)
        return this
    }

    fun addHeader(key: String, value: String): Param {
        this.headers.put(key, value)
        return this
    }

    fun headers(): HashMap<String, String> {
        return this.headers.contents
    }

    fun addParam(key: String, value: String): Param {
        this.params.put(key, value)
        return this
    }

    fun addParams(paramMap: Map<String, String>): Param {
        this.params.putAll(paramMap)
        return this
    }

    fun params(): HashMap<String, Any> {
        return this.params.contents
    }

    fun addFormDataPart(key: String, file: File) {
        this.params.addFormDataPart(key, file)
    }

    fun addFormDataPart(map: Map<String, File>) {
        this.params.addFormDataPart(map)
    }

    /**
     * Param 设置请求方式的扩展方法
     */
    fun get(): Param = this.apply { this.method(Method.GET) }
    fun post(): Param = this.apply { this.method(Method.POST) }
    fun delete(): Param = this.apply { this.method(Method.DELETE) }
    fun head(): Param = this.apply { this.method(Method.HEAD) }
    fun patch(): Param = this.apply { this.method(Method.PATCH) }
    fun put(): Param = this.apply { this.method(Method.PUT) }

    companion object {
        fun build(isMultipart: Boolean = false, bodyType: BodyType = BodyType.FORM) =
            Param(isMultipart).bodyType(bodyType)
    }

}