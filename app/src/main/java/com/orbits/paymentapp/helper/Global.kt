package com.orbits.paymentapp.helper

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.text.SpannableString
import android.util.*
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import coil.load
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.orbits.paymentapp.R
import com.orbits.paymentapp.databinding.LayoutSnackBarBinding
import java.util.*


@SuppressLint("StaticFieldLeak")
object Global {

    fun getDimension(activity: Activity, size: Double): Int {
        return if (Constants.DEVICE_DENSITY > 0) {
            //density saved in constant calculated on first time in splash if in case its 0 then calculate again
            (Constants.DEVICE_DENSITY * size).toInt()
        } else {
            ((getDeviceWidthInDouble(activity) / 320) * size).toInt()

        }
    }

    fun getDeviceWidthInDouble(activity: Activity): Double {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels.toDouble()
    }

    @SuppressLint("RestrictedApi")
    fun View?.showSnackBar(
        strMsg: String,
        showBelowStatus: Boolean = false,
        duration: Int = 2000, //pass this true where the theme is fullscreen
        gravity: Int = Gravity.TOP
    ) {

        if (this != null) {
            val snackbar = Snackbar.make(this, strMsg, Snackbar.LENGTH_LONG)
            val layoutParams = ActionBar.LayoutParams(snackbar.view.layoutParams)
            layoutParams.gravity = gravity
            snackbar.view.setPadding(0, 0, 0, 0)

            if (showBelowStatus) {//If true, adding top margin to show it below status bar,
                // because in fullscreen theme statusBar is overlapping snackBar
                var statusBarHeight = 0
                val resourceId: Int =
                    context.resources.getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
                }
                layoutParams.topMargin = statusBarHeight
            }
            snackbar.view.layoutParams = layoutParams
            snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
            val context = snackbar.context
            val binding: LayoutSnackBarBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_snack_bar, null, false
            )
            val spanText = SpannableString(strMsg)
            val span = SpanMaker(spanText).textFont(strMsg, Constants.fontRegular, context)
            binding.txtSnackBarText.text = span.spanText
            val snackBarLayout = snackbar.view as Snackbar.SnackbarLayout
            snackBarLayout.addView(binding.root)
            snackbar.setBackgroundTint(
                ContextCompat.getColor(
                    context,
                    R.color.color_black
                )
            )
            snackbar.duration = duration
            snackbar.show()
        }
    }

    @SuppressLint("NewApi")
    fun getTypeFace(context: Context, fontStyle: String): Typeface {
        return when (fontStyle) {
            Constants.fontRegular -> context.resources.getFont(R.font.font_regular)
            Constants.fontBold -> context.resources.getFont(R.font.font_bold)
            Constants.fontMedium -> context.resources.getFont(R.font.font_medium)
            Constants.fontRegularRev -> context.resources.getFont(R.font.font_regular_rev)
            else -> context.resources.getFont(R.font.font_regular)
        }
    }

    fun ImageView.loadImagesUsingCoil(
        strUrl: String?,
        errorImage: Int? = null,
    ) {
        this.load(strUrl) {
            crossfade(true)
            if (errorImage != null) {
                error(errorImage)
            }
            allowConversionToBitmap(true)
            bitmapConfig(Bitmap.Config.ARGB_8888)
            allowHardware(true)
            listener(object : ImageRequest.Listener {
                override fun onError(request: ImageRequest, result: ErrorResult) {
                }
            })
        }
    }

}
