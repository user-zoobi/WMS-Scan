package com.example.wms_scan.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.budiyev.android.codescanner.*
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.click
import com.example.scanmate.extensions.obtainViewModel
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityScannerCameraBinding
import com.google.android.gms.vision.barcode.BarcodeDetector

class ScannerCameraActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private val requestCodeCameraPermission = 1001
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var dialog: CustomProgressDialog
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityScannerCameraBinding
    private var palleteCode = ""

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        setTransparentStatusBarColor(R.color.transparent)
        viewModel = obtainViewModel(MainViewModel::class.java)
        codeScanner = CodeScanner(this,binding.cameraSurfaceView)

        val aniSlide: Animation = AnimationUtils.loadAnimation(this, R.anim.scanner_animation)
        binding.barcodeLine.startAnimation(aniSlide)
        dialog = CustomProgressDialog(this)
        codeScannerCamera()
        initObserver()

    }

    private fun initObserver(){


        viewModel.scanAll.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{
                    dialog.show()
                }
                Status.SUCCESS ->{
                    try
                    {
                        dialog.dismiss()
                        Log.i("scanAllResponse","${it.data?.get(0)?.pilotCode}")

                    }
                    catch (e:Exception)
                    {
                        Log.i("scanAll","${e.message}")
                    }

                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }
        })

    }

    private fun codeScannerCamera(){
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {

            runOnUiThread {
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                val scannedData = it.text

                Log.i("scannedQR",scannedData)

                var location    = ""
                var warehouse   = ""
                var rack        = ""
                var shelve      = ""
                var pallete     = ""

                if (scannedData.contains("L"))
                {
                    location = "${scannedData.substringBefore("L-")}L"
                    Log.i("LocCode","$location")
                }
                else
                {

                }

                if (scannedData.contains("WH"))
                {
                    warehouse = "${scannedData.substringAfter("L-").substringBefore("WH")}WH"
                    Log.i("whCode","${warehouse}")
                }
                else
                {

                }

                if (scannedData.contains("RK"))
                {
                    rack = "${scannedData.substringAfter("WH-").substringBefore("RK")}RK"
                    Log.i("rackCode","$rack")
                }
                else
                {

                }

                if (scannedData.contains("SF"))
                {
                    shelve = "${scannedData.substringAfter("RK-").substringBefore("SF")}SF"
                    Log.i("shelfCode",shelve)
                }
                else
                {

                }

                if (scannedData.contains("PL"))
                {
                    pallete = "${scannedData.substringAfter("SF-").substringBefore("PL")}PL"
                    palleteCode = pallete
                    Log.i("palletCode",pallete)
                }
                else
                {

                }

                val intent =  Intent(this@ScannerCameraActivity, ShowAllHierarchy::class.java)
                intent.putExtra("l",location)
                intent.putExtra("w",warehouse)
                intent.putExtra("r",rack)
                intent.putExtra("s",shelve)
                intent.putExtra("p",pallete)
                startActivity(intent)

                finish()

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