package com.adazhdw.kthttp.ext

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.callback.RequestJsonCallback
import com.adazhdw.kthttp.constant.Method
import com.adazhdw.kthttp.entity.Param
import com.adazhdw.kthttp.request.*
import com.adazhdw.kthttp.request.base.BaseRequest

/**
 * FileName: DSL
 * Author: adazhdw
 * Date: 2021/1/5 17:39
 * Description:网络请求扩展类：KtHttp DSL方式请求封装
 * History:
 */

inline fun <reified T : Any> BaseRequest.enqueue(
    lifecycleOwner: LifecycleOwner,
    noinline success: (data: T) -> Unit
) = this.enqueue(lifecycleOwner, success, failed = { code, msg -> })

inline fun <reified T : Any> BaseRequest.enqueue(
    lifecycleOwner: LifecycleOwner,
    noinline success: (data: T) -> Unit,
    noinline failed: (code: Int, msg: String?) -> Unit
) = this.apply {
    this.execute(object : RequestJsonCallback<T>(lifecycleOwner) {
        override fun onSuccess(data: T) {
            success.invoke(data)
        }

        override fun onError(code: Int, msg: String?) {
            failed.invoke(code, msg)
        }
    })
}

/*通过Param设置并且返回一个Request*/
fun request(block: Param.() -> Unit): BaseRequest {
    val param = param(block)
    return when (param.method) {
        Method.GET -> GetRequest(param)
        Method.POST -> PostRequest(param)
        Method.DELETE -> DeleteRequest(param)
        Method.HEAD -> HeadRequest(param)
        Method.PATCH -> PatchRequest(param)
        Method.PUT -> PutRequest(param)
    }
}

/*Param 设置请求方式的扩展方法*/
fun Param.get(): Param = this.apply { this.method(Method.GET) }
fun Param.post(): Param = this.apply { this.method(Method.POST) }
fun Param.delete(): Param = this.apply { this.method(Method.DELETE) }
fun Param.head(): Param = this.apply { this.method(Method.HEAD) }
fun Param.patch(): Param = this.apply { this.method(Method.PATCH) }
fun Param.put(): Param = this.apply { this.method(Method.PUT) }

fun param(block: Param.() -> Unit): Param {
    return Param.build().apply { block(this) }
}
