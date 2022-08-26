package com.lasupre.adapter.flow

import com.adazhdw.lasupre.Call
import com.adazhdw.lasupre.CallAdapter
import com.adazhdw.lasupre.HttpException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class FlowBodyCallAdapter<R>(private val responseBodyType: Type) : CallAdapter<R, Flow<R>> {
    override fun adapt(call: Call<R>): Flow<R> {
        return bodyFlow(call)
    }

    override fun responseType(): Type {
        return responseBodyType
    }

}

fun <R> bodyFlow(call: Call<R>): Flow<R> {
    return flow {
        suspendCancellableCoroutine<R> { continuation ->
            continuation.invokeOnCancellation {
                call.cancel()
            }
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    continuation.resume(response.body!!)
                } else {
                    continuation.resumeWithException(HttpException(response))
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }.let {
            emit(it)
        }
    }
}
