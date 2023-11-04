package com.ara.storyappdicoding1.view.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ara.storyappdicoding1.data.remote.repository.StoryRepository
import com.ara.storyappdicoding1.data.remote.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SignupViewModel (private val repository: StoryRepository) : ViewModel() {

    companion object {
        private const val TAG = "RegisterViewModel"
    }

    private val _registerResponse = MutableLiveData<RegisterResponse>()
    val registerResponse: LiveData<RegisterResponse> = _registerResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, password: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            try {
                //get success message
                val response = repository.register(name, email, password)
                _isLoading.postValue(false)
                _registerResponse.postValue(response)
                Log.d(TAG, "onSuccess: ${response.message}")
            } catch (e: HttpException) {
                //get error message
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
                val errorMessage = errorBody.message
                _isLoading.postValue(false)
                _registerResponse.postValue(errorBody)
                Log.d(TAG, "onError: $errorMessage")
            }
        }
    }
}