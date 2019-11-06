package com.taller.tp.foodie

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

class MyApplication : Application() {

    companion object {
        private lateinit var mContext: MyApplication

        fun getContext(): MyApplication {
            return mContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        mContext = this
        Fresco.initialize(this)
    }

}
