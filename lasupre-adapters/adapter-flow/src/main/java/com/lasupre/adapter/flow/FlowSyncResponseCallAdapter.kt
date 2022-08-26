package com.lasupre.adapter.flow

import com.adazhdw.lasupre.Call
import com.adazhdw.lasupre.CallAdapter
import com.adazhdw.lasupre.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class FlowResponseCallAdapter<R>(private val responseType: Type) : CallAdapter<R, Flow<Response<R>>> {
    override fun adapt(call: Call<R>): Flow<Response<R>> {
        return responseFlow(call)
    }

    override fun responseType(): Type {
        return responseType
    }

}


fun <R> responseFlow(call: Call<R>): Flow<Response<R>> {
    return flow {
        suspendCancellableCoroutine<Response<R>> { continuation ->
            continuation.invokeOnCancellation {
                call.cancel()
            }
            try {
                val response = call.execute()
                continuation.resume(response)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }.let {
            emit(it)
        }
    }
}
