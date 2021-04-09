package com.adazhdw.kthttp.internal

import com.adazhdw.kthttp.HttpClient
import com.adazhdw.kthttp.constant.HttpConstant
import com.adazhdw.kthttp.util.MimeUtil
import okhttp3.MediaType
import java.io.File

/**
 * author：daguozhu
 * date-time：2020/11/17 16:40
 * description：请求参数封装
 **/
class HttpParams(httpClient: HttpClient) : MapEntity<Any>() {
    /** 请求头存放集合 */
    /** 上传文件集合 */
    internal val files: MutableList<Part> = mutableListOf()

    init {
        contents.putAll(httpClient.commonParams)
    }

    fun addFormDataPart(key: String, file: File) {
        if (!file.exists() || file.length() == 0L) return
        val isPng = file.name.indexOf("png") > 0 || file.name.indexOf("PNG") > 0
        if (isPng) {
            this.files.add(Part(key, Part.FileWrapper(file, HttpConstant.PNG)))
            return
        }
        val isJpg = file.name.indexOf("jpg") > 0 || file.name.indexOf("JPG") > 0
                || file.name.indexOf("jpeg") > 0 || file.name.indexOf("JPEG") > 0
        if (isJpg) {
            this.files.add(Part(key, Part.FileWrapper(file, HttpConstant.JPG)))
            return
        }
        if (!isPng && !isJpg) {
            this.files.add(Part(key, Part.FileWrapper(file, mediaType(file))))
        }
    }

    fun addFormDataPart(map: Map<String, File>) {
        for ((key, file) in map) addFormDataPart(key, file)
    }

    private fun mediaType(file: File): MediaType? = MimeUtil.getMediaType(file.path)

}