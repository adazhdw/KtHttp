package lasupre.converter.jackson

import com.adazhdw.lasupre.Converter
import com.fasterxml.jackson.databind.ObjectWriter
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class JacksonRequestBodyConverter<T : Any>(private val adapter: ObjectWriter) : Converter<T, okhttp3.RequestBody> {

    companion object {
        private val MEDIA_TYPE: MediaType = "application/json; charset=UTF-8".toMediaType()
    }

    override fun convert(value: T): okhttp3.RequestBody {
        val bytes = adapter.writeValueAsBytes(value)
        return bytes.toRequestBody(MEDIA_TYPE)
    }
}