package com.adazhdw.kthttp.ext

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.callback.RequestJsonCallback
import com.adazhdw.kthttp.request.HttpRequest
import okhttp3.Call

/**
 * FileName: DSL
 * Author: adazhdw
 * Date: 2021/1/12 19:46
 * Description: HttpRequest Extension方法
 * History: 1、first init
 */

/**
 * 默认请求方法为 GET
 */
fun httpRequest(isMultipart: Boolean = false, block: HttpRequest.() -> Unit): HttpRequest {
    return HttpRequest(isMultipart = isMultipart).apply { block.invoke(this) }
}

/**
 * POST 请求方式
 */
fun postRequest(isMultipart: Boolean = false, block: HttpRequest.() -> Unit): HttpRequest {
    return HttpRequest(isMultipart = isMultipart).post().apply { block.invoke(this) }
}

inline fun <reified T : Any> HttpRequest.execute(
    lifecycleOwner: LifecycleOwner,
    noinline success: (data: T) -> Unit
) = this.execute(lifecycleOwner, success, failed = { e, call -> })

inline fun <reified T : Any> HttpRequest.execute(
    lifecycleOwner: LifecycleOwner,
    noinline success: (data: T) -> Unit,
    noinline failed: (e: Exception, call: Call) -> Unit
) = this.apply {
    this.enqueue(object : RequestJsonCallback<T>(lifecycleOwner) {
        override fun onSuccess(data: T) {
            success.invoke(data)
        }

        override fun onError(e: Exception, call: Call) {
            failed.invoke(e, call)
        }
    })
}





