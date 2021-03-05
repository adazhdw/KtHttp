package com.adazhdw.kthttp.request.progress

import com.adazhdw.kthttp.util.KtExecutors
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*

/**
 * name：ResponseProgressBody
 * author：adazhdw
 * date：2021/3/1:13:50
 * description:
 */
class ResponseProgressBody(
    private val responseBody: ResponseBody,
    private val progress: ((value: Long, total: Long) -> Unit)? = null
) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentLength(): Long = responseBody.contentLength()

    override fun contentType(): MediaType? = responseBody.contentType()

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            private var totalBytesRead = 0L

            //总字节长度，避免多次调用contentLength()方法
            internal var contentLength = 0L
            override fun read(sink: Buffer, byteCount: Long): Long {
                if (contentLength == 0L) {
                    //获得总字节长度
                    contentLength = contentLength()
                }
                val bytesRead = super.read(sink, byteCount)
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0L
                KtExecutors.mainThread.execute { progress?.invoke(totalBytesRead, contentLength) }
                return bytesRead
            }
        }
    }
}