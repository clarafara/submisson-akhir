package com.ara.storyappdicoding1.view.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ara.storyappdicoding1.R
import com.ara.storyappdicoding1.databinding.ActivityMainBinding
import com.ara.storyappdicoding1.view.adapter.StoryAdapter
import com.ara.storyappdicoding1.view.ViewModelFactory
import com.ara.storyappdicoding1.view.adapter.LoadingStateAdapter
import com.ara.storyappdicoding1.view.add.AddStoryActivity
import com.ara.storyappdicoding1.view.maps.MapsActivity
import com.ara.storyappdicoding1.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel(savedInstanceState)
        setupAction()
        supportActionBar?.elevation = 0f
        supportActionBar?.setTitle(R.string.app_name)
    }

    private fun setupView() {

        binding.rvUsers.setHasFixedSize(false)
        binding.rvUsers.isNestedScrollingEnabled = false
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
    }

    private fun setupViewModel(savedInstanceState: Bundle?) {
        mainViewModel.getUser().observe(this) { user ->
            Log.d("MainActivity", "user: $user")
            if (user.isLogin) {
                binding.root.visibility = View.VISIBLE

                if (savedInstanceState == null) {
                    fetchData("Bearer ${user.token}")
                }

            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.sign_out)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        mainViewModel.logout()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }

                val alert = builder.create()
                alert.show()
                true
            }
            R.id.action_maps -> {
                val intent = Intent(this, MapsActivity::class.java)
                mainViewModel.getUser().observe(this) { user ->
                    intent.putExtra(MapsActivity.EXTRA_TOKEN, user.token)
                }
                startActivity(intent)
            }
            R.id.action_setting->{
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchData(token: String) {
        val adapter = StoryAdapter()
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
        binding.rvUsers.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                if (loadStates.refresh is LoadState.Loading) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                    if (loadStates.refresh is LoadState.Error) {
                        if (adapter.itemCount < 1) {
                            binding.llError.visibility = View.VISIBLE
                            binding.btnRetry.setOnClickListener {
                                fetchData(token) // Retry fetching data
                            }
                        } else {
                            binding.llError.visibility = View.GONE
                        }
                    }
                }
            }
        }

        mainViewModel.getStories(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }
}