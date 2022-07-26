package com.example.wms_scan.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.bumptech.glide.util.Util
import com.example.scanmate.data.callback.Status
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.data.response.GetShelfResponse
import com.example.scanmate.data.response.GetWarehouseResponse
import com.example.scanmate.data.response.UserLocationResponse
import com.example.scanmate.extensions.*
import com.example.scanmate.util.Constants
import com.example.scanmate.util.Constants.LogMessages.error
import com.example.scanmate.util.Constants.LogMessages.loading
import com.example.scanmate.util.Constants.Toast.NoInternetFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.carton.ScanCartonAdapter
import com.example.wms_scan.data.response.GetCartonResponse
import com.example.wms_scan.databinding.ActivityScanCartonBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException
import java.util.ArrayList

class ScanCartonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanCartonBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private var scannedValue = ""
    private lateinit var codeScanner: CodeScanner
    var pilotNo = 0
    var scannedPalletCode = ""
    var cartonCode = ""
    var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanCartonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        initListeners()
        setupUi()
        initObserver()

        if (ContextCompat.checkSelfPermission(
                this , android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            codeScannerCamera()
        }
        else
        {
            codeScannerCamera()
        }


        /**
         *  scanned qr code intent from { create qr code }
         */

        var palletCode  = intent.extras?.getString("scannedValue")
        Log.i("scannedCode","$palletCode")

        viewModel.palletHierarchy(
            Utils.getSimpleTextBody("$palletCode"),
            Utils.getSimpleTextBody("0")
        )
        viewModel.palletHierarchy.observe(this, Observer {

            when(it.status){

                Status.LOADING -> {}

                Status.SUCCESS ->
                {
                    Log.i("palletCode","${it.data?.get(0)?.pilotCode}")

                    val warehouse = it.data?.get(0)?.wHName.toString()
                    val  racks = it.data?.get(0)?.rackName.toString()
                    val shelf = it.data?.get(0)?.shelfName.toString()
                    val pallet = it.data?.get(0)?.pilotName.toString()
                    scannedPalletCode = it.data?.get(0)?.pilotCode.toString()

                    binding.WHTV.text = warehouse
                    binding.rackTV.text = racks
                    binding.shelfTV.text = shelf
                    binding.palletTV.text = pallet
                    binding.palletNameTV.text = pallet
                    binding.palletCodeTV.text = palletCode

                }
                Status.ERROR -> {}

            }
        })

    }

    private fun setupUi()
    {

        dialog = CustomProgressDialog(this)
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        codeScanner = CodeScanner(this,binding.cameraSurfaceView)
        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )

        binding.palletDetailCont.isEnabled = false

    }

    private fun clearPreferences(context: Context)
    {
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        finish()
    }

    @SuppressLint("MissingPermission")
    private fun initListeners()
    {
        binding.hierarchyTree.click {
            binding.surfaceCont.gone()
            binding.viewRV.visible()
            binding.palletDetailCont.visible()
        }

        binding.treeView.click {
            binding.qrScanCont.gone()
            binding.viewRV.visible()
            binding.palletDetailCont.isEnabled = false
        }

        binding.scanCont.click {
            binding.qrScanCont.gone()
            binding.surfaceCont.visible()
            binding.palletCont.isEnabled = false
            binding.palletDetailCont.isEnabled = false
            binding.hierarchyTree.isEnabled = false
            codeScanner.startPreview()
        }

        binding.closeIV.click {
            binding.surfaceCont.gone()
            binding.qrScanCont.visible()
            binding.palletCont.isEnabled = true
            binding.hierarchyTree.isEnabled = true
            binding.palletDetailCont.isEnabled = false
            codeScanner.stopPreview()
        }

        binding.palletCont.click {
            binding.surfaceCont.gone()
            binding.qrScanCont.gone()
            binding.viewRV.visible()
            binding.palletDetailCont.visible()
            binding.palletDetailCont.isEnabled = false
        }

        binding.backBtn.click {
            onBackPressed()
        }

    }

    private fun initObserver()
    {
        //GET CARTON

        viewModel.getCartonDetails.observe(this@ScanCartonActivity, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{

                    it.let {
                        if(Utils.isNetworkConnected(this))
                        {
                            try
                            {
                                if (it.data?.get(0)?.status == true)
                                {
                                    status = it.data[0].status.toString()
                                    val intent = Intent(this, CartonDetailActivity::class.java)
                                    intent.putExtra("scanAnalyticalNum",scannedValue)
                                    intent.putExtra("palletCode",scannedPalletCode)
                                    intent.putExtra("isExist",it.data[0].isExist)
                                    startActivity(intent)


                                }
                                else
                                {
                                    Log.i("getCartonDetails","${Exception().message}")
                                    toast(it.data?.get(0)?.error.toString())
                                }
                            }
                            catch (e:Exception)
                            {
                                Log.i("getCartonDetails","${e.message}")
                            }
                        }
                    }
                }

                Status.ERROR ->{ }

            }
        })

    }


    private fun codeScannerCamera() {

            // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.isTouchFocusEnabled = true
        codeScanner.isAutoFocusEnabled = true
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {

            runOnUiThread {
                    scannedValue = it.text

                    Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()

                    //Don't forget to add this line printing value or finishing activity must run on main thread
                    runOnUiThread {

                        when {
                            scannedValue.contains("L") -> {
                                toast("No record found")
                                codeScanner.startPreview()
                            }
                            scannedValue.contains("WH") -> {
                                toast("No record found")
                                codeScanner.startPreview()
                            }
                            scannedValue.contains("RK") -> {
                                toast("No record found")
                                codeScanner.startPreview()
                            }
                            scannedValue.contains("SF") -> {
                                toast("No record found")
                                codeScanner.startPreview()
                            }
                            scannedValue.contains("PL") -> {
                                toast("No record found")
                                codeScanner.startPreview()
                            }
                            status.equals(false) -> {
                                toast("No record found")
                                codeScanner.startPreview()
                            }
                            else -> {
                                viewModel.getCartonDetails(scannedValue)
                            }
                        }
                    }
                }

            binding.cameraSurfaceView.setOnClickListener {
                codeScanner.startPreview()
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
