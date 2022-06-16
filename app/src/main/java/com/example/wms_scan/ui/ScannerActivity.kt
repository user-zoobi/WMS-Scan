package com.example.wms_scan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.scanmate.extensions.*
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.scanCarton
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityScannerBinding

class ScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
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