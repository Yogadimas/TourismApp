package com.yogadimas.tourismapp.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yogadimas.tourismapp.R
import com.yogadimas.tourismapp.auth.pin.AuthCreatePinActivity
import com.yogadimas.tourismapp.auth.pin.AuthInsertPinActivity
import com.yogadimas.tourismapp.core.data.auth.AuthResource
import com.yogadimas.tourismapp.databinding.ActivityAuthBinding
import com.yogadimas.tourismapp.home.HomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAuthBinding
    private val activityContext = this@AuthActivity

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupAction()
        setupFingerprint()
    }


    private fun setupView() {
        binding = ActivityAuthBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupAction() {
        binding.apply {
            btnAuthFingerprint.setOnClickListener { setupFingerprint() }
            btnAuthNavigateToPin.setOnClickListener { setupPIN() }
        }
    }

    private fun setupFingerprint() {
        authViewModel.isBiometricAvailable().observe(this) { available ->
            if (!available) {
                Toast.makeText(
                    activityContext,
                    getString(R.string.biometric_not_available),
                    Toast.LENGTH_SHORT
                ).show()
                setupPIN()
            }
        }
        authViewModel.authenticate(activityContext).observe(activityContext) { authResource ->
            when (authResource) {
                is AuthResource.Success -> navigateToHomeActivity()
                is AuthResource.Error -> Toast.makeText(
                    activityContext,
                    authResource.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }


    }

    private fun setupPIN() {
        getPIN { pinValue ->
            if (pinValue != null) {
                navigateToAuthInsertPinActivity()
            } else {
                navigateToAuthCreatePinActivity()
            }
        }
    }

    private fun getPIN(action: (pinValue: String?) -> Unit) {
        authViewModel.getPIN().observe(activityContext) { it -> action(it) }
    }

    private fun navigateToAuthCreatePinActivity() {
        startActivity(Intent(activityContext, AuthCreatePinActivity::class.java))
    }

    private fun navigateToAuthInsertPinActivity() {
        startActivity(Intent(activityContext, AuthInsertPinActivity::class.java))
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(activityContext, HomeActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }


}