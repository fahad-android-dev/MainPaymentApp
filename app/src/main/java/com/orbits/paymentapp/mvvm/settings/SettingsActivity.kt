package com.orbits.paymentapp.mvvm.settings

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
import com.orbits.paymentapp.helper.PrefUtils.getUserDataResponse
import com.orbits.paymentapp.helper.PrefUtils.setUserDataResponse
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
            title = "Settings",
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
    }

    private fun showGenerateCodeDialog() {
        Dialogs.showCodeDialog(
            activity = this,
            code = getUserDataResponse()?.code ?: "",
            alertDialogInterface = object : AlertDialogInterface {
                override fun onYesClick() {
                    Dialogs.showCustomAlert(
                        activity = this@SettingsActivity,
                        msg = "Are you sure you want to generate a new code?",
                        yesBtn = "Yes",
                        noBtn = "No",
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