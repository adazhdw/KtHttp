package com.adazhdw.kthttp.util

import android.os.Handler
import android.os.Looper
import okhttp3.internal.threadFactory
import java.util.concurrent.*

object ExecutorUtils {

    private const val THREAD_COUNT = 3

    val diskIO: Executor = DiskIOThreadExecutor()

    val networkIO: ThreadPoolExecutor =
        Executors.newFixedThreadPool(THREAD_COUNT) as ThreadPoolExecutor

    val networkExecutor: ExecutorService = ThreadPoolExecutor(
        0, Int.MAX_VALUE, 60, TimeUnit.SECONDS,
        SynchronousQueue(), threadFactory("$ExecutorUtils Dispatcher", false)
    )

    val mainThread = MainThreadExecutor()

    class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    class DiskIOThreadExecutor : Executor {

        private val diskIO = Executors.newSingleThreadExecutor()

        override fun execute(command: Runnable) {
            diskIO.execute(command)
        }
    }
}
