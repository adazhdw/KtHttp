package com.adazhdw.kthttp

import com.adazhdw.kthttp.converter.Converter
import com.adazhdw.kthttp.converter.GsonConverter
import com.adazhdw.kthttp.internal.HttpBodyType
import com.adazhdw.kthttp.internal.HttpRequest
import com.adazhdw.kthttp.util.ExecutorUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.nio.charset.Charset
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


class HttpClient private constructor(builder: Builder) {

    fun request(): HttpRequest = HttpRequest(this)

    fun newBuilder():Builder{
        return Builder(this)
    }

    internal var client: OkHttpClient = builder.client!!
    internal var writeTimeout: HttpRequest.TimeoutHolder = builder.writeTimeout
    internal var connectTimeout: HttpRequest.TimeoutHolder = builder.connectTimeout
    internal var readTimeout: HttpRequest.TimeoutHolder = builder.readTimeout
    internal var mainExecutor: Executor = builder.mainExecutor ?: ExecutorUtils.mainThread
    internal var resultConverter: Converter = builder.resultConverter
    internal var bodyType: HttpBodyType = builder.bodyType
    internal var charset: Charset = builder.charset
    internal val commonHeaders: MutableMap<String, String> = builder.commonHeaders
    internal val commonParams: MutableMap<String, String> = builder.commonParams

    interface OkHttpConfig {
        fun config(builder: OkHttpClient.Builder)
    }

    class Builder(httpClient: HttpClient? = null) {
        internal var client: OkHttpClient? = null
        internal var config: OkHttpConfig? = null
        internal var writeTimeout = HttpRequest.TimeoutHolder(10)
        internal var connectTimeout = HttpRequest.TimeoutHolder(10)
        internal var readTimeout = HttpRequest.TimeoutHolder(10)
        internal val interceptors: MutableList<Interceptor> = mutableListOf()
        internal val networkInterceptors: MutableList<Interceptor> = mutableListOf()
        internal var mainExecutor: Executor? = null
        internal var resultConverter: Converter = GsonConverter.create()
        internal var bodyType: HttpBodyType = HttpBodyType.FORM
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

        fun connectTimeout(connectTimeout: Long) = connectTimeout(connectTimeout, TimeUnit.SECONDS)

        fun connectTimeout(connectTimeout: Long, timeUnit: TimeUnit) = apply {
            this.connectTimeout = HttpRequest.TimeoutHolder(connectTimeout, timeUnit)
        }

        fun readTimeout(readTimeout: Long) = readTimeout(readTimeout, TimeUnit.SECONDS)

        fun readTimeout(readTimeout: Long, timeUnit: TimeUnit) = apply {
            this.readTimeout = HttpRequest.TimeoutHolder(readTimeout, timeUnit)
        }

        fun writeTimeout(writeTimeout: Long) = writeTimeout(writeTimeout, TimeUnit.SECONDS)

        fun writeTimeout(writeTimeout: Long, timeUnit: TimeUnit) = apply {
            this.writeTimeout = HttpRequest.TimeoutHolder(writeTimeout, timeUnit)
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

        fun bodyType(bodyType: HttpBodyType) = apply {
            this.bodyType = bodyType
        }

        fun resultConverter(resultConverter: Converter) = apply {
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
            builder.connectTimeout(connectTimeout.timeOut, connectTimeout.timeUnit)
                .readTimeout(readTimeout.timeOut, readTimeout.timeUnit)
                .writeTimeout(writeTimeout.timeOut, writeTimeout.timeUnit)
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