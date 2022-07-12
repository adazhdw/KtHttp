package com.adazhdw.lasupre

import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.Executor


interface ProgressListener {
    fun onProgress(total: Long, current: Long)
    fun onComplete(file: File)
    fun onError(throwable: Throwable)
}

fun tryCatch(block: () -> Unit, catch: ((e: Exception) -> Unit)? = null) {
    try {
        block.invoke()
    } catch (e: IOException) {
        catch?.invoke(e)
    }
}

class FileDownloadCallback(
    private val callbackExecutor: Executor,
    private val workExecutor: Executor,
    private val file: String,
    private val progressListener: ProgressListener
) : Callback<okhttp3.ResponseBody> {
    override fun onResponse(call: Call<okhttp3.ResponseBody>, response: Response<okhttp3.ResponseBody>) {
        workExecutor.execute {
            var ins: InputStream? = null
            val buf = ByteArray(2048)
            var length = 0
            var fos: FileOutputStream? = null
            val savePath: String = isDirExist(file)
            val fileName = getNameFromUrl(call.getRawCall().request().url.toString())
            val filePath = savePath + File.separator + fileName
            try {
                FileUtils.delete(filePath)
                val body = response.body ?: throw Exception("response's body is null")
                ins = body.byteStream()
                val total: Long = body.contentLength()
                val file = File(filePath)
                fos = FileOutputStream(file)
                var sum: Long = 0
                while (ins.read(buf).also { length = it } != -1) {
                    fos.write(buf, 0, length)
                    sum += length
                    //下载中:val progress = (sum * 1.0f / total * 100).toInt()
                    callbackExecutor.execute { progressListener.onProgress(total, sum) }
                }
                fos.flush()
                //下载完成
                callbackExecutor.execute { progressListener.onComplete(file) }
            } catch (e: Exception) {
                callbackExecutor.execute { progressListener.onError(e) }
            } finally {
                tryCatch({ ins?.close() })
                tryCatch({ fos?.close() })
            }
        }
    }

    override fun onFailure(call: Call<okhttp3.ResponseBody>, t: Throwable) {
        progressListener.onError(t)
    }

    private fun getNameFromUrl(url: String): String {
        val array = url.split("/")
        if (array.isEmpty()) return "${System.currentTimeMillis()}_randomFile"
        return array.last().toString()
    }

    private fun isDirExist(saveDir: String): String {
        val downloadFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + "/$saveDir")
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile()
        }
        return downloadFile.absolutePath
    }

}