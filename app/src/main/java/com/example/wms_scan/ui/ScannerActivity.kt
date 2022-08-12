package com.example.wms_scan.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scanmate.extensions.*
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.scanCarton
import com.example.scanmate.util.Utils.isNetworkConnected
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
            if (isNetworkConnected(this))
            {
                gotoActivity(ScannerCameraActivity::class.java)
            }
            else
            {
                toast("No internet")
            }

        }

        binding.searchManualTV.click {

            binding.scanOptionCont.gone()
            binding.searchManualTV.gone()
            binding.searchScanTV.visible()
            binding.manualOptionCont.visible()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        }

        binding.searchScanTV.click {

            binding.scanOptionCont.visible()
            binding.searchManualTV.visible()
            binding.searchScanTV.gone()
            binding.manualOptionCont.gone()

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