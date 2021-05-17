package com.adazhdw.kthttp

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.callback.RequestJsonCallback
import com.adazhdw.kthttp.internal.HttpRequest
import com.adazhdw.kthttp.internal.TypeRef
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
fun httpRequest(block: HttpRequest.() -> Unit): HttpRequest {
    return Https.request().get().apply { block.invoke(this) }
}

/**
 * POST 请求方式
 */
fun postRequest(block: HttpRequest.() -> Unit): HttpRequest {
    return Https.request().post().apply { block.invoke(this) }
}


inline fun <reified T : Any> HttpRequest.execute(
    noinline success: (data: T) -> Unit
) = this.execute(success, failure = { e -> })

inline fun <reified T : Any> HttpRequest.execute(
    noinline success: (data: T) -> Unit,
    noinline failure: (e: Exception) -> Unit
) {
    val response = this.executeRequest()
    if (response.isSuccessful) {
        success.invoke(response.toBean(object : TypeRef<T>() {}))
    } else {
        failure.invoke(Exception(response.message))
    }
}


inline fun <reified T : Any> HttpRequest.enqueue(
    lifecycleOwner: LifecycleOwner?,
    noinline success: (data: T) -> Unit
) = this.enqueue(lifecycleOwner, success, failure = { e, call -> })

inline fun <reified T : Any> HttpRequest.enqueue(
    lifecycleOwner: LifecycleOwner?,
    noinline success: (data: T) -> Unit,
    noinline failure: (e: Exception, call: Call) -> Unit
) = this.apply {
    this.enqueueRequest(object : RequestJsonCallback<T>(lifecycleOwner) {
        override fun onSuccess(data: T) {
            success.invoke(data)
        }

        override fun onError(e: Exception, call: Call) {
            failure.invoke(e, call)
        }
    })
}





