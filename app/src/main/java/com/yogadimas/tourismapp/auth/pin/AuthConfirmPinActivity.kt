package com.yogadimas.tourismapp.auth.pin

import android.content.Intent
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
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.yogadimas.tourismapp.R
import com.yogadimas.tourismapp.auth.AuthViewModel
import com.yogadimas.tourismapp.databinding.ActivityAuthConfirmPinBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.yogadimas.tourismapp.core.R as coreR

class AuthConfirmPinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthConfirmPinBinding
    private val activityContext = this@AuthConfirmPinActivity

    private val authViewModel: AuthViewModel by viewModel()

    private val pinBuilder = StringBuilder()
    private val pinMaxLength = 6
    private val pinRange = 1..pinMaxLength
    private val placeholderSingleUnderscore = '_'

    private var isHidden = true
    private var pin = ""

    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupAuth()
    }

    private fun setupView() {
        binding = ActivityAuthConfirmPinBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupObtainIntent()
        setupChip()
    }

    private fun setupObtainIntent() {
        pin = obtainIntent().orEmpty()
    }

    private fun setupChip() = binding.chipAuthPinVisibility.apply {
        val mediumTypeface = ResourcesCompat.getFont(activityContext, coreR.font.comfortaa_medium)
        typeface = mediumTypeface
        setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) chipCheckedState() else chipUnCheckedState()
        }

    }

    private fun Chip.chipUnCheckedState() {
        text = getString(R.string.show_pin)
        isHidden = true
        checkedIcon = null
        chipIcon = ContextCompat.getDrawable(activityContext, R.drawable.ic_visibility_off)
        updatePINText(pinBuilder.toString(), pinBuilder.length, isHidden = isHidden)
    }

    private fun Chip.chipCheckedState() {
        text = getString(R.string.hide_pin)
        isHidden = false
        chipIcon = null
        checkedIcon = ContextCompat.getDrawable(activityContext, R.drawable.ic_visibility_on)
        updatePINText(pinBuilder.toString(), pinBuilder.length, isHidden = isHidden)
    }

    private fun setupAuth() = binding.layoutGridNumber.apply {
        updatePINText(pinBuilder.setLength(0).toString(), 0, isHidden)
        val buttons = listOf(
            btn0, btn1, btn2, btn3, btn4,
            btn5, btn6, btn7, btn8, btn9
        )
        buttons.forEach { button ->
            button.setOnClickListener {
                if (pinBuilder.length < pinMaxLength) appendPINBuilder(button.text.toString())
            }
        }
        btnBackspace.setOnClickListener {
            if (pinBuilder.isNotEmpty()) backspacePINBuilder()
        }
        btnAuthNavigateToFingerprint.apply {
            setImageDrawable(
                ContextCompat.getDrawable(activityContext, coreR.drawable.ic_back_pin_create)
            )
            setOnClickListener { finish() }
        }

    }


    private fun checkPinCreateButtonStatus(status: Boolean) = binding.btnAuthPinConfirm.apply {
        isVisible = status
        isEnabled = isVisible
        if (isVisible) setOnClickListener { savePin(pinBuilder.toString()) }
    }

    private fun savePin(pinConfirm: String) {
        if (pin == pinConfirm) alertPINCreatedSuccessfully(pinConfirm) else showSnackBarInvalidPIN()
    }

    private fun updatePINText(pin: String, digitCount: Int = 0, isHidden: Boolean = true) =
        binding.tvAuthPinConfirm.apply {
            val typedValuePrimary = TypedValue()
            theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValuePrimary, true)
            val colorPrimary = typedValuePrimary.data

            val showValue = when (digitCount) {
                in pinRange -> pin.padEnd(pinMaxLength, placeholderSingleUnderscore)
                else -> getString(R.string.pin_placeholder)
            }
            val hideValue = when (digitCount) {
                in pinRange -> {
                    getString(R.string.placeholder_single_star).repeat(pin.length)
                        .padEnd(pinMaxLength, placeholderSingleUnderscore)
                }

                else -> getString(R.string.pin_placeholder)
            }
            val showOrHiddenValue = if (isHidden) hideValue else showValue
            text = showOrHiddenValue
            setTextColor(colorPrimary)

            checkPinCreateButtonStatus(pinBuilder.length == pinMaxLength)
        }

    private fun appendPINBuilder(value: String) {
        pinBuilder.append(value)
        updatePINText(pinBuilder.toString(), pinBuilder.length, isHidden)
    }

    private fun backspacePINBuilder() {
        pinBuilder.deleteCharAt(pinBuilder.length - 1)
        updatePINText(pinBuilder.toString(), pinBuilder.length, isHidden)
    }

    private fun obtainIntent(): String? = intent.getStringExtra(EXTRA_PIN)


    private fun alertPINCreatedSuccessfully(pinConfirm: String) {
        if (alertDialog == null) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(DELAY_LONG)
                dismissAlertDialog()
                authViewModel.savePIN(pinConfirm)
                navigateToInsertPinActivity()
            }
            alertDialog = MaterialAlertDialogBuilder(
                activityContext,
                R.style.ThemeOverlay_App_MaterialAlertDialog_CreateANewPIN
            )
                .apply {
                    setCancelable(false)
                    setIcon(ContextCompat.getDrawable(activityContext, R.drawable.ic_pin))
                    setTitle(getString(R.string.pin_success_created_title))
                    setMessage(getString(R.string.pin_success_created_message))
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
            getString(R.string.pin_confirmation_failed),
            Snackbar.LENGTH_LONG
        )
        val typeface = ResourcesCompat.getFont(activityContext, coreR.font.comfortaa_regular)
        val textView =
            snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTypeface(typeface)
        snackBar.show()
    }

    private fun navigateToInsertPinActivity() {
        val intent = Intent(activityContext, AuthInsertPinActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_PIN = "extra_pin"
        private const val DELAY_LONG = 2100L
    }
}