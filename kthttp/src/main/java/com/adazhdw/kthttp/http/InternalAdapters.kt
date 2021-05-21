package com.adazhdw.kthttp.http

import com.adazhdw.kthttp.internal.HttpMethod
import java.lang.reflect.Type


abstract class InternalAdapters<ResponseT, ReturnT>(
    private val requestFactory: RequestFactory,
    private val callFactory: okhttp3.Call.Factory,
    private val responseConverter: Converter<okhttp3.ResponseBody, ResponseT>
) {

    companion object {

        @JvmStatic
        fun <ResponseT, ReturnT> parse(returnType: Type, net: Net, requestFactory: RequestFactory): ReturnT {
            val callAdapter = createCallAdapter<ResponseT, ReturnT>(net, returnType)
            val responseType = callAdapter.responseType()
            if (responseType === okhttp3.Response::class.java) {
                throw IllegalArgumentException("$responseType is not a valid response body type")
            }
            if (responseType === Response::class.java) {
                throw IllegalArgumentException("Response must include generic type (e.g., Response<String>)")
            }
            if (requestFactory.method is HttpMethod.HEAD && (Void::class.java != responseType || Unit::class.java != responseType)) {
                throw IllegalArgumentException("HEAD method must use Void as response type.")
            }

            val responseConverter = createResponseConverter<ResponseT>(net, responseType)

            val callFactory = net.client

            return CallAdapted<ResponseT, ReturnT>(requestFactory, callFactory, responseConverter, callAdapter).internalAdapter()
        }

        @JvmStatic
        private fun <ResponseT> createResponseConverter(net: Net, responseType: Type): Converter<okhttp3.ResponseBody, ResponseT> {
            try {
                return net.responseBodyConverter<ResponseT>(responseType)
            } catch (e: RuntimeException) {
                throw IllegalArgumentException("Unable to create converter for responseType:$responseType")
            }
        }

        @JvmStatic
        fun <ResponseT, ReturnT> createCallAdapter(net: Net, returnType: Type): CallAdapter<ResponseT, ReturnT> {
            try {
                return net.callAdapter(returnType) as CallAdapter<ResponseT, ReturnT>
            } catch (e: RuntimeException) {
                throw IllegalArgumentException("Unable to create call adapter for returnType:$returnType")
            }
        }

    }

    internal fun internalAdapter(): ReturnT {
        val call: Call<ResponseT> = OkHttpCall<ResponseT>(requestFactory, callFactory, responseConverter)
        return adapt(call)
    }

    abstract fun adapt(call: Call<ResponseT>): ReturnT

    class CallAdapted<ResponseT, ReturnT>(
        requestFactory: RequestFactory,
        callFactory: okhttp3.Call.Factory,
        responseConverter: Converter<okhttp3.ResponseBody, ResponseT>,
        private val callAdapter: CallAdapter<ResponseT, ReturnT>
    ) : InternalAdapters<ResponseT, ReturnT>(requestFactory, callFactory, responseConverter) {
        override fun adapt(call: Call<ResponseT>): ReturnT {
            return callAdapter.adapt(call)
        }
    }

}