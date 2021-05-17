package com.adazhdw.kthttp

import androidx.lifecycle.LifecycleOwner
import okhttp3.Call

/**
 * author：daguozhu
 * date-time：2020/11/16 15:05
 * description：
 **/
object Https {

    private val httpClient by lazy { http() }

    internal fun http(): HttpClient {
        val builder = HttpClient.Builder()
        return builder.build()
    }

    fun newBuilder(): HttpClient.Builder {
        return httpClient.newBuilder()
    }

    fun request() = httpClient.request()
    fun getRequest(url: String) = httpClient.request().get().url(url)
    fun postRequest(url: String) = httpClient.request().post().url(url)
    fun headRequest(url: String) = httpClient.request().head().url(url)
    fun deleteRequest(url: String) = httpClient.request().delete().url(url)
    fun putRequest(url: String) = httpClient.request().put().url(url)
    fun patchRequest(url: String) = httpClient.request().patch().url(url)

    /**
     * GET 方式请求
     */
    inline fun <reified T : Any> get(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        getRequest(url).enqueue(owner, success)
    }

    inline fun <reified T : Any> get(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        getRequest(url).enqueue(owner, success, failure)
    }

    /**
     * POST 方式请求
     */
    inline fun <reified T : Any> post(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        postRequest(url).enqueue(owner, success)
    }

    inline fun <reified T : Any> post(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        postRequest(url).enqueue(owner, success, failure)
    }

    /**
     * head 方式请求
     */
    inline fun <reified T : Any> head(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        headRequest(url).enqueue(owner, success)
    }

    inline fun <reified T : Any> head(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        headRequest(url).enqueue(owner, success, failure)
    }

    /**
     * delete 方式请求
     */
    inline fun <reified T : Any> delete(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        deleteRequest(url).enqueue(owner, success)
    }

    inline fun <reified T : Any> delete(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        deleteRequest(url).enqueue(owner, success, failure)
    }

    /**
     * put 方式请求
     */
    inline fun <reified T : Any> put(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        putRequest(url).enqueue(owner, success)
    }

    inline fun <reified T : Any> put(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        putRequest(url).enqueue(owner, success, failure)
    }

    /**
     * patch 方式请求
     */
    inline fun <reified T : Any> patch(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        patchRequest(url).enqueue(owner, success)
    }

    inline fun <reified T : Any> patch(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        patchRequest(url).enqueue(owner, success, failure)
    }

}