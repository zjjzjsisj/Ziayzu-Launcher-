package com.ziayzu.launcher

import android.app.Application

class ZiayzuApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ZiayzuApp
            private set
    }
}
