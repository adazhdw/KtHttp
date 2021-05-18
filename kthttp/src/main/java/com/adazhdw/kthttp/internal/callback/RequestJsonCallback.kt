package com.adazhdw.kthttp.internal.callback

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.HttpClient
import com.adazhdw.kthttp.coroutines.convert
import com.adazhdw.kthttp.util.ClazzType
import okhttp3.Call
import okhttp3.Response
import java.lang.reflect.Type

/**
 * Author: dgz
 * Date: 2020/8/21 14:50
 * Description: Gson回调转换泛型类 T
 */
abstract class RequestJsonCallback<T : Any>(owner: LifecycleOwner?, httpClient: HttpClient) : RequestCallbackImpl(owner, httpClient) {
    private val mType: Type?

    init {
        mType = getSuperclassTypeParameter(javaClass)
    }

    override fun onResult(response: Response, call: Call) {
        super.onResult(response, call)
        val data = response.convert<T>(mType)
        execute(Runnable {
            this.onSuccess(data)
            this.onFinish()
        }, false)
    }

    abstract fun onSuccess(data: T)

    private fun getSuperclassTypeParameter(subclass: Class<*>): Type {
        return ClazzType.getType(subclass, 0)
    }

}