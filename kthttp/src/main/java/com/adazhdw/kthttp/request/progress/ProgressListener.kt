package com.adazhdw.kthttp.request.progress

/**
 * 进度回调
 *
 * @author lizhangqu
 * @version V1.0
 * @since 2017-07-12 16:19
 */
abstract class ProgressListener : ProgressCallback {
    var started = false
    var lastRefreshTime = 0L
    var lastBytesWritten = 0L
    var minTime = 100 //最小回调时间100ms，避免频繁回调

    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     */
    override fun onProgressChanged(numBytes: Long, totalBytes: Long, percent: Float) {
        if (!started) {
            onProgressStart(totalBytes)
            started = true
        }
        if (numBytes == -1L && totalBytes == -1L && percent == -1f) {
            onProgressChanged(-1, -1, -1f, -1f)
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRefreshTime >= minTime || numBytes == totalBytes || percent >= 1f) {
            var intervalTime = currentTime - lastRefreshTime
            if (intervalTime == 0L) {
                intervalTime += 1
            }
            val updateBytes = numBytes - lastBytesWritten
            val networkSpeed = updateBytes / intervalTime
            onProgressChanged(numBytes, totalBytes, percent, networkSpeed.toFloat())
            lastRefreshTime = System.currentTimeMillis()
            lastBytesWritten = numBytes
        }
        if (numBytes == totalBytes || percent >= 1f) {
            onProgressFinish()
        }
    }

    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent，speed都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     * @param speed      速度 bytes/ms
     */
    abstract fun onProgressChanged(numBytes: Long, totalBytes: Long, percent: Float, speed: Float)

    /**
     * 进度开始
     *
     * @param totalBytes 总大小
     */
    open fun onProgressStart(totalBytes: Long) {}

    /**
     * 进度结束
     */
    open fun onProgressFinish() {}
}