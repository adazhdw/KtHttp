package com.adazhdw.kthttp.internal

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.Timeout

/**
 * author：daguozhu
 * date-time：2020/11/17 19:50
 * description：
 **/
class HttpCallProxy(val call: Call) {

    fun request(): Request {
        return call.request()
    }

    fun enqueue(responseCallback: Callback) {
        call.enqueue(responseCallback)
    }

    fun execute(): Response {
        return call.execute()
    }

    fun cancel() {
        call.cancel()
    }

    fun clone(): Call {
        return call.clone()
    }

    fun isCanceled(): Boolean {
        return call.isCanceled()
    }

    fun isExecuted(): Boolean {
        return call.isExecuted()
    }

    fun timeout(): Timeout {
        return call.timeout()
    }
}