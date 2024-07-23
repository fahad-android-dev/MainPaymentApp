package com.orbits.paymentapp.helper

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.orbits.paymentapp.R

class ServerService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> {
                start()
            }
            Actions.STOP.toString() -> {
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("ForegroundServiceType")
    private fun start() {
        val notification = NotificationCompat.Builder(this, "2")
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle("Aflak")
            .setContentText("Server is Running")
            .build()
        startForeground(1,notification)
    }


    enum class Actions {
        START,STOP
    }
}