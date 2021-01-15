package com.adazhdw.kthttp

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.callback.RequestJsonCallback
import com.adazhdw.kthttp.entity.Param
import com.adazhdw.kthttp.request.GetRequest
import com.adazhdw.kthttp.request.PostRequest
import okhttp3.Call

/**
 * FileName: KtHttp
 * Author: adazhdw
 * Date: 2021/1/15 15:57
 * Description:
 * History:
 */

fun getRequest(url: String, param: Param) = GetRequest(param.url(url))
fun postRequest(url: String, param: Param) = PostRequest(param.url(url))

/**
 * GET 方式请求
 */
inline fun <reified T : Any> get(
    owner: LifecycleOwner,
    url: String,
    param: Param,
    noinline success: (data: T) -> Unit
) {
    get(owner, url, param, success, failure = {})
}

inline fun <reified T : Any> get(
    owner: LifecycleOwner,
    url: String,
    param: Param,
    noinline success: (data: T) -> Unit,
    noinline failure: (e: Exception) -> Unit
) {
    getRequest(url, param).enqueue(object : RequestJsonCallback<T>(owner) {
        override fun onSuccess(data: T) {
            success.invoke(data)
        }

        override fun onError(e: Exception, call: Call) {
            failure.invoke(e)
        }
    })
}

/**
 * POST 方式请求
 */
inline fun <reified T : Any> post(
    owner: LifecycleOwner,
    url: String,
    param: Param,
    noinline success: (data: T) -> Unit
) {
    post(owner, url, param, success, failure = {})
}

inline fun <reified T : Any> post(
    owner: LifecycleOwner,
    url: String,
    param: Param,
    noinline success: (data: T) -> Unit,
    noinline failure: (e: Exception) -> Unit
) {
    postRequest(url, param).enqueue(object : RequestJsonCallback<T>(owner) {
        override fun onSuccess(data: T) {
            success.invoke(data)
        }

        override fun onError(e: Exception, call: Call) {
            failure.invoke(e)
        }
    })
}
