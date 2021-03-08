package com.adazhdw.kthttp.request.progress

/**
 * 流读写进度
 */
internal interface ProgressCallback {
    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     */
    fun onProgressChanged(numBytes: Long, totalBytes: Long, percent: Float)
}