package com.adazhdw.kthttp.http

import com.adazhdw.kthttp.http.CallAdapter.Factory
import com.adazhdw.kthttp.util.ClazzType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Adapts a [Call] with response type `R` into the type of `T`. Instances are
 * created by [a factory][Factory] which is []
 */
interface CallAdapter<R, T> {
    /**
     * Returns the value type that this adapter uses when converting the HTTP response body to a Java
     * object. For example, the response type for `Call<Repo>` is `Repo`. This type is
     * used to prepare the `call` passed to `#adapt`.
     */
    fun responseType(): Type

    /**
     * Returns an instance of `T` which delegates to `call`.
     *
     * For example, given an instance for a hypothetical utility, `Async`, this instance
     * would return a new `Async<R>` which invoked `call` when run.
     */
    fun adapt(call: Call<R>): T

    /**
     * Creates [CallAdapter] instances based on the return type
     */
    abstract class Factory {
        /**
         * Returns a call adapter for interface methods that return `returnType`, or null if it
         * cannot be handled by this factory.
         */
        abstract fun get(returnType: Type, net: Net): CallAdapter<*, *>?

        companion object {
            /**
             * Extract the upper bound of the generic parameter at `index` from `type`. For
             * example, index 1 of `Map<String, ? extends Runnable>` returns `Runnable`.
             */
            @JvmStatic
            protected fun getParameterUpperBound(index: Int, type: ParameterizedType): Type {
                return ClazzType.getParameterUpperBound(index, type)
            }

            /**
             * Extract the raw class type from `type`. For example, the type representing `List<? extends Runnable>` returns `List.class`.
             */
            @JvmStatic
            protected fun getRawType(type: Type): Class<*> {
                return ClazzType.getRawType(type)
            }
        }
    }
}