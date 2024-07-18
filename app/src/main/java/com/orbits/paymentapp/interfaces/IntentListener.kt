package com.orbits.paymentapp.interfaces

import com.google.gson.JsonObject
import java.net.Socket

interface IntentListener {
    fun onIntentReceived()
}