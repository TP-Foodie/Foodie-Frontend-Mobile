package com.taller.tp.foodie

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import java.util.*

class MyApplication : Application() {

    private lateinit var mSocket: Socket

    fun getSocket(): Socket {
        return mSocket
    }

    override fun onCreate() {
        super.onCreate()

        Fresco.initialize(this)

        val stream = applicationContext.assets.open("environment.properties")
        val properties = Properties()
        properties.load(stream)
        val serverUrl = properties.getProperty("foodie-back.socket-url")

        try {
            mSocket = IO.socket("$serverUrl/chat")
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

    }
}
