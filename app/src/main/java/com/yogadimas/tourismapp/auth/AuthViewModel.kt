package com.yogadimas.tourismapp.auth

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yogadimas.tourismapp.core.data.auth.AuthResource
import com.yogadimas.tourismapp.core.domain.usecase.auth.AuthUseCase
import kotlinx.coroutines.launch

class AuthViewModel(private val authUseCase: AuthUseCase) : ViewModel() {

    fun isBiometricAvailable(): LiveData<Boolean> {
        return authUseCase.isBiometricAvailable().asLiveData()
    }

    fun authenticate(activity: FragmentActivity): LiveData<AuthResource> {
        return authUseCase.authenticate(activity).asLiveData()
    }


    fun savePIN(pin: String) = viewModelScope.launch { authUseCase.savePin(pin) }

    fun getPIN(): LiveData<String?> = authUseCase.getPin().asLiveData()

}