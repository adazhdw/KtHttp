package com.lasupre.adapter.flow

import com.adazhdw.lasupre.CallAdapter
import com.adazhdw.lasupre.Lasupre
import com.adazhdw.lasupre.Response
import com.adazhdw.lasupre.TypeUtils
import com.lasupre.adapter.flow.FlowAsyncBodyCallAdapter
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class FlowCallAdapterFactory private constructor(private val isAsync: Boolean) : CallAdapter.Factory() {

    companion object {
        @JvmStatic
        fun create(isAsync: Boolean) = FlowCallAdapterFactory(isAsync)
    }

    override fun get(returnType: Type, lasupre: Lasupre): CallAdapter<*, *>? {
        if (Flow::class.java != TypeUtils.getRawType(returnType)) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalStateException("Flow return type must be parameterized as Flow<Foo> or Flow<? extends Foo>")
        }
        val responseType = TypeUtils.getParameterUpperBound(0, returnType)

        val rawType = TypeUtils.getRawType(responseType)
        return if (rawType == Response::class.java) {
            if (responseType !is ParameterizedType) {
                throw IllegalStateException("Response must be parameterized as Response<Foo> or Response<out Foo>")
            }
            if (isAsync) {
                FlowAsyncResponseCallAdapter<Any>(TypeUtils.getParameterUpperBound(0, responseType))
            } else {
                FlowResponseCallAdapter<Any>(TypeUtils.getParameterUpperBound(0, responseType))
            }
        } else {
            if (isAsync) {
                FlowAsyncBodyCallAdapter<Any>(responseType)
            } else {
                FlowBodyCallAdapter<Any>(responseType)
            }
        }
    }


}