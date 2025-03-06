package com.yogadimas.tourismapp.core.data.auth.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.yogadimas.tourismapp.core.R
import com.yogadimas.tourismapp.core.data.auth.AuthResource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Executor

class BiometricAuthManager(private val context: Context) {

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager
                .Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(activity: FragmentActivity, executor: Executor): Flow<AuthResource> =
        callbackFlow {
            val biometricPrompt = BiometricPrompt(activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        trySend(AuthResource.Success())
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        trySend(AuthResource.Error(errString.toString()))
                    }

                    override fun onAuthenticationFailed() {
                        trySend(AuthResource.Error(context.getString(R.string.authentication_failed_please_try_again)))
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(context.getString(R.string.biometric_prompt_title))
                .setSubtitle(context.getString(R.string.biometric_prompt_subtitle))
                .setDescription(context.getString(R.string.biometric_prompt_description))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .setNegativeButtonText(context.getString(R.string.cancel))
                .build()

            biometricPrompt.authenticate(promptInfo)

            awaitClose { }
        }
}


