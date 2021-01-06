package com.adazhdw.kthttp.progress.body

import com.adazhdw.kthttp.util.KtExecutors
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

/**
 * 自定义RequestBody类，得到文件上传的进度
 */
class ProgressRequestBody(
    //实际的待包装请求体
    private val requestBody: RequestBody,
    private val progress: (value: Float, total: Long) -> Unit
) : RequestBody() {
    //包装完成的BufferedSink
    private var bufferedSink: BufferedSink? = null

    /** 重写调用实际的响应体的contentType */
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    /**重写调用实际的响应体的contentLength ，这个是文件的总字节数  */
    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    /** 重写进行写入 */
    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (bufferedSink == null) {
            bufferedSink = sink(sink).buffer()
        }
        requestBody.writeTo(bufferedSink!!)
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink?.flush()
    }

    /** 写入，回调进度接口 */
    private fun sink(sink: BufferedSink): Sink {
        return object : ForwardingSink(sink) {
            //当前写入字节数
            internal var bytesWritten = 0L

            //总字节长度，避免多次调用contentLength()方法
            internal var contentLength = 0L

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)//这个方法会循环调用，byteCount是每次调用上传的字节数。
                if (contentLength == 0L) {
                    //获得总字节长度
                    contentLength = contentLength()
                }
                //增加当前写入的字节数
                bytesWritten += byteCount
                val progress = bytesWritten * 1.0f / contentLength
                KtExecutors.mainThread.execute { progress(progress, contentLength) }
            }
        }
    }
}