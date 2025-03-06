package com.yogadimas.tourismapp.core.data.auth

sealed class AuthResource(val message: String? = null) {
    class Success() : AuthResource()
    class Error(message: String? = null) : AuthResource(message)
}
