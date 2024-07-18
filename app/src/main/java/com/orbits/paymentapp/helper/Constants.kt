package com.orbits.paymentapp.helper

import android.os.Build
import com.orbits.paymentapp.BuildConfig


object Constants {
    var DEVICE_TOKEN = ""
    val DEVICE_MODEL: String = Build.MODEL
    const val DEVICE_TYPE = "A" //passed in banners
    val OS_VERSION = Build.VERSION.RELEASE
    const val APP_VERSION = BuildConfig.VERSION_NAME

    const val DATE_FORMAT = "yyyy-MM-dd hh:mm:ss"

    var DEVICE_DENSITY = 0.0
    var ZONE_KUWAIT = "kuwait"
    var NO_ZONE = "no_zone"

    val fontBold = "bold"
    val fontRegular = "regular"
    val fontMedium = "medium"
    val fontLight = "light"
    val fontRegularRev = "regular_reverse"


    const val TOOLBAR_ICON_ONE = "iconOne"
    const val TOOLBAR_ICON_TWO = "iconTwo"

}

object DeepLinkTargets {
   /** define all targets here add use this from object for both pushwoosh & branch*/
    const val exampleListing = "abc"
    const val exampleDetails = "xyz"
    const val exampleExternalWeb = "e"
    /** remove or modify this example variables this is only for reference*/
}