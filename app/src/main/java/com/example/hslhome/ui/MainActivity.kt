package com.example.hslhome.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.hslhome.databinding.ActivityMainBinding
import com.example.hslhome.ui.auth.AuthViewModel
import com.example.hslhome.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    private val loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                renderHome()
            } else {
                // 用户取消登录，退出
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (authViewModel.isLoggedIn()) {
            renderHome()
        } else {
            loginLauncher.launch(Intent(this, LoginActivity::class.java))
        }
    }

    private fun renderHome() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        // HomeFragment 已通过 FragmentContainerView 的 android:name 静态挂载
    }
}
