package lasupre.converter.jackson

import com.adazhdw.lasupre.Converter
import com.fasterxml.jackson.databind.ObjectReader
import java.io.IOException

class JacksonResponseBodyConverter<T>(private val adapter: ObjectReader) : Converter<okhttp3.ResponseBody, T> {
    @kotlin.jvm.Throws(IOException::class)
    override fun convert(value: okhttp3.ResponseBody): T? {
        return value.use { body ->
            adapter.readValue<T>(body.charStream())
        }
    }
}