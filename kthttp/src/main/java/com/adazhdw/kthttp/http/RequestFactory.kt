package com.adazhdw.kthttp.http

import com.adazhdw.kthttp.internal.HttpMethod
import com.adazhdw.kthttp.internal.TypeRef
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.ByteString
import java.io.File
import java.io.IOException
import java.util.regex.Pattern


class RequestFactory(val builder: Builder) {

    companion object {
        fun parseBuilder(net: Net): Builder {
            return Builder(net)
        }
    }

    internal val method: HttpMethod = builder.method
    private val baseUrl: okhttp3.HttpUrl = builder.net.baseUrl
    internal val urlPath: String? = builder.urlPath
    private val hasBody = builder.hasBody
    private val isFormEncoded = builder.isFormEncoded
    private val isMultipart = builder.isMultipart
    internal val headersBuilder: okhttp3.Headers.Builder = builder.headersBuilder
    internal val contentType: okhttp3.MediaType? = builder.contentType

    inline fun <reified T> parseCall(): Call<T> {
        return InternalAdapters.parse<T, Call<T>>(object : TypeRef<Call<T>>() {}.type, builder.net, this)
    }

    inline fun <reified T, reified R> parseCall(typeRef: TypeRef<R>): R {
        return InternalAdapters.parse<T, R>(typeRef.type, builder.net, this)
    }

    @Throws(IOException::class)
    fun create(): okhttp3.Request {
        val headers = headersBuilder.build()
        val requestBuilder = RequestBuilder(method.name, baseUrl, urlPath, headers, contentType, hasBody, isFormEncoded, isMultipart)
        requestBuilder.addPathParam(builder.pathParams)
        for (param in builder.queryParams) {
            requestBuilder.addQueryParam(param.name, param.value, param.encoded)
        }
        if (isFormEncoded) {
            for (param in builder.formFields) {
                requestBuilder.addFormField(param.name, param.value, param.encoded)
            }
        } else if (isMultipart) {
            for (param in builder.partParams) {
                requestBuilder.addPart(
                    okhttp3.Headers.headersOf(
                        "Content-Disposition", "form-data; name=\"" + param.name + "\"",
                        "Content-Transfer-Encoding", param.encoding
                    ), param.value
                )
            }
        }
        builder.requestBody?.let { body ->
            requestBuilder.setBody(body)
        }
        builder.mTag.let { tag ->
            requestBuilder.addTag(tag)
        }
        return requestBuilder.get().build()
    }

    class Builder(val net: Net) {
        // Upper and lower characters, digits, underscores, and hyphens, starting with a character.
        companion object {
            private const val PARAM = "[a-zA-Z][a-zA-Z0-9_-]*"
            private val PARAM_URL_REGEX = Pattern.compile("\\{($PARAM)\\}")
            private val PARAM_NAME_REGEX = Pattern.compile(PARAM)
        }

        internal var gotField = false
        internal var gotPart = false
        internal var gotBody = false
        internal var gotPath = false
        internal var gotQuery = false
        internal var gotUrl = false
        internal var method: HttpMethod = HttpMethod.GET
        internal var hasBody = false
        internal var urlPath: String? = null
        internal var isFormEncoded = false
        internal var isMultipart = false
        internal val headersBuilder = okhttp3.Headers.Builder()
        internal val queryParams = mutableListOf<ParamField>()
        internal val pathParams = mutableMapOf<String, String>()
        internal val formFields = mutableListOf<ParamField>()
        internal val partParams = mutableListOf<PartField>()
        internal var contentType: okhttp3.MediaType? = null
        internal var requestBody: okhttp3.RequestBody? = null
        internal var mTag: Any? = null

        fun method(method: HttpMethod) = apply {
            this.method = method
            this.hasBody = method.hasBody
        }

        fun urlPath(urlPath: String) = apply {
            this.urlPath = urlPath
            this.gotUrl = true
        }

        fun headers(headers: okhttp3.Headers) = apply {
            this.headersBuilder.addAll(headers)
        }

        fun headers(name: String, value: String) = apply {
            this.headersBuilder.add(name, value)
        }

        fun headers(headerMap: Map<String, String>) = apply {
            for ((name, value) in headerMap) {
                headers(name, value)
            }
        }

        fun pathParams(name: String, value: String) = apply {
            this.pathParams[name] = value
            if (!this.gotPath) {
                this.gotPath = true
            }
        }

        fun pathParams(params: Map<String, String>) = apply {
            this.pathParams.putAll(params)
            if (!this.gotPath) {
                this.gotPath = true
            }
        }

        fun queryParams(name: String, value: String, encoded: Boolean = false) = apply {
            queryParams(ParamField(name, value, encoded))
        }

        fun queryParams(paramField: ParamField) = apply {
            this.queryParams.add(paramField)
            if (!this.gotQuery) {
                this.gotQuery = true
            }
        }

        fun isMultipart() = apply {
            require(isFormEncoded) { "isFormEncoded() or isMultipart() only can be called one of them" }
            isMultipart = true
        }

        fun isFormEncoded() = apply {
            require(isMultipart) { "isFormEncoded() or isMultipart() only can be called one of them" }
            isFormEncoded = true
        }

        fun formFields(paramField: ParamField) = apply {
            if (!this.gotField) {
                this.gotField = true
            }
            this.formFields.add(paramField)
        }

        fun formFields(name: String, value: String, encoded: Boolean) = apply {
            formFields(ParamField(name, value, encoded))
        }

        fun formFields(name: String, value: String) = apply {
            formFields(ParamField(name, value, false))
        }

        fun partFileMap(params: Map<String, File>) = apply {
            for ((name, value) in params) {
                partParams(name, value)
            }
        }

        fun partParams(name: String, value: File, encoding: String = "binary", contentType: okhttp3.MediaType? = null) = apply {
            partParams(PartField(name, value.asRequestBody(contentType), encoding))
        }

        fun partStringMap(params: Map<String, String>) = apply {
            for ((name, value) in params) {
                partParams(name, value)
            }
        }

        fun partParams(name: String, value: String, encoding: String = "binary", contentType: okhttp3.MediaType? = null) = apply {
            partParams(PartField(name, value.toRequestBody(contentType), encoding))
        }

        fun partByteArrayMap(params: Map<String, ByteArray>) = apply {
            for ((name, value) in params) {
                partParams(name, value)
            }
        }

        fun partParams(name: String, value: ByteArray, encoding: String = "binary", contentType: okhttp3.MediaType? = null) = apply {
            partParams(PartField(name, value.toRequestBody(contentType), encoding))
        }

        fun partByteStringMap(params: Map<String, ByteString>) = apply {
            for ((name, value) in params) {
                partParams(name, value)
            }
        }

        fun partParams(name: String, value: ByteString, encoding: String = "binary", contentType: okhttp3.MediaType? = null) = apply {
            partParams(PartField(name, value.toRequestBody(contentType), encoding))
        }

        fun partParams(partField: PartField) = apply {
            if (!this.gotPart) {
                this.gotPart = true
            }
            this.partParams.add(partField)
        }

        fun requestBody(value: File, contentType: okhttp3.MediaType? = null) = apply {
            requestBody(value.asRequestBody(contentType))
        }

        fun requestBody(value: String, contentType: okhttp3.MediaType? = null) = apply {
            requestBody(value.toRequestBody(contentType))
        }

        fun requestBody(value: ByteArray, contentType: okhttp3.MediaType? = null) = apply {
            requestBody(value.toRequestBody(contentType))
        }

        fun requestBody(value: ByteString, contentType: okhttp3.MediaType? = null) = apply {
            requestBody(value.toRequestBody(contentType))
        }

        inline fun <reified T : Any> requestBody(value: T) = apply {
            val requestBodyConverter = net.requestBodyConverter<T>(object : TypeRef<T>() {}.type)
            requestBody(requestBodyConverter.convert(value))
        }

        fun requestBody(requestBody: okhttp3.RequestBody?) = apply {
            this.requestBody = requestBody
            if (!this.gotBody) {
                this.gotBody = true
            }
        }

        fun tag(tag: Any) = apply {
            this.mTag = tag
        }

        fun build(): RequestFactory {
            if (!hasBody) {
                require(!isMultipart) { "Multipart can only be specified on HTTP methods with request body" }
                require(!isFormEncoded) { "isFormEncoded can only be specified on HTTP methods with request body" }
            }
            if (urlPath.isNullOrBlank() || !gotUrl) {
                throw IllegalArgumentException("urlPath must not be null")
            }

            if (!isFormEncoded && !isMultipart && !hasBody && gotBody) {
                throw IllegalArgumentException("Non-body HTTP method cannot contain requestBody:$requestBody")
            }

            if (isFormEncoded && !gotField) {
                throw IllegalArgumentException("Form-encoded method must contain at least one FieldParam")
            }

            if (isMultipart && !gotPart) {
                throw IllegalArgumentException("Form-encoded method must contain at least one FieldParam")
            }

            return RequestFactory(this)
        }

        inline fun <reified T> parseCall(): Call<T> {
            return build().parseCall<T>()
        }

        inline fun <reified T, reified R> parseObject(): R {
            return build().parseCall<T, R>(object : TypeRef<R>() {})
        }

    }
}