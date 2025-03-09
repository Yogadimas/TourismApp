package com.yogadimas.tourismapp.auth.pin

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.yogadimas.tourismapp.R
import com.yogadimas.tourismapp.auth.AuthActivity
import com.yogadimas.tourismapp.databinding.ActivityAuthCreatePinBinding
import com.yogadimas.tourismapp.databinding.LayoutGridNumberBinding
import com.yogadimas.tourismapp.core.R as coreR

class AuthCreatePinActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAuthCreatePinBinding
    private val activityContext = this@AuthCreatePinActivity

    private val pinBuilder = StringBuilder()
    private val pinMaxLength = 6
    private val pinRange = 1..pinMaxLength
    private val placeholderSingleUnderscore = '_'

    private var isHidden = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupAuth()
    }

    private fun setupView() {
        binding = ActivityAuthCreatePinBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupChip()
    }


    private fun setupAuth() = binding.layoutGridNumber.apply {
        updatePINText(pinBuilder.setLength(0).toString(), 0, isHidden)
        setupNumberGrid()
        btnBackspace.setOnClickListener { if (pinBuilder.isNotEmpty()) backspacePINBuilder() }
        btnAuthNavigateToFingerprint.setOnClickListener { navigateToAuthActivity() }
        onBackPressedDispatcher.addCallback(activityContext, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToAuthActivity()
            }
        })

    }

    private fun LayoutGridNumberBinding.setupNumberGrid() {
        val buttons = listOf(
            btn0, btn1, btn2, btn3, btn4,
            btn5, btn6, btn7, btn8, btn9
        )
        buttons.forEach { button ->
            button.setOnClickListener {
                if (pinBuilder.length < pinMaxLength) appendPINBuilder(button.text.toString())
            }
        }
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

    private fun checkPinCreateButtonStatus(status: Boolean) = binding.btnAuthPinCreate.apply {
        isVisible = status
        isEnabled = isVisible
        if (isVisible) setOnClickListener { navigateToConfirmPin(pinBuilder.toString()) }
    }

    private fun updatePINText(pin: String, digitCount: Int = 0, isHidden: Boolean = true) =
        binding.tvAuthPinCreate.apply {
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

    private fun navigateToConfirmPin(pin: String) {
        val intent = Intent(activityContext, AuthConfirmPinActivity::class.java).apply {
            putExtra(AuthConfirmPinActivity.EXTRA_PIN, pin)
        }
        startActivity(intent)
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(activityContext, AuthActivity::class.java).apply {
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
}