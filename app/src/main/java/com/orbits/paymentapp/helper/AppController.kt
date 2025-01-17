package com.orbits.paymentapp.helper

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import com.orbits.paymentapp.helper.PrefUtils.getAppConfig
import com.orbits.paymentapp.helper.PrefUtils.setAppConfig
import com.orbits.paymentapp.helper.helper_model.AppConfigModel
import java.util.Locale


class AppController : Application() {
    companion object {
        lateinit var instance: AppController
            private set
        const val CHANNEL_ID = "1"
        const val CHANNEL_NAME = "Payment"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        showServiceNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // Set the importance to HIGH for high-priority notifications
            ).apply {
                description = "Channel Description"
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        if (getAppConfig()?.lang?.isEmpty() == true) {
            if (Locale.getDefault().displayLanguage.equals("English", true)) {
                LocaleHelper.setLocale(this, "en")
            } else {
                LocaleHelper.setLocale(this, "ar")
            }
        } else {
            LocaleHelper.setLocale(this, getAppConfig()?.lang ?: "en")
        }

    }

    private fun showServiceNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "2",
                "Server Running",
                NotificationManager.IMPORTANCE_MIN // Set the importance to HIGH for high-priority notifications
            ).apply {
                description = "Channel Description"
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun setLocale() {
        val locale: Locale = resources.configuration.locale

        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration

        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        conf.setLocales(localeList)

        conf.setLayoutDirection(locale)
        res.updateConfiguration(conf, dm)
    }

    fun arabicLanguage() {
        val model = getAppConfig()
        setAppConfig(AppConfigModel(lang = "ar", cartBadgeCount = model?.cartBadgeCount ?: ""))
        setLocale()
    }

    fun englishLanguage() {
        val model = getAppConfig()
        setAppConfig(AppConfigModel(lang = "en", cartBadgeCount = model?.cartBadgeCount ?: ""))
        setLocale()
    }
}