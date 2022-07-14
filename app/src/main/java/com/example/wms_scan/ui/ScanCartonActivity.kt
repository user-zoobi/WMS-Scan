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
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private var itemCode = ""
    private var analyticalNo = ""
    private var totCarton = ""
    private lateinit var scanCartonAdapter : ScanCartonAdapter
    var Analytical_No = ""
    var material_id = ""
    var Material_name = ""
    var pilotNo = 0
    var scannedPalletCode = ""
    var cartonSNo = ""
    var isExist = 0
    var stock = ""
    var cartonCode = ""

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
        ) {
            askForCameraPermission()
        } else {
            setupControls()
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

                Status.LOADING ->
                {

                }
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
                Status.ERROR ->
                {

                }

            }
        })

    }

    private fun setupUi(){
        dialog = CustomProgressDialog(this)
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)

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

    private fun clearPreferences(context: Context){
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

        binding.showQRIV.click {
            binding.qrScanCont.visible()
            binding.viewRV.gone()
        }

        binding.treeView.click {
            binding.qrScanCont.gone()
            binding.viewRV.visible()
            binding.palletDetailCont.isEnabled = false
        }

        binding.scanCont.click {
            binding.qrScanCont.gone()
            binding.surfaceCont.visible()
            binding.showQRIV.gone()
            binding.palletCont.isEnabled = false
            binding.palletDetailCont.isEnabled = false
            binding.hierarchyTree.isEnabled = false
        }

        binding.closeIV.click {
            binding.surfaceCont.gone()
            binding.qrScanCont.visible()
            binding.palletCont.isEnabled = true
            binding.hierarchyTree.isEnabled = true
            binding.palletDetailCont.isEnabled = false
        }

        binding.palletCont.click {
            binding.surfaceCont.gone()
            binding.qrScanCont.gone()
            binding.viewRV.visible()
            binding.palletDetailCont.visible()
            binding.showQRIV.visible()
            binding.palletDetailCont.isEnabled = false
        }

        binding.backBtn.click {
            onBackPressed()
        }

    }

    private fun initObserver(){

        //GET CARTON

        viewModel.getCartonDetails.observe(this@ScanCartonActivity, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{

                    it.let {
                        try
                        {
                            if (it.data?.get(0)?.status == true)
                            {
                                val intent = Intent(this, CartonDetailActivity::class.java)
                                intent.putExtra("scanAnalyticalNum",scannedValue)
                                intent.putExtra("palletCode",scannedPalletCode)
                                startActivity(intent)
                            }
                            else
                            {
                                Log.i("getCartonDetails","${Exception().message}")
                                toast("No record found")
                            }
                        }
                        catch (e:Exception)
                        {
                            Log.i("getCartonDetails","${e.message}")
                        }
                    }
                }

                Status.ERROR ->{ }

            }
        })

    }

    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        binding.cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int,
                width: Int, height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1)
                {
                    scannedValue = barcodes.valueAt(0).rawValue

                    //Don't forget to add this line printing value or finishing activity must run on main thread
                    runOnUiThread {
                        cameraSource.stop()
                        Toast.makeText(this@ScanCartonActivity, "value- $scannedValue", Toast.LENGTH_SHORT).show()

                        viewModel.getCartonDetails(scannedValue)

                    }
                }
                else { }
            }
        })
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }

    override fun onBackPressed() {
        finish()
    }

}