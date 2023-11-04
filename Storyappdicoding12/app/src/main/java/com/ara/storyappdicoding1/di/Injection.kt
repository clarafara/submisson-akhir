package com.ara.storyappdicoding1.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ara.storyappdicoding1.data.local.UserPreference
import com.ara.storyappdicoding1.data.remote.api.ApiConfig
import com.ara.storyappdicoding1.data.remote.repository.StoryRepository

//object Injection {
//    fun provideStoryRepository(): StoryRepository {
//        val apiService = ApiConfig.getApiService()
//        return StoryRepository.getInstance(apiService)
//    }
//
//    fun provideUserRepository(context: Context): UserRepository {
//        val pref = UserPreference.getInstance(context.dataStore)
//        val apiService = ApiConfig.getApiService()
//        return UserRepository.getInstance(apiService, pref)
//    }

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService, pref)
    }
}