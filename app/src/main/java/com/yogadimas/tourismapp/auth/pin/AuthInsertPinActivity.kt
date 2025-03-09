package com.yogadimas.tourismapp.auth.pin

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.yogadimas.tourismapp.R
import com.yogadimas.tourismapp.auth.AuthActivity
import com.yogadimas.tourismapp.auth.AuthViewModel
import com.yogadimas.tourismapp.databinding.ActivityAuthInsertPinBinding
import com.yogadimas.tourismapp.databinding.LayoutGridNumberBinding
import com.yogadimas.tourismapp.home.HomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.appcompat.R as appCompatR
import com.google.android.material.R as materialR
import com.yogadimas.tourismapp.core.R as coreR

class AuthInsertPinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthInsertPinBinding
    private val activityContext = this@AuthInsertPinActivity

    private val authViewModel: AuthViewModel by viewModel()

    private val pinBuilder = StringBuilder()
    private val pinMaxLength = 6

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
                if (pinBuilder.length == pinMaxLength) savePin(pinBuilder.toString())
            }
        }
    }


    private fun updatePINIconsTint(digitCount: Int = 0) = binding.layoutAuthPinDigits.apply {
        val typedValuePrimary = TypedValue()
        theme.resolveAttribute(appCompatR.attr.colorPrimary, typedValuePrimary, true)
        val colorPrimary = typedValuePrimary.data

        val typedValueDefault = TypedValue()
        theme.resolveAttribute(
            appCompatR.attr.colorControlNormal,
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
            if (pinValue != null && pinValue == pin) {
                authViewModel.savePIN(pin)
                navigateToHomeActivity()
            } else {
                removeAllPINBuilder()
                showSnackBarInvalidPIN()
            }
        }
    }


    private fun getPIN(action: (pinValue: String?) -> Unit) {
        authViewModel.getPIN().observe(activityContext) { action(it) }
    }


    private fun showSnackBarInvalidPIN() {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.invalid_pin_message),
            Snackbar.LENGTH_LONG
        )
        val typeface = ResourcesCompat.getFont(activityContext, coreR.font.comfortaa_regular)
        val textView =
            snackBar.view.findViewById<TextView>(materialR.id.snackbar_text)
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

    private fun navigateToHomeActivity() {
        val intent = Intent(activityContext, HomeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun navigateToAuthActivity() {
        val intent = Intent(activityContext, AuthActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

}




