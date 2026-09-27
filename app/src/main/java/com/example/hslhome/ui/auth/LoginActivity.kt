package com.example.hslhome.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.hslhome.data.repository.AuthResult
import com.example.hslhome.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendCode.setOnClickListener {
            Toast.makeText(this, "验证码已发送（演示，固定 1234）", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogin.setOnClickListener {
            viewModel.login(binding.etPhone.text.toString(), binding.etCode.text.toString())
        }

        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is AuthResult.Ok -> {
                    setResult(Activity.RESULT_OK, Intent())
                    finish()
                }
                is AuthResult.Fail ->
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                null -> Unit
            }
        }
    }
}
