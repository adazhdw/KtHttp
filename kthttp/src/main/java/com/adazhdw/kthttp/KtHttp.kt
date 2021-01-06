package com.adazhdw.kthttp

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.callback.RequestCallback
import com.adazhdw.kthttp.constant.Method
import com.adazhdw.kthttp.entity.Param
import com.adazhdw.kthttp.request.*
import okhttp3.Call

/**
 * Author: dgz
 * Date: 2020/8/21 14:50
 * Description: 请求工具类
 */

class KtHttp private constructor() {

    companion object {
        val ktHttp by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { KtHttp() }
    }

    /**
     * 请求
     * @param url url
     * @param param 请求参数工具类
     * @param callback 请求回调
     */
    @JvmOverloads
    fun request(
        method: Method,
        url: String,
        param: Param = Param.build(),
        callback: RequestCallback
    ) {
        when (method) {
            Method.GET -> get(url, param, callback)
            Method.DELETE -> delete(url, param, callback)
            Method.HEAD -> head(url, param, callback)
            Method.POST -> post(url, param, callback)
            Method.PUT -> put(url, param, callback)
            Method.PATCH -> patch(url, param, callback)
        }
    }

    /**
     * Get请求
     * @param url url
     * @param param 请求参数工具类
     */
    @JvmOverloads
    fun get(
        url: String,
        param: Param = Param.build(),
        callback: RequestCallback
    ) {
        get(url, param).tag(callback.mLifecycleOwner).enqueue(callback)
    }

    /**
     * Post请求
     * @param url url
     * @param param 请求参数工具类
     */
    @JvmOverloads
    fun post(
        url: String,
        param: Param = Param.build(),
        callback: RequestCallback
    ) {
        post(url, param).tag(callback.mLifecycleOwner).enqueue(callback)
    }

    /**
     * delete请求
     * @param url url
     * @param param 请求参数工具类
     */
    @JvmOverloads
    fun delete(
        url: String,
        param: Param = Param.build(),
        callback: RequestCallback
    ) {
        delete(url, param).tag(callback.mLifecycleOwner).enqueue(callback)
    }

    /**
     * head请求
     * @param url url
     * @param param 请求参数工具类
     */
    @JvmOverloads
    fun head(
        url: String,
        param: Param = Param.build(),
        callback: RequestCallback
    ) {
        head(url, param).tag(callback.mLifecycleOwner).enqueue(callback)
    }

    /**
     * put请求
     * @param url url
     * @param param 请求参数工具类
     */
    @JvmOverloads
    fun put(
        url: String,
        param: Param = Param.build(),
        callback: RequestCallback
    ) {
        put(url, param).tag(callback.mLifecycleOwner).enqueue(callback)
    }

    /**
     * patch请求
     * @param url url
     * @param param 请求参数工具类
     */
    @JvmOverloads
    fun patch(
        url: String,
        param: Param = Param.build(),
        callback: RequestCallback
    ) {
        patch(url, param).tag(callback.mLifecycleOwner).enqueue(callback)
    }

    /**
     * 取消请求
     */
    fun cancel(lifecycleOwner: LifecycleOwner) {
        cancel(lifecycleOwner.toString())
    }

    /**
     * 根据 TAG 取消请求任务
     */
    fun cancel(tag: Any?) {
        if (tag == null) return
        val client = KtConfig.mOkHttpClient

        //清除排队的请求任务
        for (call: Call in client.dispatcher.queuedCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }

        //清除正在执行的任务
        for (call: Call in client.dispatcher.runningCalls()) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
    }

    @JvmOverloads
    fun get(url: String, param: Param = Param.build()): GetRequest = GetRequest(param.url(url))

    @JvmOverloads
    fun post(url: String, param: Param = Param.build()): PostRequest = PostRequest(param.url(url))

    @JvmOverloads
    fun delete(url: String, param: Param = Param.build()): DeleteRequest = DeleteRequest(param.url(url))

    @JvmOverloads
    fun head(url: String, param: Param = Param.build()): HeadRequest = HeadRequest(param.url(url))

    @JvmOverloads
    fun put(url: String, param: Param = Param.build()): PutRequest = PutRequest(param.url(url))

    @JvmOverloads
    fun patch(url: String, param: Param = Param.build()): PatchRequest = PatchRequest(param.url(url))


}