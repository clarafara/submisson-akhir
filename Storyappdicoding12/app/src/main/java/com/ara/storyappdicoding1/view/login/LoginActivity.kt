package com.ara.storyappdicoding1.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.ara.storyappdicoding1.R
import com.ara.storyappdicoding1.data.local.UserModel
import com.ara.storyappdicoding1.databinding.ActivityLoginBinding
import com.ara.storyappdicoding1.view.ViewModelFactory
import com.ara.storyappdicoding1.view.main.MainActivity
import com.ara.storyappdicoding1.data.remote.repository.Result
import com.ara.storyappdicoding1.view.main.MainViewModel


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupAction()
        setupView()
        setupViewModel()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.registerResponse.observe(this) {
            if (it.error == true) {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(this, "Login success", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

            private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleTV = setOtherViewAnimation(binding.titleTextView)
        val emailTV = setOtherViewAnimation(binding.emailEditText)
        val emailET = setOtherViewAnimation(binding.emailEditTextLayout)
        val passTV = setOtherViewAnimation(binding.passwordEditText)
        val passET = setOtherViewAnimation(binding.passwordEditTextLayout)
        val signupBtn = setOtherViewAnimation(binding.loginButton)

        AnimatorSet().apply {
            playSequentially(titleTV, emailTV, emailET, passTV, passET, signupBtn)
            start()
        }
    }

    private fun setOtherViewAnimation(view: View): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1f).apply {
            duration = 500
        }
    }
    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            if (binding.emailEditText.text.toString().isEmpty()) {
                binding.emailEditTextLayout.error = resources.getString(R.string.email_empty)
            } else if (binding.passwordEditText.text.toString().isEmpty()) {
                binding.passwordEditTextLayout.error = resources.getString(R.string.password_empty)
            } else {
                if (binding.passwordEditTextLayout.error == null && binding.emailEditTextLayout.error == null) {
                    loginViewModel.login(
                        binding.emailEditText.text.toString(),
                        binding.passwordEditText.text.toString()
                    )
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}