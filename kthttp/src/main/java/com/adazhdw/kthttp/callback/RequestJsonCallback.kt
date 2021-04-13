package com.adazhdw.kthttp.callback

import androidx.lifecycle.LifecycleOwner
import com.adazhdw.kthttp.coroutines.convert
import com.adazhdw.kthttp.util.ClazzType
import com.adazhdw.kthttp.util.ExecutorUtils
import okhttp3.Call
import okhttp3.Response
import java.lang.reflect.Type

/**
 * Author: dgz
 * Date: 2020/8/21 14:50
 * Description: Gson回调转换泛型类 T
 */
abstract class RequestJsonCallback<T : Any>(owner: LifecycleOwner?) : RequestCallbackImpl(owner) {
    private val mType: Type?

    init {
        mType = getSuperclassTypeParameter(javaClass)
    }

    override fun onResult(response: Response, call: Call) {
        super.onResult(response, call)
        val data = response.convert<T>(mType)
        ExecutorUtils.mainThread.execute {
            this.onSuccess(data)
            this.onFinish()
        }
    }

    override fun onFailure(e: Exception, call: Call) {
        super.onFailure(e, call)
        this.onError(e, call)
    }

    abstract fun onSuccess(data: T)
    abstract fun onError(e: Exception, call: Call)

    private fun getSuperclassTypeParameter(subclass: Class<*>): Type {
        return ClazzType.getType(subclass,0)
    }

}