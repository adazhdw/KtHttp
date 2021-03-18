package com.adazhdw.kthttp

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.ext.execute
import com.adazhdw.kthttp.internal.HttpRequest
import okhttp3.Call

/**
 * author：daguozhu
 * date-time：2020/11/16 15:05
 * description：
 **/
object KtHttp {

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
        getRequest(url).execute(owner, success)
    }

    inline fun <reified T : Any> get(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        getRequest(url).execute(owner, success, failure)
    }

    /**
     * POST 方式请求
     */
    inline fun <reified T : Any> post(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        postRequest(url).execute(owner, success)
    }

    inline fun <reified T : Any> post(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        postRequest(url).execute(owner, success, failure)
    }

    /**
     * head 方式请求
     */
    inline fun <reified T : Any> head(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        headRequest(url).execute(owner, success)
    }

    inline fun <reified T : Any> head(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        headRequest(url).execute(owner, success, failure)
    }

    /**
     * delete 方式请求
     */
    inline fun <reified T : Any> delete(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        deleteRequest(url).execute(owner, success)
    }

    inline fun <reified T : Any> delete(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        deleteRequest(url).execute(owner, success, failure)
    }

    /**
     * put 方式请求
     */
    inline fun <reified T : Any> put(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        putRequest(url).execute(owner, success)
    }

    inline fun <reified T : Any> put(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        putRequest(url).execute(owner, success, failure)
    }

    /**
     * patch 方式请求
     */
    inline fun <reified T : Any> patch(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit
    ) {
        patchRequest(url).execute(owner, success)
    }

    inline fun <reified T : Any> patch(
        owner: LifecycleOwner?,
        url: String,
        noinline success: (data: T) -> Unit,
        noinline failure: (e: Exception, call: Call) -> Unit
    ) {
        patchRequest(url).execute(owner, success, failure)
    }

}