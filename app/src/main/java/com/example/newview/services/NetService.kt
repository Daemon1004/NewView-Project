package com.example.newview.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class NetService : Service() {
    private val binder = LocalBinder()

    fun connect() {



    }

    inner class LocalBinder : Binder() {
        fun getService(): NetService = this@NetService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}