package com.yogadimas.tourismapp.core.domain.repository.auth

import androidx.fragment.app.FragmentActivity
import com.yogadimas.tourismapp.core.data.auth.AuthResource
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    fun isBiometricAvailable(): Flow<Boolean>
    fun authenticate(activity: FragmentActivity): Flow<AuthResource>
    suspend fun savePin(pin: String)
    fun getPin(): Flow<String?>
}