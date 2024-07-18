package com.orbits.paymentapp.mvvm.main.model

import io.nearpay.sdk.utils.enums.TransactionData

data class SuccessPurchaseDataModel(
    val data : TransactionData ?= null
)
