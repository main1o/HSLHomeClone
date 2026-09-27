package com.example.hslhome.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hslhome.App
import com.example.hslhome.data.repository.AuthDataSource
import com.example.hslhome.data.repository.AuthResult
import kotlinx.coroutines.launch

class AuthViewModel(
    private val auth: AuthDataSource = App.instance.authDataSource
) : ViewModel() {

    private val _loginResult = MutableLiveData<AuthResult?>()
    val loginResult: LiveData<AuthResult?> = _loginResult

    fun isLoggedIn(): Boolean = !auth.savedToken().isNullOrEmpty()

    fun login(phone: String, code: String) {
        viewModelScope.launch {
            val result = auth.login(phone.trim(), code.trim())
            if (result is AuthResult.Ok) {
                auth.saveToken(result.token, result.phone)
            }
            _loginResult.value = result
        }
    }

    fun logout() = auth.logout()
}
