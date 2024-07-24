package com.orbits.paymentapp.helper

import android.view.View

interface AlertDialogInterface {
    fun onYesClick() {}
    fun onMasterYesClick() {}
    fun onNoClick() {}
    fun onCloseDialog() {}
    fun onSubmitPasswordClick(password: String) {}
}