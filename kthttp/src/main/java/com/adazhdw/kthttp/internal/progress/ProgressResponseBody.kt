package com.adazhdw.kthttp.internal.progress

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.buffer
import okio.source

/**
 * 带进度响应体
 */
internal class ProgressResponseBody(
    private val responseBody: ResponseBody,
    private val progressListener: ProgressCallback?
) : ResponseBody() {
    private var progressSource: BufferedSource? = null
    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (progressListener == null) {
            return responseBody.source()
        }
        val progressInputStream = ProgressInputStream(responseBody.source().inputStream(), progressListener, contentLength())
        progressSource = progressInputStream.source().buffer()
        return progressSource!!
    }

    override fun close() {
        try {
            progressSource?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}