package com.example.wms_scan.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scanmate.extensions.*
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.scanCarton
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityScannerBinding

class ScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScannerBinding
    private var cameraRequestCode = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)

        if(
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),cameraRequestCode)
        }
        setupUi()
    }

    private fun initListeners(){

        binding.scanBtn.click {
            gotoActivity(ScannerCameraActivity::class.java)
        }

        binding.loginBtn.click {
            gotoActivity(LoginActivity::class.java)
        }
        binding.backBtn.click {
            onBackPressed()
        }

    }

    private fun setupUi(){
        when {
            intent.extras?.getBoolean(scanCarton) == true -> {
                binding.loginBtn.hide()
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}