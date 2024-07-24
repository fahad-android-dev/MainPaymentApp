package com.orbits.paymentapp.helper

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.method.PasswordTransformationMethod
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.orbits.paymentapp.R
import com.orbits.paymentapp.databinding.LayoutChangePasswordDialogBinding
import com.orbits.paymentapp.databinding.LayoutChangePasswordNewDialogBinding
import com.orbits.paymentapp.databinding.LayoutCustomAlertBinding
import com.orbits.paymentapp.databinding.LayoutGenerateCodeDialogBinding
import com.orbits.paymentapp.databinding.LayoutSettingsPasswordDialogBinding
import com.orbits.paymentapp.helper.Global.getDimension
import com.orbits.paymentapp.helper.PrefUtils.getAppPassword
import com.orbits.paymentapp.helper.PrefUtils.getMasterKey

object Dialogs {

    var customDialog: Dialog? = null
    var codeDialog: Dialog? = null
    var changeAllPasswordDialog: Dialog? = null
    var changePasswordDialog: Dialog? = null

    fun showPasswordDialog(
        activity: Context,
        isCancellable: Boolean? = true,
        alertDialogInterface: AlertDialogInterface,
    ) {
        try {
            customDialog = Dialog(activity)
            customDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: LayoutSettingsPasswordDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.layout_settings_password_dialog, null, false
            )
            customDialog?.setContentView(binding.root)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(customDialog?.window?.attributes)
            lp.width = getDimension(activity as Activity, 300.00)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            customDialog?.window?.attributes = lp
            customDialog?.setCanceledOnTouchOutside(isCancellable ?: true)
            customDialog?.setCancelable(isCancellable ?: true)

            binding.btnAlertPositive.text = "Confirm"

            val appSalt = byteArrayOf(17, 43, 99, 82, 55, 28, 40, 90)
            val masterPassword =  Global.getMasterKey(activity,appSalt)

            println("here is master password $masterPassword")

            binding.txtId.text = "Id : ${activity.getMasterKey()?.masterKey?.substring(11)}"

            binding.ivPasswordEye.setOnClickListener {
                if (binding.edtPassword.transformationMethod == null) {
                    binding.edtPassword.transformationMethod = PasswordTransformationMethod()
                    binding.edtPassword.setSelection(binding.edtPassword.text?.length ?: 0)
                    binding.ivPasswordEye.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_eye))
                } else {
                    binding.edtPassword.transformationMethod = null
                    binding.edtPassword.setSelection(binding.edtPassword.text?.length ?: 0)
                    binding.ivPasswordEye.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_eye_closed))
                }
            }

            binding.ivCancel.setOnClickListener {
                customDialog?.dismiss()
            }

            binding.btnAlertPositive.setOnClickListener {
                if (binding.edtPassword.text.toString() == masterPassword){
                    customDialog?.dismiss()
                    alertDialogInterface.onMasterYesClick()
                }
                else if (binding.edtPassword.text.toString() == activity.getAppPassword()?.appPassword){
                    customDialog?.dismiss()
                    alertDialogInterface.onYesClick()
                }else {
                    Toast.makeText(activity,"Invalid password", Toast.LENGTH_SHORT).show()
                }
            }
            customDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showCodeDialog(
        activity: Context,
        code : String ?= "",
        isCancellable: Boolean? = true,
        alertDialogInterface: AlertDialogInterface,
    ) {
        try {
            codeDialog = Dialog(activity)
            codeDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            codeDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: LayoutGenerateCodeDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.layout_generate_code_dialog, null, false
            )
            codeDialog?.setContentView(binding.root)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(codeDialog?.window?.attributes)
            lp.width = getDimension(activity as Activity, 300.00)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            codeDialog?.window?.attributes = lp
            codeDialog?.setCanceledOnTouchOutside(isCancellable ?: true)
            codeDialog?.setCancelable(isCancellable ?: true)

            println("herer is code 111 $code")

            binding.txtCode.text = addSpacesBetweenLetters(code ?: "")

            if (code?.isEmpty() == true){
                binding.btnAlertPositive.text = activity.getString(R.string.label_generate)
            }else{
                binding.btnAlertPositive.text = activity.getString(R.string.label_regenerate)
            }

            binding.ivCancel.setOnClickListener {
                codeDialog?.dismiss()
            }

            binding.btnAlertPositive.setOnClickListener {
                alertDialogInterface.onYesClick()
                binding.txtCode.text = addSpacesBetweenLetters(code ?: "")
                codeDialog?.dismiss()
            }
            codeDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showChangeAllPasswordDialog(
        activity: Context,
        alertDialogInterface: AlertDialogInterface,
    ) {
        try {
            changeAllPasswordDialog = Dialog(activity)
            changeAllPasswordDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            changeAllPasswordDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: LayoutChangePasswordDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.layout_change_password_dialog, null, false
            )
            changeAllPasswordDialog?.setContentView(binding.root)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(changeAllPasswordDialog?.window?.attributes)
            lp.width = getDimension(activity as Activity, 300.00)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            changeAllPasswordDialog?.window?.attributes = lp
            changeAllPasswordDialog?.setCanceledOnTouchOutside(true)
            changeAllPasswordDialog?.setCancelable(true)

            binding.ivOldPasswordEye.setOnClickListener {
                Extensions.showPassword(
                    activity,binding.edtOldPassword, imageView = binding.ivOldPasswordEye
                )
            }

            binding.ivNewPasswordEye.setOnClickListener {
                Extensions.showPassword(
                    activity,binding.edtNewPassword, imageView = binding.ivNewPasswordEye
                )
            }

            binding.ivConfirmPasswordEye.setOnClickListener {
                Extensions.showPassword(
                    activity,binding.edtConfirmPassword, imageView = binding.ivConfirmPasswordEye
                )
            }


            binding.ivCancel.setOnClickListener {
                changeAllPasswordDialog?.dismiss()
            }

            binding.btnAlertPositive.setOnClickListener {
                if(binding.edtNewPassword.text.toString() != binding.edtConfirmPassword.text.toString()){
                    Toast.makeText(activity,"Both Passwords don't match", Toast.LENGTH_SHORT).show()
                }else if (binding.edtOldPassword.text.toString() != activity.getAppPassword()?.appPassword){
                    Toast.makeText(activity,"Old Password is not correct", Toast.LENGTH_SHORT).show()
                }else {
                    changeAllPasswordDialog?.dismiss()
                    alertDialogInterface.onSubmitPasswordClick(binding.edtConfirmPassword.text.toString())
                }
            }
            changeAllPasswordDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showChangePasswordDialog(
        activity: Context,
        alertDialogInterface: AlertDialogInterface,
    ) {
        try {
            changePasswordDialog = Dialog(activity)
            changePasswordDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            changePasswordDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: LayoutChangePasswordNewDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.layout_change_password_new_dialog, null, false
            )
            changePasswordDialog?.setContentView(binding.root)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(changePasswordDialog?.window?.attributes)
            lp.width = getDimension(activity as Activity, 300.00)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            changePasswordDialog?.window?.attributes = lp
            changePasswordDialog?.setCanceledOnTouchOutside(true)
            changePasswordDialog?.setCancelable(true)


            binding.ivCancel.setOnClickListener {
                changePasswordDialog?.dismiss()
            }

            binding.btnAlertPositive.setOnClickListener {
                if (binding.edtNewPassword.text.toString().isEmpty()){
                    Toast.makeText(activity,"Please enter password", Toast.LENGTH_SHORT).show()
                }else {
                    changePasswordDialog?.dismiss()
                    alertDialogInterface.onSubmitPasswordClick(binding.edtNewPassword.text.toString())
                }
            }
            changePasswordDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showCustomAlert(
        activity: Context,
        title: String = "",
        msg: String = "",
        yesBtn: String,
        noBtn: String,
        singleBtn: Boolean = false,
        isCancellable: Boolean? = true,
        alertDialogInterface: AlertDialogInterface,
    ) {
        try {
            customDialog = Dialog(activity)
            customDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: LayoutCustomAlertBinding = DataBindingUtil.inflate(
                LayoutInflater.from(activity),
                R.layout.layout_custom_alert, null, false
            )
            customDialog?.setContentView(binding.root)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(customDialog?.window?.attributes)
            lp.width = getDimension(activity as Activity, 300.00)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            customDialog?.window?.attributes = lp
            customDialog?.setCanceledOnTouchOutside(isCancellable ?: true)
            customDialog?.setCancelable(isCancellable ?: true)


            binding.txtAlertTitle.text = title
            binding.txtAlertMessage.text = msg
            binding.btnAlertNegative.text = noBtn
            binding.btnAlertPositive.text = yesBtn

            binding.btnAlertNegative.visibility = if (singleBtn) View.GONE else View.VISIBLE
            binding.btnAlertNegative.setOnClickListener {
                customDialog?.dismiss()
                alertDialogInterface.onNoClick()
            }
            binding.btnAlertPositive.setOnClickListener {
                customDialog?.dismiss()
                alertDialogInterface.onYesClick()
            }
            customDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addSpacesBetweenLetters(input: String): String {
        // Convert the string to a list of characters, join them with spaces, and convert back to string
        return input.toCharArray().joinToString("   ")
    }


}
