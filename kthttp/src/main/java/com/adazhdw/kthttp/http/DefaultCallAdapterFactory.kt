package com.adazhdw.kthttp.http

import com.adazhdw.kthttp.util.ClazzType
import okhttp3.Request
import okio.Timeout
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.Executor


class DefaultCallAdapterFactory(private val mainExecutor: Executor) : CallAdapter.Factory() {
    override fun get(returnType: Type, net: Net): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) {
            return null
        }
        if (returnType !is ParameterizedType) {
            throw IllegalArgumentException("Call return type must be parameterized as Call<Foo> or Call<? extends Foo>")
        }
        val responseType: Type = ClazzType.getParameterUpperBound(0, returnType)

        return object : CallAdapter<Any, Call<*>> {
            override fun responseType(): Type {
                return responseType
            }

            override fun adapt(call: Call<Any>): Call<*> {
                return ExecutorCallbackCall(mainExecutor, call)
            }
        }
    }

    class ExecutorCallbackCall<T>(private val mainExecutor: Executor, private val delegate: Call<T>) : Call<T> {
        override fun execute(): Response<T> {
            return delegate.execute()
        }

        override fun enqueue(callback: Callback<T>) {
            delegate.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    mainExecutor.execute {
                        if (delegate.isCanceled()) {
                            callback.onFailure(call, IOException("Canceled"))
                        } else {
                            callback.onResponse(call, response)
                        }
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callback.onFailure(call, t)
                }
            })
        }

        override fun isExecuted(): Boolean {
            return delegate.isExecuted()
        }

        override fun cancel() {
            delegate.cancel()
        }

        override fun isCanceled(): Boolean {
            return delegate.isCanceled()
        }

        override fun copy(): Call<T> {
            return delegate.copy()
        }

        override fun request(): Request {
            return delegate.request()
        }

        override fun timeout(): Timeout {
            return delegate.timeout()
        }

    }
}