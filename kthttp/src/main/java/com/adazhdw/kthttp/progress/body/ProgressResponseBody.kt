package com.adazhdw.kthttp.progress.body

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 * 服务器返回的response的自定义类，能拿到当前下载的文件大小和类型等信息
 * ProgressInterceptor里用到
 */
class ProgressResponseBody : ResponseBody {
    private val responseBody: ResponseBody?
    private var bufferedSource: BufferedSource? = null
    private var tag: String? = null

    constructor(responseBody: ResponseBody?) {
        this.responseBody = responseBody
    }

    constructor(responseBody: ResponseBody?, tag: String?) {
        this.responseBody = responseBody
        this.tag = tag
    }

    override fun contentType(): MediaType? {
        return responseBody!!.contentType()
    }

    override fun contentLength(): Long {
        return responseBody!!.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody!!.source()).buffer()
        }
        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var bytesRead: Long = 0

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                this.bytesRead += if (bytesRead == -1L) 0 else bytesRead
                //使用LiveEventBus的方式，实时发送当前已读取(上传/下载)的字节数据

                return bytesRead
            }
        }
    }
}