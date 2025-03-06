package com.yogadimas.tourismapp.core.data.auth

import androidx.fragment.app.FragmentActivity
import com.yogadimas.tourismapp.core.data.auth.biometric.BiometricAuthManager
import com.yogadimas.tourismapp.core.data.auth.datastore.AuthPreferences
import com.yogadimas.tourismapp.core.domain.repository.auth.IAuthRepository
import com.yogadimas.tourismapp.core.utils.AppExecutors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(
    private val biometricAuthManager: BiometricAuthManager,
    private var dataStore: AuthPreferences,
    private val appExecutors: AppExecutors,
) : IAuthRepository {

    override fun isBiometricAvailable(): Flow<Boolean> = flow {
        emit(biometricAuthManager.isBiometricAvailable())
    }

    override fun authenticate(activity: FragmentActivity): Flow<AuthResource> {
        return biometricAuthManager.authenticate(activity, appExecutors.mainThread())
    }

    override suspend fun savePin(pin: String) {
        dataStore.savePIN(pin)
    }

    override fun getPin(): Flow<String?> = dataStore.getPIN()
}