package com.ara.storyappdicoding1.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ara.storyappdicoding1.data.remote.repository.StoryRepository
import com.ara.storyappdicoding1.di.Injection
import com.ara.storyappdicoding1.view.add.AddViewModel
import com.ara.storyappdicoding1.view.login.LoginViewModel
import com.ara.storyappdicoding1.view.main.MainViewModel
import com.ara.storyappdicoding1.view.maps.MapsViewModel
import com.ara.storyappdicoding1.view.signup.SignupViewModel


class ViewModelFactory(private val repository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(this.repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(this.repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(this.repository) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(this.repository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(this.repository) as T
            }
            else -> {
                throw IllegalArgumentException("ViewModel Not Found: ${modelClass.name}")
            }
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}