package com.adazhdw.kthttp.request.progress

import java.io.IOException
import java.io.OutputStream

/**
 * 带进度的输出流
 */
internal class ProgressOutputStream(
    private val stream: OutputStream?,
    private val listener: ProgressCallback,
    private val total: Long
) : OutputStream() {
    private var totalWritten: Long = 0

    @Throws(IOException::class)
    override fun write(b: ByteArray, off: Int, len: Int) {
        stream?.write(b, off, len)
        if (total < 0) {
            listener.onProgressChanged(-1, -1, -1f)
            return
        }
        totalWritten += if (len < b.size) {
            len.toLong()
        } else {
            b.size.toLong()
        }
        listener.onProgressChanged(totalWritten, total, totalWritten * 1.0f / total)
    }

    @Throws(IOException::class)
    override fun write(b: Int) {
        stream?.write(b)
        if (total < 0) {
            listener.onProgressChanged(-1, -1, -1f)
            return
        }
        totalWritten++
        listener.onProgressChanged(totalWritten, total, totalWritten * 1.0f / total)
    }

    @Throws(IOException::class)
    override fun close() {
        stream?.close()
    }

    @Throws(IOException::class)
    override fun flush() {
        stream?.flush()
    }
}