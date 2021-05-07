package com.adazhdw.kthttp.coroutines

import com.adazhdw.kthttp.coroutines.parser.Parser
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Author: dgz
 * Date: 2020/8/21 14:50
 * Description: okhttp3.Call await 方法
 */

internal suspend fun okhttp3.Call.await(): okhttp3.Response {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        this.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                continuation.resume(response)
            }
        })
    }
}

internal suspend fun <T> okhttp3.Call.await(parser: Parser<T>): T {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        this.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                try {
                    continuation.resume(parser.parse(response))
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
        })
    }
}