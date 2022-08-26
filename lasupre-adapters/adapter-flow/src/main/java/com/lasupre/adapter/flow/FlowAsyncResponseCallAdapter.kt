package com.lasupre.adapter.flow

import com.adazhdw.lasupre.Call
import com.adazhdw.lasupre.CallAdapter
import com.adazhdw.lasupre.Callback
import com.adazhdw.lasupre.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.reflect.Type
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class FlowAsyncResponseCallAdapter<R>(private val responseType: Type) : CallAdapter<R, Flow<Response<R>>> {
    override fun adapt(call: Call<R>): Flow<Response<R>> {
        return responseAsyncFlow(call)
    }

    override fun responseType(): Type {
        return responseType
    }

}


fun <R> responseAsyncFlow(call: Call<R>): Flow<Response<R>> {
    return flow {
        suspendCancellableCoroutine<Response<R>> { continuation ->
            continuation.invokeOnCancellation {
                call.cancel()
            }
            try {
                call.enqueue(object :Callback<R>{
                    override fun onFailure(call: Call<R>, t: Throwable) {
                        continuation.resumeWithException(t)
                    }

                    override fun onResponse(call: Call<R>, response: Response<R>) {
                        continuation.resume(response)
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
