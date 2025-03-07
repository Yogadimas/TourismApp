package com.yogadimas.tourismapp.auth.pin

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.yogadimas.tourismapp.R
import com.yogadimas.tourismapp.auth.AuthViewModel
import com.yogadimas.tourismapp.databinding.ActivityAuthInsertPinBinding
import com.yogadimas.tourismapp.home.HomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.yogadimas.tourismapp.core.R as coreR

class AuthInsertPinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthInsertPinBinding
    private val activityContext = this@AuthInsertPinActivity

    private val authViewModel: AuthViewModel by viewModel()

    private val pinBuilder = StringBuilder()
    private val pinMaxLength = 6


    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupAuth()
    }


    private fun setupView() {
        binding = ActivityAuthInsertPinBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun setupAuth() = binding.layoutGridNumber.apply {
        getPIN { pinValue ->
            binding.btnAuthPinToCreate.apply {
                isVisible = pinValue.orEmpty().isEmpty()
                isEnabled = isVisible
                if (isVisible) setOnClickListener { navigateToCreatePinActivity() }
            }
        }
        val buttons = listOf(
            btn0, btn1, btn2, btn3, btn4,
            btn5, btn6, btn7, btn8, btn9
        )
        buttons.forEach { button ->
            button.setOnClickListener {
                if (pinBuilder.length < pinMaxLength) appendPINBuilder(button.text.toString())
                if (pinBuilder.length == pinMaxLength) savePin(pinBuilder.toString())
            }
        }
        btnBackspace.setOnClickListener { if (pinBuilder.isNotEmpty()) backspacePINBuilder() }
        btnAuthNavigateToFingerprint.setOnClickListener { finish() }
    }


    private fun updatePINIconsTint(digitCount: Int = 0) = binding.layoutAuthPinDigits.apply {
        val typedValuePrimary = TypedValue()
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValuePrimary, true)
        val colorPrimary = typedValuePrimary.data

        val typedValueDefault = TypedValue()
        theme.resolveAttribute(
            androidx.appcompat.R.attr.colorControlNormal,
            typedValueDefault,
            true
        )
        val defaultColor = ContextCompat.getColor(activityContext, typedValueDefault.resourceId)

        val pinIcons = listOf(
            ivAuthPinDigit1,
            ivAuthPinDigit2,
            ivAuthPinDigit3,
            ivAuthPinDigit4,
            ivAuthPinDigit5,
            ivAuthPinDigit6
        )

        pinIcons.forEachIndexed { index, imageView ->
            if (index < digitCount) {
                imageView.setColorFilter(colorPrimary, PorterDuff.Mode.SRC_IN)
            } else {
                imageView.setColorFilter(defaultColor, PorterDuff.Mode.SRC_IN)
            }
        }
    }

    private fun savePin(pin: String) {
        getPIN { pinValue ->
            if (pinValue != null) {
                if (pinValue != pin) {
                    removeAllPINBuilder()
                    showSnackBarInvalidPIN()
                } else {
                    authViewModel.savePIN(pin)
                    navigateToHomeActivity()
                }
            } else {
                alertCreateNewPin()
            }
        }
    }


    private fun getPIN(action: (pinValue: String?) -> Unit) {
        authViewModel.getPIN().observe(activityContext) { it -> action(it) }
    }

    private fun alertCreateNewPin() {
        if (alertDialog == null) {
            alertDialog = MaterialAlertDialogBuilder(
                activityContext,
                R.style.ThemeOverlay_App_MaterialAlertDialog_CreateANewPIN
            )
                .apply {
                    setCancelable(false)
                    setIcon(ContextCompat.getDrawable(activityContext, R.drawable.ic_pin))
                    setTitle(getString(R.string.pin_not_exist_title))
                    setMessage(getString(R.string.pin_not_exist_message))
                    setPositiveButton(getString(R.string.create_pin_positive_button)) { _, _ ->
                        alertDialog = null
                        navigateToCreatePinActivity()
                    }
                    setNegativeButton(getString(coreR.string.cancel)) { _, _ ->
                        alertDialog = null
                        backspacePINBuilder()
                        return@setNegativeButton
                    }
                }.create()
        }
        if (alertDialog?.isShowing == false) alertDialog?.show()
    }

    private fun dismissAlertDialog() {
        alertDialog?.dismiss()
        alertDialog = null
    }

    private fun showSnackBarInvalidPIN() {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.invalid_pin_message),
            Snackbar.LENGTH_LONG
        )
        val typeface = ResourcesCompat.getFont(activityContext, coreR.font.comfortaa_regular)
        val textView =
            snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTypeface(typeface)
        snackBar.show()
    }


    private fun appendPINBuilder(value: String) {
        pinBuilder.append(value)
        updatePINIconsTint(pinBuilder.length)
    }

    private fun removeAllPINBuilder() {
        pinBuilder.setLength(0)
        updatePINIconsTint(pinBuilder.length)
    }

    private fun backspacePINBuilder() {
        pinBuilder.deleteCharAt(pinBuilder.length - 1)
        updatePINIconsTint(pinBuilder.length)
    }

    private fun navigateToCreatePinActivity() {
        removeAllPINBuilder()
        val intent = Intent(activityContext, AuthCreatePinActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(activityContext, HomeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        dismissAlertDialog()
    }

}




