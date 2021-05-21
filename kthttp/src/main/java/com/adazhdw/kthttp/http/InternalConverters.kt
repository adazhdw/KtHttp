package com.adazhdw.kthttp.http

import com.adazhdw.kthttp.util.ClazzType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type


class InternalConverters : Converter.Factory() {

    /**
     * Not volatile because we don't mind multiple threads discovering this.
     */
    private var checkForKotlinUnit = true
    override fun responseBodyConverter(type: Type, net: Net): Converter<ResponseBody, *>? {
        if (type === ResponseBody::class.java) {
            return BufferingResponseBodyConverter.INSTANCE
        }
        if (type === Void::class.java) {
            return VoidResponseBodyConverter.INSTANCE
        }
        if (checkForKotlinUnit) {
            try {
                if (type === Unit::class.java) {
                    return UnitResponseBodyConverter.INSTANCE
                }
            } catch (ignored: NoClassDefFoundError) {
                checkForKotlinUnit = false
            }
        }
        return null
    }

    override fun requestBodyConverter(type: Type, net: Net): Converter<*, RequestBody>? {
        if (RequestBody::class.java.isAssignableFrom(ClazzType.getRawType(type))) {
            RequestBodyConverter.INSTANCE
        }
        return null
    }

    internal class VoidResponseBodyConverter : Converter<ResponseBody, Void> {
        companion object {
            val INSTANCE = VoidResponseBodyConverter()
        }

        override fun convert(value: ResponseBody): Void? {
            value.close()
            return null
        }
    }

    internal class UnitResponseBodyConverter : Converter<ResponseBody, Unit> {
        companion object {
            val INSTANCE = UnitResponseBodyConverter()
        }

        override fun convert(value: ResponseBody) {
            value.close()
            return
        }
    }

    class RequestBodyConverter : Converter<RequestBody, RequestBody> {
        companion object {
            val INSTANCE = RequestBodyConverter()
        }

        override fun convert(value: RequestBody): RequestBody {
            return value
        }
    }

    class StreamingResponseBodyConverter : Converter<ResponseBody, ResponseBody> {
        companion object {
            val INSTANCE = StreamingResponseBodyConverter()
        }

        override fun convert(value: ResponseBody): ResponseBody {
            return value
        }
    }

    class BufferingResponseBodyConverter : Converter<ResponseBody, ResponseBody> {
        companion object {
            val INSTANCE = BufferingResponseBodyConverter()
        }

        override fun convert(value: ResponseBody): ResponseBody {
            value.use {
                return Utils.buffer(it)
            }
        }
    }
}