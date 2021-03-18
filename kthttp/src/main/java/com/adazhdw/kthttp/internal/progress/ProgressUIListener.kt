package com.adazhdw.kthttp.internal.progress

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * 流读写进度ui回调
 */
abstract class ProgressUIListener : ProgressListener() {
    private var mHandler: Handler? = null
    private fun ensureHandler() {
        if (mHandler != null) {
            return
        }
        synchronized(ProgressUIListener::class.java) {
            if (mHandler == null) {
                mHandler = object : Handler(Looper.getMainLooper()) {
                    override fun handleMessage(msg: Message) {
                        when (msg.what) {
                            WHAT_START -> {
                                val startData = msg.data ?: return
                                onUIProgressStart(startData.getLong(TOTAL_BYTES))
                            }
                            WHAT_UPDATE -> {
                                val updateData = msg.data ?: return
                                val numBytes = updateData.getLong(CURRENT_BYTES)
                                val totalBytes = updateData.getLong(TOTAL_BYTES)
                                val percent = updateData.getFloat(PERCENT)
                                val speed = updateData.getFloat(SPEED)
                                onUIProgressChanged(numBytes, totalBytes, percent, speed)
                            }
                            WHAT_FINISH -> onUIProgressFinish()
                            else -> {
                            }
                        }
                    }
                }
            }
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
    override fun onProgressChanged(numBytes: Long, totalBytes: Long, percent: Float, speed: Float) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onUIProgressChanged(numBytes, totalBytes, percent, speed)
            return
        }
        ensureHandler()
        val message = mHandler!!.obtainMessage()
        message.what = WHAT_UPDATE
        val data = Bundle()
        data.putLong(CURRENT_BYTES, numBytes)
        data.putLong(TOTAL_BYTES, totalBytes)
        data.putFloat(PERCENT, percent)
        data.putFloat(SPEED, speed)
        message.data = data
        mHandler!!.sendMessage(message)
    }

    /**
     * 进度开始
     *
     * @param totalBytes 总大小
     */
    override fun onProgressStart(totalBytes: Long) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onUIProgressStart(totalBytes)
            return
        }
        ensureHandler()
        if (mHandler == null) return
        val message = mHandler!!.obtainMessage()
        message.what = WHAT_START
        val data = Bundle()
        data.putLong(TOTAL_BYTES, totalBytes)
        message.data = data
        mHandler!!.sendMessage(message)
    }

    /**
     * 进度结束
     */
    override fun onProgressFinish() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            onUIProgressFinish()
            return
        }
        ensureHandler()
        if (mHandler == null) return
        val message = mHandler!!.obtainMessage()
        message.what = WHAT_FINISH
        mHandler!!.sendMessage(message)
    }

    /**
     * 进度发生了改变，如果numBytes，totalBytes，percent，speed都为-1，则表示总大小获取不到
     *
     * @param numBytes   已读/写大小
     * @param totalBytes 总大小
     * @param percent    百分比
     * @param speed      速度 bytes/ms
     */
    abstract fun onUIProgressChanged(numBytes: Long, totalBytes: Long, percent: Float, speed: Float)

    /**
     * 进度开始
     *
     * @param totalBytes 总大小
     */
    fun onUIProgressStart(totalBytes: Long) {}

    /**
     * 进度结束
     */
    fun onUIProgressFinish() {}

    companion object {
        private const val WHAT_START = 0x01
        private const val WHAT_UPDATE = 0x02
        private const val WHAT_FINISH = 0x03
        private const val CURRENT_BYTES = "numBytes"
        private const val TOTAL_BYTES = "totalBytes"
        private const val PERCENT = "percent"
        private const val SPEED = "speed"
    }
}