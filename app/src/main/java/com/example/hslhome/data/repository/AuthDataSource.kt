package com.example.hslhome.data.repository

import kotlinx.coroutines.delay

/**
 * 鉴权数据源（接口，便于替换成真实后端）。
 * 说明：这里演示的是【你自己的账号体系】，登录/发 token 都应打到你自己的合法服务端。
 */
interface AuthDataSource {
    suspend fun login(phone: String, code: String): AuthResult
    fun savedToken(): String?
    fun saveToken(token: String?, phone: String?)
    fun logout()
}

sealed interface AuthResult {
    data class Ok(val token: String, val phone: String) : AuthResult
    data class Fail(val message: String) : AuthResult
}

class MockAuthRepository : AuthDataSource {

    private val store = Prefs()

    override suspend fun login(phone: String, code: String): AuthResult {
        delay(500)
        return when {
            phone.length != 11 -> AuthResult.Fail("手机号格式不正确")
            code != "1234" -> AuthResult.Fail("验证码错误（演示固定 1234）")
            else -> AuthResult.Ok("mock-token-${System.currentTimeMillis()}", phone)
        }
    }

    override fun savedToken(): String? = store.token

    override fun saveToken(token: String?, phone: String?) {
        store.token = token
        store.phone = phone
    }

    override fun logout() {
        store.token = null
        store.phone = null
    }

    /** 内存版存储，真实项目换 DataStore/EncryptedSharedPreferences */
    private class Prefs {
        var token: String? = null
        var phone: String? = null
    }
}
