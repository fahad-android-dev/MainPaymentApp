package com.orbits.paymentapp.interfaces

interface MessageSender {
    fun sendMessageToClient(clientId: String, message: String)
}