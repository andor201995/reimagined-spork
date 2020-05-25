package com.andor.workasync

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    companion object {
        const val IMAGE_URL_1 =
            "https://images.unsplash.com/photo-1486010586814-abd061e90cf9?ixlib=rb-1.2.1&auto=format&fit=crop&w=3068&q=80"
        const val IMAGE_URL_2 =
            "https://images.unsplash.com/photo-1495978866932-92dbc079e62e?ixlib=rb-1.2.1&auto=format&fit=crop&w=934&q=80"
    }

    private val serviceWorker1: ServiceWorker = ServiceWorker("service_worker_1")
    private val serviceWorker2: ServiceWorker = ServiceWorker("service_worker_2")
    private val okHttpClient = OkHttpClient().newBuilder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Button1.setOnClickListener {
            fetchImage1AndSet()
        }
        Button2.setOnClickListener {
            fetchImage2AndSet()
        }
    }

    override fun onDestroy() {
        serviceWorker1.kill()
        serviceWorker2.kill()

        super.onDestroy()
    }

    private fun fetchImage2AndSet() {
        val task = object : Task<Bitmap> {
            override fun onExecute(): Bitmap {
                val request = Request.Builder().url(IMAGE_URL_2).build()
                val response = okHttpClient.newCall(request).execute()
                // Todo: can throw network exception and cause crash can be solved using enqueue and Callback
                return BitmapFactory.decodeStream(response.body?.byteStream())
            }

            override fun onTaskComplete(result: Bitmap) {
                ImageView2.setImageBitmap(result)
            }
        }
        serviceWorker2.addTask(task)
    }

    private fun fetchImage1AndSet() {
        val task = object : Task<Bitmap> {
            override fun onExecute(): Bitmap {
                val request = Request.Builder().url(IMAGE_URL_1).build()
                val response = okHttpClient.newCall(request).execute()
                // Todo: can throw network exception and cause crash can be solved using enqueue and Callback
                return BitmapFactory.decodeStream(response.body?.byteStream())
            }

            override fun onTaskComplete(result: Bitmap) {
                ImageView1.setImageBitmap(result)
            }
        }
        serviceWorker1.addTask(task)
    }

}
