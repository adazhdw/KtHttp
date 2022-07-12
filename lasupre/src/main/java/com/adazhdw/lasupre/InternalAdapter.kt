package com.adazhdw.lasupre

import java.lang.reflect.Type


abstract class InternalAdapter<ResponseT, ReturnT>(
    private val requestFactory: RequestFactory,
    private val callFactory: okhttp3.Call.Factory,
    private val responseConverter: Converter<okhttp3.ResponseBody, ResponseT>
) {

    internal fun adapt(): ReturnT {
        val call: Call<ResponseT> = OkHttpCall<ResponseT>(requestFactory, callFactory, responseConverter)
        return adapt(call)
    }

    abstract fun adapt(call: Call<ResponseT>): ReturnT

    class InternalCallAdapter<ResponseT, ReturnT>(
        requestFactory: RequestFactory,
        callFactory: okhttp3.Call.Factory,
        responseConverter: Converter<okhttp3.ResponseBody, ResponseT>,
        private val callAdapter: CallAdapter<ResponseT, ReturnT>
    ) : InternalAdapter<ResponseT, ReturnT>(requestFactory, callFactory, responseConverter) {
        override fun adapt(call: Call<ResponseT>): ReturnT {
            return callAdapter.adapt(call)
        }
    }

    companion object {

        fun <ResponseT, ReturnT> parse(typeRef: TypeRef<ReturnT>, lasupre: Lasupre, requestFactory: RequestFactory): ReturnT {
            val callAdapter = createCallAdapter<ResponseT, ReturnT>(lasupre, typeRef.type)
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

            val responseConverter = createResponseBodyConverter<ResponseT>(lasupre, responseType, requestFactory)
            val callFactory = lasupre.client

            return InternalCallAdapter<ResponseT, ReturnT>(requestFactory, callFactory, responseConverter, callAdapter).adapt()
        }

        @JvmStatic
        private fun <ResponseT> createResponseBodyConverter(
            lasupre: Lasupre,
            responseType: Type,
            requestFactory: RequestFactory
        ): Converter<okhttp3.ResponseBody, ResponseT> {
            try {
                return lasupre.responseBodyConverter<ResponseT>(responseType, requestFactory)
            } catch (e: RuntimeException) {
                throw IllegalArgumentException("Unable to create converter for responseType:$responseType")
            }
        }

        @JvmStatic
        fun <ResponseT, ReturnT> createCallAdapter(lasupre: Lasupre, returnType: Type): CallAdapter<ResponseT, ReturnT> {
            try {
                return lasupre.callAdapter(returnType) as CallAdapter<ResponseT, ReturnT>
            } catch (e: RuntimeException) {
                throw IllegalArgumentException("Unable to create call adapter for returnType:$returnType")
            }
        }

    }

}