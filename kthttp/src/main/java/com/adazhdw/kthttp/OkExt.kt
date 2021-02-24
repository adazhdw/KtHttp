package com.adazhdw.kthttp

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.callback.RequestJsonCallback
import com.adazhdw.kthttp.request.HttpRequest
import okhttp3.Call

/**
 * author：daguozhu
 * date-time：2020/11/16 15:05
 * description：
 **/
object OkExt {

    fun getRequest(url: String) = HttpRequest().get().url(url)
    fun postRequest(url: String) = HttpRequest().post().url(url)
    fun headRequest(url: String) = HttpRequest().head().url(url)
    fun deleteRequest(url: String) = HttpRequest().delete().url(url)
    fun putRequest(url: String) = HttpRequest().put().url(url)
    fun patchRequest(url: String) = HttpRequest().patch().url(url)

    /**
     * GET 方式请求
     */
    inline fun <reified T : Any> get(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        get(owner, url, success, failure = {})
    }

    inline fun <reified T : Any> get(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception) -> Unit
    ) {
        getRequest(url).enqueue(object : RequestJsonCallback<T>(owner) {
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
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        post(owner, url, success, failure = {})
    }

    inline fun <reified T : Any> post(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception) -> Unit
    ) {
        postRequest(url).enqueue(object : RequestJsonCallback<T>(owner) {
            override fun onSuccess(data: T) {
                success.invoke(data)
            }

            override fun onError(e: Exception, call: Call) {
                failure.invoke(e)
            }
        })
    }

    /**
     * head 方式请求
     */
    inline fun <reified T : Any> head(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        head(owner, url, success, failure = {})
    }

    inline fun <reified T : Any> head(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception) -> Unit
    ) {
        postRequest(url).enqueue(object : RequestJsonCallback<T>(owner) {
            override fun onSuccess(data: T) {
                success.invoke(data)
            }

            override fun onError(e: Exception, call: Call) {
                failure.invoke(e)
            }
        })
    }

    /**
     * delete 方式请求
     */
    inline fun <reified T : Any> delete(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        delete(owner, url, success, failure = {})
    }

    inline fun <reified T : Any> delete(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception) -> Unit
    ) {
        postRequest(url).enqueue(object : RequestJsonCallback<T>(owner) {
            override fun onSuccess(data: T) {
                success.invoke(data)
            }

            override fun onError(e: Exception, call: Call) {
                failure.invoke(e)
            }
        })
    }

    /**
     * put 方式请求
     */
    inline fun <reified T : Any> put(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        put(owner, url, success, failure = {})
    }

    inline fun <reified T : Any> put(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception) -> Unit
    ) {
        postRequest(url).enqueue(object : RequestJsonCallback<T>(owner) {
            override fun onSuccess(data: T) {
                success.invoke(data)
            }

            override fun onError(e: Exception, call: Call) {
                failure.invoke(e)
            }
        })
    }

    /**
     * patch 方式请求
     */
    inline fun <reified T : Any> patch(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        patch(owner, url, success, failure = {})
    }

    inline fun <reified T : Any> patch(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception) -> Unit
    ) {
        postRequest(url).enqueue(object : RequestJsonCallback<T>(owner) {
            override fun onSuccess(data: T) {
                success.invoke(data)
            }

            override fun onError(e: Exception, call: Call) {
                failure.invoke(e)
            }
        })
    }

}