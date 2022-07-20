package com.example.wms_scan.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.obtainViewModel
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.extensions.toast
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityScannerCameraBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class ScannerCameraActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private val requestCodeCameraPermission = 1001
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var dialog: CustomProgressDialog
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityScannerCameraBinding
    private var palleteCode = ""
//    private lateinit var busLocNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        setTransparentStatusBarColor(R.color.transparent)
        viewModel = obtainViewModel(MainViewModel::class.java)

        codeScannerCamera()

        val aniSlide: Animation = AnimationUtils.loadAnimation(this, R.anim.scanner_animation)
        binding.barcodeLine.startAnimation(aniSlide)
        dialog = CustomProgressDialog(this)
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
        codeScanner = CodeScanner(this,binding.cameraSurfaceView)
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

                if (scannedData.contains("WH"))
                {
                    warehouse = "${scannedData.substringAfter("L-").substringBefore("WH")}WH"
                    Log.i("whCode","${warehouse}")
                }

                if (scannedData.contains("RK"))
                {
                    rack = "${scannedData.substringAfter("WH-").substringBefore("RK")}RK"
                    Log.i("rackCode","$rack")
                }

                if (scannedData.contains("SF"))
                {
                    shelve = "${scannedData.substringAfter("RK-").substringBefore("SF")}SF"
                    Log.i("shelfCode",shelve)
                }

                if (scannedData.contains("PL"))
                {
                    pallete = "${scannedData.substringAfter("SF-").substringBefore("PL")}PL"
                    palleteCode = pallete
                    Log.i("palletCode",pallete)
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