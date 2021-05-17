package com.adazhdw.kthttp

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.internal.callback.RequestJsonCallback
import com.adazhdw.kthttp.internal.HttpRequest
import com.adazhdw.kthttp.internal.TypeRef
import okhttp3.Call
import java.io.IOException

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
fun getRequest(block: HttpRequest.() -> Unit): HttpRequest {
    return Https.request().get().apply { block.invoke(this) }
}

/**
 * POST 请求方式
 */
fun postRequest(block: HttpRequest.() -> Unit): HttpRequest {
    return Https.request().post().apply { block.invoke(this) }
}

/**
 * HEAD 请求
 */
fun headRequest(block: HttpRequest.() -> Unit): HttpRequest {
    return Https.request().head().apply { block.invoke(this) }
}

/**
 * DELETE 请求
 */
fun deleteRequest(block: HttpRequest.() -> Unit): HttpRequest {
    return Https.request().delete().apply { block.invoke(this) }
}

/**
 * PUT 请求
 */
fun putRequest(block: HttpRequest.() -> Unit): HttpRequest {
    return Https.request().put().apply { block.invoke(this) }
}

/**
 * PATCH 请求
 */
fun patchRequest(block: HttpRequest.() -> Unit): HttpRequest {
    return Https.request().patch().apply { block.invoke(this) }
}


inline fun <reified T : Any> HttpRequest.sync(
    noinline success: (data: T) -> Unit
) = this.sync(success, failure = { e -> })

inline fun <reified T : Any> HttpRequest.sync(
    noinline success: (data: T) -> Unit,
    noinline failure: (e: Exception) -> Unit
) {
    try {
        val response = this.execute()
        if (response.isSuccessful) {
            success.invoke(response.toBean(object : TypeRef<T>() {}))
        } else {
            failure.invoke(Exception(response.message))
        }
    } catch (e: IOException) {
        failure.invoke(e)
    }
}


inline fun <reified T : Any> HttpRequest.async(
    lifecycleOwner: LifecycleOwner?,
    noinline success: (data: T) -> Unit
) = this.async(lifecycleOwner, success, failure = { e, call -> })

inline fun <reified T : Any> HttpRequest.async(
    lifecycleOwner: LifecycleOwner?,
    noinline success: (data: T) -> Unit,
    noinline failure: (e: Exception, call: Call) -> Unit
) = this.apply {
    this.enqueue(object : RequestJsonCallback<T>(lifecycleOwner) {
        override fun onSuccess(data: T) {
            success.invoke(data)
        }

        override fun onError(e: Exception, call: Call) {
            failure.invoke(e, call)
        }
    })
}





