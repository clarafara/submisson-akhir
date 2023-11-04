package com.ara.storyappdicoding1.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ara.storyappdicoding1.data.local.UserModel
import com.ara.storyappdicoding1.data.remote.repository.StoryRepository
import com.ara.storyappdicoding1.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(private val repository: StoryRepository) :
    ViewModel() {

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return repository.getStories(token).cachedIn(viewModelScope)
    }

    fun getUser(): LiveData<UserModel> {
        return repository.getUser()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}