package com.ara.storyappdicoding1.view.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.ara.storyappdicoding1.R
import com.ara.storyappdicoding1.databinding.ActivityAddStoryBinding
import com.ara.storyappdicoding1.view.ViewModelFactory
import com.ara.storyappdicoding1.view.utils.reduceFileImage
import com.ara.storyappdicoding1.view.utils.uriToFile
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.ara.storyappdicoding1.view.main.MainActivity
import com.ara.storyappdicoding1.view.utils.createTempFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private var getFile: File? = null

    private lateinit var binding: ActivityAddStoryBinding
    private val addViewModel by viewModels<AddViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat: Double? = null
    private var lon: Double? = null

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.request_denied),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private lateinit var currentPhotoPath: String
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
//                rotateFile(file)
                getFile = file
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.ivPreview.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupView()
        setupViewModel()
        setupAction()
    }

    private fun setupView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupViewModel() {
        addViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        addViewModel.errorResponse.observe(this) {
            if (it.error) {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.apply {
            btnCamera.setOnClickListener { startCamera() }
            btnGallery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener { uploadImage() }
            cbLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getMyLastLocation()
                } else {
                    lat = null
                    lon = null
                }
            }
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createTempFile(application).also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this,
                "com.ara.storyappdicoding1",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description =
                binding.etCaption.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImage = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImage
            )

            addViewModel.getUser().observe(this) { user ->
                Log.d("AddActivity", "lat: $lat, lon: $lon")
                addViewModel.addNewStory(
                    "Bearer ${user.token}",
                    imageMultipart,
                    description,
                    lat,
                    lon
                )
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.email_empty), Toast.LENGTH_SHORT)
                .show()
        }
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = (if (isLoading) View.VISIBLE else View.GONE)
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }

                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }

                else -> {
                    binding.cbLocation.isChecked = false
                }
            }
        }
}