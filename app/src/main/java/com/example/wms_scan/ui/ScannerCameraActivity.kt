package com.example.wms_scan.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.budiyev.android.codescanner.*
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.*
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityScannerCameraBinding

class ScannerCameraActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var dialog: CustomProgressDialog
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityScannerCameraBinding
    private var scannedData = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScannerCameraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        setTransparentStatusBarColor(R.color.transparent)
        viewModel = obtainViewModel(MainViewModel::class.java)
        codeScanner = CodeScanner(this,binding.cameraSurfaceView)

        val aniSlide: Animation = AnimationUtils.loadAnimation(this, R.anim.anim_hide)
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
                    if (isNetworkConnected(this))
                    {
                        try
                        {
                            dialog.dismiss()
                            Log.i("scanAllResponse","${it.data?.get(0)?.pilotCode}")
                        }
                        catch (e:Exception)
                        {
                            Log.i("scanAll","${e.message}")
                            toast("${e.message}")
                        }
                    }
                    else
                    {
                       toast("No internet")
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
        codeScanner.isTouchFocusEnabled = true
        codeScanner.isAutoFocusEnabled = true
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {

            runOnUiThread {
//                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                scannedData = it.text

                if ((scannedData.contains("PL")) or (scannedData.contains("SF")) or
                    (scannedData.contains("RK")) or (scannedData.contains("WH")) or
                    (scannedData.contains("MK")) or (scannedData.contains("PK")) or
                    (scannedData.contains("RM")))

                {
                    if (isNetworkConnected(this))
                    {
                        scannedProcess()
                    }
                    else
                    {
                        toast("No internet")
                    }

                    Log.i("scannedQR",scannedData)
                }
                else
                {
                   toast("Please scan correct code")
                    codeScanner.startPreview()
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

    private fun scannedProcess(){

        var warehouse = "${scannedData.substringAfter("L-").substringBefore("WH")}WH"

        var rack = if (scannedData.contains("RK")) "${scannedData.substringAfter("WH-").substringBefore("RK")}RK"
        else ""

        var shelve = if (scannedData.contains("SF")) "${scannedData.substringAfter("RK-").substringBefore("SF")}SF"
        else ""

        var pallete = if (scannedData.contains("PL")) "${scannedData.substringAfter("SF-").substringBefore("PL")}PL"
        else ""

//        Log.i("INTENT", "scannedData: $scannedData")
//        Log.i("INTENT", "warehouse: $warehouse")
//        Log.i("INTENT", "rack: $rack")
//        Log.i("INTENT", "shelve: $shelve")
//        Log.i("INTENT", "pallete: $pallete")

            if (scannedData.contains("PL"))
            {
                val intent =  Intent(this@ScannerCameraActivity, ShowAllHierarchy::class.java)
                intent.putExtra("p",pallete)
                intent.putExtra("scannedData",scannedData)
                startActivity(intent)
                Log.i("elect",pallete)
            }

            if (scannedData.contains("SF"))
            {
                val intent =  Intent(this@ScannerCameraActivity, ShowAllHierarchy::class.java)
                intent.putExtra("s",shelve)
                intent.putExtra("scannedData",scannedData)
                startActivity(intent)
                Log.i("elect",shelve)
            }
            if (scannedData.contains("RK"))
            {
                val intent =  Intent(this@ScannerCameraActivity, ShowAllHierarchy::class.java)
                intent.putExtra("r",rack)
                intent.putExtra("scannedData",scannedData)
                startActivity(intent)
                Log.i("elect",rack)
            }
            if (scannedData.contains("WH"))
            {
                //toast(warehouse)
                val intent =  Intent(this@ScannerCameraActivity, ShowAllHierarchy::class.java)
                intent.putExtra("w",warehouse)
                intent.putExtra("scannedData",scannedData)
                startActivity(intent)
                Log.i("elect",warehouse)
            }
        if (scannedData.contains("MK") or scannedData.contains("RM") or scannedData.contains("PK"))
        {
            val intent =  Intent(this@ScannerCameraActivity, ShowAllHierarchy::class.java)
            intent.putExtra("c",scannedData)
            intent.putExtra("analyticalKey",true)
            startActivity(intent)
        }
    }
}