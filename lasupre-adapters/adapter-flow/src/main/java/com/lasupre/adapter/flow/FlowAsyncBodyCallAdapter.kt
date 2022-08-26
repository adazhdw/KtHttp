package com.lasupre.adapter.flow

import com.adazhdw.lasupre.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class FlowAsyncBodyCallAdapter<R>(private val responseBodyType: Type) : CallAdapter<R, Flow<R>> {
    override fun adapt(call: Call<R>): Flow<R> {
        return bodyFlowAsync(call)
    }

    override fun responseType(): Type {
        return responseBodyType
    }

}

fun <R> bodyFlowAsync(call: Call<R>): Flow<R> {
    return flow {
        suspendCancellableCoroutine<R> { continuation ->
            continuation.invokeOnCancellation {
                call.cancel()
            }
            try {
                call.enqueue(object : Callback<R> {
                    override fun onFailure(call: Call<R>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }

                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        if (response.isSuccessful) {
                            continuation.resume(response.body!!)
                        } else {
                            continuation.resumeWithException(HttpException(response))
                        }
                    }
                })
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }.let {
            emit(it)
        }
    }
}
