package com.example.hslhome

import android.app.Application
import com.example.hslhome.data.repository.AuthDataSource
import com.example.hslhome.data.repository.HomeDataSource
import com.example.hslhome.data.repository.MockAuthRepository
import com.example.hslhome.data.repository.MockHomeRepository

class App : Application() {

    lateinit var homeDataSource: HomeDataSource
        private set

    lateinit var authDataSource: AuthDataSource
        private set

    override fun onCreate() {
        super.onCreate()
        // 接入真实后端时，把这里替换成 RetrofitHomeRepository(apiService) / RetrofitAuthRepository(apiService)
        homeDataSource = MockHomeRepository()
        authDataSource = MockAuthRepository()
    }

    companion object {
        @JvmStatic
        lateinit var instance: App
            private set
    }

    init {
        instance = this
    }
}
