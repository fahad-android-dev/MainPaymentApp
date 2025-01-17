package com.orbits.paymentapp.mvvm.settings

import AppNavigation.navigateToMain
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.orbits.paymentapp.R
import com.orbits.paymentapp.databinding.ActivitySettingsBinding
import com.orbits.paymentapp.helper.AlertDialogInterface
import com.orbits.paymentapp.helper.BaseActivity
import com.orbits.paymentapp.helper.Dialogs
import com.orbits.paymentapp.helper.Dialogs.showCustomAlert
import com.orbits.paymentapp.helper.LocaleHelper
import com.orbits.paymentapp.helper.PrefUtils.getMasterKey
import com.orbits.paymentapp.helper.PrefUtils.getUserDataResponse
import com.orbits.paymentapp.helper.PrefUtils.setAppPassword
import com.orbits.paymentapp.helper.PrefUtils.setUserDataResponse
import com.orbits.paymentapp.helper.helper_model.PasswordModel
import com.orbits.paymentapp.helper.helper_model.UserDataModel
import com.orbits.paymentapp.helper.helper_model.UserResponseModel
import com.orbits.paymentapp.interfaces.CommonInterfaceClickEvent
import kotlin.random.Random

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_settings)

        initializeToolbar()
        setupClickListeners()
    }

    private fun initializeToolbar() {
        setUpToolbar(
            binding.layoutToolbar,
            title = getString(R.string.settings),
            isBackArrow = true,
            toolbarClickListener = object : CommonInterfaceClickEvent {
                override fun onToolBarListener(type: String) {
                    // Handle toolbar icon click if needed
                }
            }
        )
    }

    private fun setupClickListeners() {
        binding.txtGenerateCode.setOnClickListener {
            showGenerateCodeDialog()
        }

        binding.txtReconcile.setOnClickListener {
            val intent = Intent(this@SettingsActivity, ReconcileActivity::class.java)
            startActivity(intent)
        }

        binding.txtChangePassword.setOnClickListener {
            Dialogs.showChangeAllPasswordDialog(
                activity = this,
                alertDialogInterface = object : AlertDialogInterface{
                    override fun onSubmitPasswordClick(password: String) {
                        println("here is 222 ${getMasterKey()?.masterKey}")
                        setAppPassword(
                            result = PasswordModel(
                                appPassword = password
                            )
                        )
                    }
                }
            )
        }

        binding.txtChangeLanguage.setOnClickListener {
            showChangeLanguageAlert()
        }
    }

    private fun showChangeLanguageAlert() {
        showCustomAlert(
            activity = this@SettingsActivity,
            title = getString(R.string.alert_title_lang),
            msg = resources.getString(R.string.alert_language),
            yesBtn = resources.getString(R.string.yes_lang),
            noBtn = resources.getString(R.string.no_lang),
            alertDialogInterface = object : AlertDialogInterface {
                override fun onYesClick() {
                    LocaleHelper.changeLanguage(this@SettingsActivity)
                    navigateToMain{finish()}
                }

                override fun onNoClick() {}
            })
    }

    private fun showGenerateCodeDialog() {
        Dialogs.showCodeDialog(
            activity = this,
            code = getUserDataResponse()?.code ?: "",
            alertDialogInterface = object : AlertDialogInterface {
                override fun onYesClick() {
                    showCustomAlert(
                        activity = this@SettingsActivity,
                        msg = getString(R.string.are_you_sure_you_want_to_generate_a_new_code),
                        yesBtn = getString(R.string.yes),
                        noBtn = getString(R.string.label_no),
                        alertDialogInterface = object : AlertDialogInterface {
                            override fun onYesClick() {
                                setUserDataResponse(
                                    UserResponseModel(
                                        code = getUserDataResponse()?.code,
                                        data = UserDataModel(
                                            isCodeVerified = false
                                        )
                                    )
                                )

                                setUserDataResponse(
                                    UserResponseModel(
                                        code = generateRandomCode()
                                    )
                                )
                            }
                        }
                    )
                }
            }
        )
    }


    private fun generateRandomCode(): String {
        val charPool: List<Char> = ('A'..'Z') + ('0'..'9')

        return (1..6)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}