package com.adazhdw.kthttp

import com.adazhdw.kthttp.constant.BodyType
import com.adazhdw.kthttp.converter.GsonConverter
import com.adazhdw.kthttp.converter.IConverter
import com.adazhdw.kthttp.internal.HttpRequest
import com.adazhdw.kthttp.util.KtExecutors
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.nio.charset.Charset
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


class HttpClient private constructor(builder: Builder) {

    fun request(): HttpRequest = HttpRequest(this)

    internal var client: OkHttpClient = builder.client!!
    internal var writeTimeout: Long = builder.writeTimeout
    internal var connectTimeout: Long = builder.connectTimeout
    internal var readTimeout: Long = builder.readTimeout
    internal var mainExecutor: Executor = builder.mainExecutor ?: KtExecutors.mainThread
    internal var resultConverter: IConverter = builder.resultConverter
    internal var bodyType: BodyType = builder.bodyType
    internal var charset: Charset = builder.charset
    internal val commonHeaders: MutableMap<String, String> = builder.commonHeaders
    internal val commonParams: MutableMap<String, String> = builder.commonParams

    interface OkHttpConfig {
        fun config(builder: OkHttpClient.Builder)
    }

    class Builder(httpClient: HttpClient? = null) {
        internal var client: OkHttpClient? = null
        internal var config: OkHttpConfig? = null
        internal var writeTimeout: Long = 10L
        internal var connectTimeout: Long = 10L
        internal var readTimeout: Long = 10L
        internal val interceptors: MutableList<Interceptor> = mutableListOf()
        internal val networkInterceptors: MutableList<Interceptor> = mutableListOf()
        internal var mainExecutor: Executor? = null
        internal var resultConverter: IConverter = GsonConverter.create()
        internal var bodyType: BodyType = BodyType.FORM
        internal var charset: Charset = Charsets.UTF_8
        internal val commonHeaders: MutableMap<String, String>
        internal val commonParams: MutableMap<String, String>

        init {
            if (httpClient != null) {
                this.client = httpClient.client
                this.writeTimeout = httpClient.writeTimeout
                this.connectTimeout = httpClient.connectTimeout
                this.readTimeout = httpClient.readTimeout
                this.mainExecutor = httpClient.mainExecutor
                this.bodyType = httpClient.bodyType
                this.charset = httpClient.charset
                this.commonHeaders = httpClient.commonHeaders
                this.commonParams = httpClient.commonParams
            } else {
                this.commonHeaders = mutableMapOf()
                this.commonParams = mutableMapOf()
            }
        }

        fun client(client: OkHttpClient) = apply {
            this.client = client
        }

        fun config(config: OkHttpConfig) = apply {
            this.config = config
        }

        fun writeTimeout(writeTimeout: Long) = apply {
            this.writeTimeout = writeTimeout
        }

        fun connectTimeout(connectTimeout: Long) = apply {
            this.connectTimeout = connectTimeout
        }

        fun readTimeout(readTimeout: Long) = apply {
            this.readTimeout = readTimeout
        }

        fun interceptor(interceptor: Interceptor) = apply {
            this.interceptors.add(interceptor)
        }

        fun networkInterceptor(interceptor: Interceptor) = apply {
            this.networkInterceptors.add(interceptor)
        }

        fun mainExecutor(mainExecutor: Executor) = apply {
            this.mainExecutor = mainExecutor
        }

        fun bodyType(bodyType: BodyType) = apply {
            this.bodyType = bodyType
        }

        fun resultConverter(resultConverter: IConverter) = apply {
            this.resultConverter = resultConverter
        }

        fun charset(charset: Charset) = apply {
            this.charset = charset
        }

        fun commonHeaders(headers: Map<String, String>) = apply {
            if (headers.isNotEmpty()) {
                this.commonHeaders.putAll(headers)
            }
        }

        fun commonHeaders(name: String, value: String) = apply {
            if (name.isNotEmpty() && value.isNotBlank()) {
                this.commonHeaders[name] = value
            }
        }

        fun commonParams(params: Map<String, String>) = apply {
            if (params.isNotEmpty()) {
                this.commonParams.putAll(params)
            }
        }

        fun commonParams(name: String, value: String) = apply {
            if (name.isNotEmpty() && value.isNotBlank()) {
                this.commonParams[name] = value
            }
        }

        fun build(): HttpClient {
            val builder = client?.let {
                it.newBuilder().apply {
                    config?.config(this)
                }
            } ?: OkHttpClient.Builder()
            builder.connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            for (interceptor in interceptors) {
                builder.addInterceptor(interceptor)
            }
            for (interceptor in networkInterceptors) {
                builder.addInterceptor(interceptor)
            }
            client = builder.build()
            return HttpClient(this)
        }
    }
}