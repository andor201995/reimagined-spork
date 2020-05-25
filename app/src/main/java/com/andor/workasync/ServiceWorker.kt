package com.andor.workasync

import android.os.Handler
import android.os.Looper
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

interface Task<T> {
    fun onExecute(): T
    fun onTaskComplete(result: T)
}

class ServiceWorker(id: String) {
    companion object {
        const val POISON_DATA = "kill"
    }

    private var poisonData = ""
    private val queue: BlockingQueue<Runnable> = LinkedBlockingQueue()
    private val mainLooper = Handler(Looper.getMainLooper())
    private val backgroundLooper: Thread

    init {
        backgroundLooper = Thread(Runnable {
            while (poisonData != POISON_DATA) {
                val taskRunnable = queue.take()
                taskRunnable.run()
            }
        })
        backgroundLooper.name = id
        backgroundLooper.start()
    }

    fun <T> addTask(task: Task<T>) {
        queue.add(
            Runnable {
                val result = task.onExecute()
                mainLooper.post { task.onTaskComplete(result) }
            }
        )
    }

    fun kill() {
        poisonData = POISON_DATA
    }
}