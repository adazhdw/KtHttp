package com.adazhdw.lasupre

import java.lang.reflect.Type


internal class InternalConverters : Converter.Factory() {

    /**
     * Not volatile because we don't mind multiple threads discovering this.
     */
    private var checkForKotlinUnit = true
    override fun responseBodyConverter(type: Type, lasupre: Lasupre, requestFactory: RequestFactory): Converter<okhttp3.ResponseBody, *>? {
        if (type === okhttp3.ResponseBody::class.java) {
            return if (requestFactory.isStreaming) {
                StreamingResponseBodyConverter.INSTANCE
            } else {
                BufferingResponseBodyConverter.INSTANCE
            }
        }
        //适配String类型，直接返回Body.string()
        if (type === String::class.java) {
            return StringResponseBodyConverter.INSTANCE
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

    override fun requestBodyConverter(type: Type, lasupre: Lasupre): Converter<*, okhttp3.RequestBody>? {
        if (okhttp3.RequestBody::class.java.isAssignableFrom(TypeUtils.getRawType(type))) {
            RequestBodyConverter.INSTANCE
        }
        return null
    }

    internal class StringResponseBodyConverter : Converter<okhttp3.ResponseBody, String> {
        companion object {
            val INSTANCE = StringResponseBodyConverter()
        }

        override fun convert(value: okhttp3.ResponseBody): String {
            return value.string()
        }
    }

    internal class VoidResponseBodyConverter : Converter<okhttp3.ResponseBody, Void> {
        companion object {
            val INSTANCE = VoidResponseBodyConverter()
        }

        override fun convert(value: okhttp3.ResponseBody): Void? {
            value.close()
            return null
        }
    }

    internal class UnitResponseBodyConverter : Converter<okhttp3.ResponseBody, Unit> {
        companion object {
            val INSTANCE = UnitResponseBodyConverter()
        }

        override fun convert(value: okhttp3.ResponseBody) {
            value.close()
            return
        }
    }

    internal class RequestBodyConverter : Converter<okhttp3.RequestBody, okhttp3.RequestBody> {
        companion object {
            val INSTANCE = RequestBodyConverter()
        }

        override fun convert(value: okhttp3.RequestBody): okhttp3.RequestBody {
            return value
        }
    }

    internal class StreamingResponseBodyConverter : Converter<okhttp3.ResponseBody, okhttp3.ResponseBody> {
        companion object {
            val INSTANCE = StreamingResponseBodyConverter()
        }

        override fun convert(value: okhttp3.ResponseBody): okhttp3.ResponseBody {
            return value
        }
    }

    internal class BufferingResponseBodyConverter : Converter<okhttp3.ResponseBody, okhttp3.ResponseBody> {
        companion object {
            val INSTANCE = BufferingResponseBodyConverter()
        }

        override fun convert(value: okhttp3.ResponseBody): okhttp3.ResponseBody {
            value.use {
                return Utils.buffer(it)
            }
        }
    }
}