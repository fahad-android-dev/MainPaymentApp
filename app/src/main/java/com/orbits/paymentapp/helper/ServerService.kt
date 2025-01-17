package com.orbits.paymentapp.helper

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.orbits.paymentapp.R
import com.orbits.paymentapp.helper.PrefUtils.setServiceData
import com.orbits.paymentapp.helper.helper_model.ServiceDataModel

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
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build()
        startForeground(2,notification)
    }


    enum class Actions {
        START,STOP
    }
}