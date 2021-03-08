package com.adazhdw.kthttp.request.progress

import java.io.IOException
import java.io.InputStream

/**
 * 带进度的输入流
 */
internal class ProgressInputStream(
    private val stream: InputStream?,
    private val listener: ProgressCallback,
    private val total: Long
) : InputStream() {
    private var totalRead: Long = 0

    @Throws(IOException::class)
    override fun read(): Int {
        val read = stream?.read() ?: 0
        if (total < 0) {
            listener.onProgressChanged(-1, -1, -1f)
            return read
        }
        if (read >= 0) {
            totalRead++
            listener.onProgressChanged(totalRead, total, totalRead * 1.0f / total)
        }
        return read
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val read = stream?.read(b, off, len) ?: 0
        if (total < 0) {
            listener.onProgressChanged(-1, -1, -1f)
            return read
        }
        if (read >= 0) {
            totalRead += read.toLong()
            listener.onProgressChanged(totalRead, total, totalRead * 1.0f / total)
        }
        return read
    }

    @Throws(IOException::class)
    override fun close() {
        stream?.close()
    }
}