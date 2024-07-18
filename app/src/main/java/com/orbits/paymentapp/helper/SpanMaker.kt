package com.orbits.paymentapp.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.*

class SpanMaker(val spanText: SpannableString) {

    fun underline(search: String) = apply {
        spanText.setSpan(
            UnderlineSpan(),
            spanText.indexOf(search),
            spanText.indexOf(search) + search.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return@apply
    }

    fun clickSpan(search: String, span: ClickableSpan) = apply {
        spanText.setSpan(
            span,
            spanText.indexOf(search),
            spanText.indexOf(search) + search.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return@apply
    }

    fun textColor(search: String, color: Int) = apply {
        spanText.setSpan(
            ForegroundColorSpan(color),
            spanText.indexOf(search),
            spanText.indexOf(search) + search.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return@apply
    }

    fun textFont(search: String, constantFonts: String, context: Context) = apply {
        spanText.setSpan(
            CustomTypefaceSpan("", Global.getTypeFace(context, constantFonts)),
            spanText.indexOf(search),
            spanText.indexOf(search) + spanText.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return@apply
    }

    fun textSize(search: String, size: Int) = apply {
        spanText.setSpan(
            AbsoluteSizeSpan(size),
            spanText.indexOf(search),
            spanText.indexOf(search) + search.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return@apply
    }

    fun textSize(startIndex: Int, endIndex: Int, size: Int) = apply {
        spanText.setSpan(
            AbsoluteSizeSpan(size),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return@apply
    }

    fun boldSpan(search: String) = apply {
        spanText.setSpan(
            StyleSpan(Typeface.BOLD),
            spanText.indexOf(search),
            spanText.indexOf(search) + search.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return@apply
    }

    fun regularSpan(search: String) = apply {
        spanText.setSpan(
            StyleSpan(Typeface.NORMAL),
            spanText.indexOf(search),
            spanText.indexOf(search) + search.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        return@apply
    }

    fun imageSpan(search: String, context: Context, icon: Bitmap) = apply {
        val span = ImageSpan(context, icon)
        spanText.setSpan(span, search.length - 1, search.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
        return@apply
    }

}
