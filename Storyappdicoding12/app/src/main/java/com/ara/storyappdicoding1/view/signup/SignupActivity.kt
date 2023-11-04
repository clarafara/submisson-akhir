package com.ara.storyappdicoding1.view.signup

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
import com.ara.storyappdicoding1.data.remote.repository.Result
import com.ara.storyappdicoding1.databinding.ActivitySignupBinding
import com.ara.storyappdicoding1.view.ViewModelFactory
import com.ara.storyappdicoding1.view.login.LoginActivity
import com.ara.storyappdicoding1.view.main.MainViewModel
//
//class SignupActivity : AppCompatActivity() {
//    private lateinit var binding: ActivitySignupBinding
//
//    private val viewModel by viewModels<MainViewModel> {
//        ViewModelFactory.getInstance(this)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySignupBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.signupButton.setOnClickListener { processRegister() }
//
//        setupView()
//        playAnimation()
//    }
//
//    private fun playAnimation() {
//        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
//            duration = 6000
//            repeatCount = ObjectAnimator.INFINITE
//            repeatMode = ObjectAnimator.REVERSE
//        }.start()
//
//        val titleTV = setOtherViewAnimation(binding.titleTextView)
//        val nameTV = setOtherViewAnimation(binding.nameEditText)
//        val nameET = setOtherViewAnimation(binding.nameEditTextLayout)
//        val emailTV = setOtherViewAnimation(binding.emailEditText)
//        val emailET = setOtherViewAnimation(binding.emailEditTextLayout)
//        val passTV = setOtherViewAnimation(binding.passwordEditText)
//        val passET = setOtherViewAnimation(binding.passwordEditTextLayout)
//        val signupBtn = setOtherViewAnimation(binding.signupButton)
//
//        AnimatorSet().apply {
//            playSequentially(titleTV, nameTV, nameET, emailTV, emailET, passTV, passET, signupBtn)
//            start()
//        }
//    }
//
//    private fun setOtherViewAnimation(view: View): ObjectAnimator {
//        return ObjectAnimator.ofFloat(view, View.ALPHA, 1f).apply {
//            duration = 500
//        }
//    }
//
//    private fun processRegister() {
//        binding.apply {
//            val name = nameEditText.text.toString()
//            val email = emailEditText.text.toString()
//            val password = passwordEditText.text.toString()
//
//            viewModel.register(name, email, password).observe(this@SignupActivity) { result ->
//                if (result != null) {
//                    when (result) {
//                        is Result.Loading -> {
//                            showLoading(true)
//                            signupButton.isEnabled = false
//                        }
//
//                        is Result.Success -> {
//                            showLoading(false)
//                            signupButton.isEnabled = true
//                            showToast(getString(R.string.akun_sukses_dibuat))
//                            Toast.makeText(
//                                this@SignupActivity,
//                                R.string.akun_sukses_dibuat,
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            moveLoginActivity()
//                        }
//
//                        is Result.Error -> {
//                            showLoading(false)
//                            signupButton.isEnabled = true
//                            showToast(getString(R.string.akun_gagal_dibuat))
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private fun showLoading(isLoading: Boolean) {
//        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }
//
//    private fun setupView() {
//        @Suppress("DEPRECATION")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            window.insetsController?.hide(WindowInsets.Type.statusBars())
//        } else {
//            window.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }
//        supportActionBar?.hide()
//    }
//
//    private fun moveLoginActivity() {
//        startActivity(Intent(this, LoginActivity::class.java))
//    }
//}

class SignupActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RegisterActivity"
    }

    private lateinit var binding: ActivitySignupBinding
    private val registerViewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setupViewModel()
        setupAction()
        playAnimation()
    }


    private fun setupViewModel() {
        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        registerViewModel.registerResponse.observe(this) {
            if (it.error == true) {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Register success", Toast.LENGTH_LONG).show()
                finish()
            }
        }
        supportActionBar?.hide()
    }


        private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleTV = setOtherViewAnimation(binding.titleTextView)
        val nameTV = setOtherViewAnimation(binding.nameEditText)
        val nameET = setOtherViewAnimation(binding.nameEditTextLayout)
        val emailTV = setOtherViewAnimation(binding.emailEditText)
        val emailET = setOtherViewAnimation(binding.emailEditTextLayout)
        val passTV = setOtherViewAnimation(binding.passwordEditText)
        val passET = setOtherViewAnimation(binding.passwordEditTextLayout)
        val signupBtn = setOtherViewAnimation(binding.signupButton)

        AnimatorSet().apply {
            playSequentially(titleTV, nameTV, nameET, emailTV, emailET, passTV, passET, signupBtn)
            start()
        }
    }

    private fun setOtherViewAnimation(view: View): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1f).apply {
            duration = 500
        }
    }
    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            Log.d(TAG, "btnRegister clicked")
            if (binding.nameEditText.text.toString().isEmpty()) {
                binding.nameEditTextLayout.error = resources.getString(R.string.name_empty)
            } else if (binding.emailEditText.text.toString().isEmpty()) {
                binding.emailEditTextLayout.error = resources.getString(R.string.email_empty)
            } else if (binding.passwordEditText.text.toString().isEmpty()) {
                binding.passwordEditTextLayout.error = resources.getString(R.string.password_empty)
            } else {
                if (binding.passwordEditTextLayout.error == null && binding.emailEditTextLayout.error == null) {
                    Log.d(TAG, "errorEnabled: ${binding.passwordEditTextLayout.error}")
                    registerViewModel.register(
                        binding.nameEditText.text.toString(),
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