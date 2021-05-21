package com.adazhdw.kthttp.http

import com.adazhdw.kthttp.util.ClazzType
import okhttp3.RequestBody
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * author：daguozhu
 * date-time：2020/9/5 16:13
 * description：
 **/
interface Converter<F, T> {
    @Throws(IOException::class)
    fun convert(value: F): T?

    abstract class Factory {

        /**
         * Returns a {@link Converter} for converting an HTTP response body to {@code type}, or null if
         * {@code type} cannot be handled by this factory. This is used to create converters for
         * response types such as {@code SimpleResponse} from a {@code Call<SimpleResponse>}
         * declaration.
         */
        open fun responseBodyConverter(type: Type, net: Net): Converter<okhttp3.ResponseBody, *>? {
            return null
        }

        /**
         * Returns a {@link Converter} for converting {@code type} to an HTTP request body, or null if
         * {@code type} cannot be handled by this factory. This is used to create converters for types
         * specified by {@link Body @Body}, {@link Part @Part}, and {@link PartMap @PartMap} values.
         */
        open fun requestBodyConverter(type: Type, net: Net): Converter<*, RequestBody>? {
            return null
        }

        companion object {
            /**
             * Extract the upper bound of the generic parameter at `index` from `type`. For
             * example, index 1 of `Map<String, ? extends Runnable>` returns `Runnable`.
             */
            protected fun getParameterUpperBound(index: Int, type: ParameterizedType): Type {
                return ClazzType.getParameterUpperBound(index, type)
            }

            /**
             * Extract the raw class type from `type`. For example, the type representing `List<? extends Runnable>` returns `List.class`.
             */
            protected fun getRawType(type: Type): Class<*> {
                return ClazzType.getRawType(type)
            }
        }
    }
}