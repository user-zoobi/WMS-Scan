package com.example.wms_scan.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.scanmate.extensions.gotoActivity
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.extensions.toast
import com.example.scanmate.util.LocalPreferences
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityCartonDetailBinding
import com.example.wms_scan.databinding.ActivityCreatecartonScanBinding

class CreateCartonScanActivity : AppCompatActivity()
{
    private lateinit var codeScanner: CodeScanner
    private lateinit var binding: ActivityCreatecartonScanBinding
    private var scannedData = ""
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatecartonScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView()
    {
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        codeScanner = CodeScanner(this,binding.cameraSurfaceView)
        codeScannerCamera()
    }

    private fun codeScannerCamera(){
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        codeScanner.isTouchFocusEnabled = true
        codeScanner.isAutoFocusEnabled = true
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {

            runOnUiThread {
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                scannedData = it.text
                Log.i("createCartonFlag", scannedData)

                if (scannedData.contains("PL"))
                {
                    gotoActivity(CartonDetailActivity::class.java, "isScannedKey",true)
                    LocalPreferences.put(this, "createCartonScanValue",scannedData)
                }
            }
        }

        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        binding.cameraSurfaceView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}