package com.adazhdw.kthttp.http

import com.adazhdw.kthttp.internal.HttpMethod
import com.adazhdw.kthttp.util.ExecutorUtils
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.lang.reflect.Type
import java.net.URL
import java.util.concurrent.Executor

/**
 * Retrofit主要功能是简化网络请求的，
 * 它通过接口方法去声明一个网络请求所需要的各种参数，然后通过动态代理去返回网络请求的实体。
 * 并且提供了CallAdapter将请求实体Call转换成我们需要的形式，
 * 以及提供了ConverterAdapter将请求后的结果转换成我们需要的形式。
 */

class Net private constructor(
    val client: okhttp3.OkHttpClient,
    val baseUrl: okhttp3.HttpUrl,
    val callbackExecutor: Executor,
    val workExecutor: Executor,
    val commonHeaders: MutableMap<String, String>,
    val commonParams: MutableMap<String, String>,
    val converterFactories: MutableList<Converter.Factory>,
    val callAdapterFactories: MutableList<CallAdapter.Factory>
) {

    fun requestBuilder(): RequestFactory.Builder {
        return RequestFactory.parseBuilder(this)
    }

    fun get() = requestBuilder().method(HttpMethod.GET)
    fun head() = requestBuilder().method(HttpMethod.HEAD)
    fun delete() = requestBuilder().method(HttpMethod.DELETE)
    fun post() = requestBuilder().method(HttpMethod.POST)
    fun patch() = requestBuilder().method(HttpMethod.PATCH)
    fun put() = requestBuilder().method(HttpMethod.PUT)
    fun options() = requestBuilder().method(HttpMethod.OPTIONS)

    /**
     * @param returnType is 'Call' of Call<Repo>, value of CallAdapter's responseType() function is Repo
     */
    fun callAdapter(returnType: Type): CallAdapter<*, *> {
        return callAdapter(null, returnType)
    }

    fun callAdapter(skipPast: CallAdapter.Factory?, returnType: Type): CallAdapter<*, *> {
        val start: Int = callAdapterFactories.indexOf(skipPast) + 1
        for (i in start until callAdapterFactories.size) {
            val callAdapter: CallAdapter<*, *>? = callAdapterFactories[i].get(returnType, this)
            if (callAdapter != null) {
                return callAdapter
            }
        }

        val builder = StringBuilder("Could not locate call adapter for ").append(returnType).append(".\n")
        if (skipPast != null) {
            builder.append(" Skipped:")
            for (i in 0 until start) {
                builder.append("\n  * ").append(callAdapterFactories[i].javaClass.name)
            }
            builder.append("\n")
        }
        builder.append(" Tried:")
        for (i in start until callAdapterFactories.size) {
            builder.append("\n  * ").append(callAdapterFactories[i].javaClass.name)
        }
        throw IllegalArgumentException(builder.toString())
    }

    fun <T> requestBodyConverter(requestType: Type): Converter<T, okhttp3.RequestBody> {
        return requestBodyConverter(null, requestType)
    }

    fun <T> requestBodyConverter(skipPast: Converter.Factory?, requestType: Type): Converter<T, okhttp3.RequestBody> {
        val start: Int = converterFactories.indexOf(skipPast) + 1
        for (i in start until converterFactories.size) {
            val converter: Converter<*, okhttp3.RequestBody>? = converterFactories[i].requestBodyConverter(requestType, this)
            if (converter != null) {
                return converter as Converter<T, okhttp3.RequestBody>
            }
        }

        val builder = StringBuilder("Could not locate ResponseBody converter for ").append(requestType).append(".\n")
        if (skipPast != null) {
            builder.append(" Skipped:")
            for (i in 0 until start) {
                builder.append("\n  * ").append(converterFactories[i].javaClass.name)
            }
            builder.append("\n")
        }
        builder.append(" Tried:")
        for (i in start until converterFactories.size) {
            builder.append("\n  * ").append(converterFactories[i].javaClass.name)
        }
        throw IllegalArgumentException(builder.toString())
    }

    fun <T> responseBodyConverter(responseType: Type): Converter<okhttp3.ResponseBody, T> {
        return responseBodyConverter(null, responseType)
    }

    fun <T> responseBodyConverter(skipPast: Converter.Factory?, responseType: Type): Converter<okhttp3.ResponseBody, T> {
        val start: Int = converterFactories.indexOf(skipPast) + 1
        for (i in start until converterFactories.size) {
            val converter: Converter<okhttp3.ResponseBody, *>? = converterFactories[i].responseBodyConverter(responseType, this)
            if (converter != null) {
                return converter as Converter<okhttp3.ResponseBody, T>
            }
        }

        val builder = StringBuilder("Could not locate ResponseBody converter for ").append(responseType).append(".\n")
        if (skipPast != null) {
            builder.append(" Skipped:")
            for (i in 0 until start) {
                builder.append("\n  * ").append(converterFactories[i].javaClass.name)
            }
            builder.append("\n")
        }
        builder.append(" Tried:")
        for (i in start until converterFactories.size) {
            builder.append("\n  * ").append(converterFactories[i].javaClass.name)
        }
        throw IllegalArgumentException(builder.toString())
    }

    fun newBuilder(): Builder {
        return Builder(this)
    }

    fun execute(runnable: Runnable, onIO: Boolean) {
        var executor = callbackExecutor
        if (onIO) {
            executor = workExecutor
        }
        executor.execute(runnable)
    }

    class Builder(net: Net? = null) {
        internal var client: okhttp3.OkHttpClient? = null
        internal var baseUrl: okhttp3.HttpUrl? = null
        internal var callbackExecutor: Executor? = null
        internal var workExecutor: Executor? = null
        internal val commonHeaders: MutableMap<String, String>
        internal val commonParams: MutableMap<String, String>
        internal val converterFactories: MutableList<Converter.Factory> = mutableListOf()
        internal val callAdapterFactories: MutableList<CallAdapter.Factory> = mutableListOf()
        private val platform = Platform.PLATFORM

        init {
            if (net != null) {
                this.client = net.client
                this.callbackExecutor = net.callbackExecutor
                this.workExecutor = net.workExecutor
                this.commonHeaders = net.commonHeaders
                this.commonParams = net.commonParams

                for (i in 1 until net.converterFactories.size - platform.defaultConverterFactoriesSize()) {
                    converterFactories.add(net.converterFactories[i])
                }
                for (i in 0 until net.callAdapterFactories.size - platform.defaultCallAdapterFactoriesSize()) {
                    callAdapterFactories.add(net.callAdapterFactories[i])
                }

            } else {
                this.commonHeaders = mutableMapOf()
                this.commonParams = mutableMapOf()
            }
        }

        fun client(client: okhttp3.OkHttpClient) = apply {
            this.client = client
        }

        fun baseUrl(baseUrl: okhttp3.HttpUrl) = apply {
            val pathSegments = baseUrl.pathSegments
            require("" == pathSegments[pathSegments.size - 1]) { "baseUrl must end in /: $baseUrl" }
            this.baseUrl = baseUrl
        }

        fun baseUrl(baseUrl: String) = apply {
            baseUrl(baseUrl.toHttpUrl())
        }

        fun baseUrl(baseUrl: URL) = apply {
            baseUrl(baseUrl.toString().toHttpUrl())
        }

        fun mainExecutor(mainExecutor: Executor) = apply {
            this.callbackExecutor = mainExecutor
        }

        fun workExecutor(workExecutor: Executor) = apply {
            this.workExecutor = workExecutor
        }

        fun commonHeaders(headers: Map<String, String>) = apply {
            if (headers.isNotEmpty()) {
                this.commonHeaders.putAll(headers)
            }
        }

        fun commonHeaders(name: String, value: String) = apply {
            if (name.isNotEmpty()) {
                this.commonHeaders[name] = value
            }
        }

        fun commonParams(params: Map<String, String>) = apply {
            if (params.isNotEmpty()) {
                this.commonParams.putAll(params)
            }
        }

        fun commonParams(name: String, value: String) = apply {
            if (name.isNotEmpty()) {
                this.commonParams[name] = value
            }
        }

        fun addConverterFactory(factory: Converter.Factory) = apply {
            this.converterFactories.add(factory)
        }

        fun addCallAdapterFactory(factory: CallAdapter.Factory) = apply {
            this.callAdapterFactories.add(factory)
        }

        fun build(): Net {
            if (baseUrl == null) {
                throw IllegalArgumentException("$baseUrl must not be null")
            }
            val builder = client?.newBuilder() ?: okhttp3.OkHttpClient.Builder()
            val client = builder.build().also { this.client = it }

            val commonHeaders = mutableMapOf<String, String>().apply { putAll(commonHeaders) }
            val commonParams = mutableMapOf<String, String>().apply { putAll(commonParams) }

            val callbackExecutor = this.callbackExecutor ?: platform.defaultCallbackExecutor() ?: ExecutorUtils.mainThread
            val workExecutor = this.workExecutor ?: ExecutorUtils.networkExecutor

            val callAdapterFactories = callAdapterFactories.toMutableList()
            callAdapterFactories.addAll(platform.defaultCallAdapterFactories(callbackExecutor))

            val converterFactories = ArrayList<Converter.Factory>(1 + this.converterFactories.size + platform.defaultConverterFactoriesSize())
            converterFactories.add(InternalConverters())
            converterFactories.addAll(this.converterFactories)
            converterFactories.addAll(platform.defaultConverterFactories())


            return Net(client, baseUrl!!, callbackExecutor, workExecutor, commonHeaders, commonParams, converterFactories, callAdapterFactories)
        }
    }
}