package lasupre.converter.jackson

import com.adazhdw.lasupre.Converter
import com.adazhdw.lasupre.Lasupre
import com.adazhdw.lasupre.RequestFactory
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.lang.reflect.Type

class JacksonConverterFactory(private val mapper: ObjectMapper) : Converter.Factory() {

    companion object {
        @JvmStatic
        @JvmOverloads
        fun create(mapper: ObjectMapper = ObjectMapper()): JacksonConverterFactory {
            return JacksonConverterFactory(mapper)
        }
    }

    override fun responseBodyConverter(type: Type, lasupre: Lasupre, requestFactory: RequestFactory): Converter<ResponseBody, *> {
        val javaType = mapper.typeFactory.constructType(type)
        val reader = mapper.readerFor(javaType)
        return JacksonResponseBodyConverter<Any>(reader)
    }

    override fun requestBodyConverter(type: Type, lasupre: Lasupre): Converter<*, RequestBody> {
        val javaType = mapper.typeFactory.constructType(type)
        val writer = mapper.writerFor(javaType)
        return JacksonRequestBodyConverter<Any>(writer)
    }
}