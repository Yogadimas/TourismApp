package com.yogadimas.tourismapp.core.domain.usecase.auth

import androidx.fragment.app.FragmentActivity
import com.yogadimas.tourismapp.core.data.auth.AuthResource
import com.yogadimas.tourismapp.core.domain.repository.auth.IAuthRepository
import kotlinx.coroutines.flow.Flow


class AuthInteractor(private val authRepository: IAuthRepository) : AuthUseCase {

    override fun isBiometricAvailable(): Flow<Boolean> =
        authRepository.isBiometricAvailable()

    override fun authenticate(activity: FragmentActivity): Flow<AuthResource> {
        return authRepository.authenticate(activity)
    }


    override suspend fun savePin(pin: String) {
        authRepository.savePin(pin)
    }

    override fun getPin(): Flow<String?> = authRepository.getPin()
}