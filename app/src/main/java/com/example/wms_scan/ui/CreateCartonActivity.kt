package com.example.wms_scan.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.boschscan.extensions.putExtra
import com.example.scanmate.data.callback.Status
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.data.response.GetShelfResponse
import com.example.scanmate.data.response.GetWarehouseResponse
import com.example.scanmate.data.response.UserLocationResponse
import com.example.scanmate.extensions.*
import com.example.scanmate.util.Constants
import com.example.scanmate.util.Constants.LogMessages.success
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.carton.CartonAdapter
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.data.response.GetCartonResponse
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityCreateCartonBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class CreateCartonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateCartonBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private var palletCode = ""
    private var palletNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCartonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListeners()

        if (ContextCompat.checkSelfPermission(
                this , android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }
    }

    private fun setupUi(){

        dialog = CustomProgressDialog(this)
        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )
        palletCode = intent.extras?.getString("palletQrCode").toString()
        palletNo = intent.extras?.getString("palletQrNo").toString()

        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)

    }

    private fun initListeners(){

        binding.scanBtn.click {
//            gotoActivity(ScanCartonActivity::class.java)
            binding.scanBtn.gone()
            binding.scannerCont.visible()
            binding.closeIV.visible()
            binding.clickHereTV.gone()
        }
        binding.closeIV.click {
            binding.scannerCont.gone()
            binding.scanBtn.visible()
            binding.closeIV.gone()
            cameraSource.stop()
        }

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
            override fun release()
            {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue
                    //Don't forget to add this line printing value or finishing activity must run on main thread
                    runOnUiThread {
                        cameraSource.stop()
                        Toast.makeText(this@CreateCartonActivity, "value- $scannedValue", Toast.LENGTH_SHORT).show()


                        viewModel.palletHierarchy(Utils.getSimpleTextBody(scannedValue))

                        viewModel.palletHierarchy.observe(this@CreateCartonActivity, Observer{
                            when(it.status){
                                Status.LOADING->{
                                }
                                Status.SUCCESS ->{
                                    if (Utils.isNetworkConnected(this@CreateCartonActivity)){
                                        it.let {
                                            if(it.data?.get(0)?.status == true) {
//                                                gotoActivity(ScanCartonActivity::class.java, "scanCarton", true)
                                                val whName = it.data[0].wHName
                                                val rackName = it.data[0].rackName
                                                val shelfName = it.data[0].shelfName
                                                val palletName = it.data[0].pilotName
                                                val intent = Intent(this@CreateCartonActivity, ScanCartonActivity::class.java)
                                                intent.putExtra("whName",whName)
                                                intent.putExtra("rackName",rackName)
                                                intent.putExtra("shelfName",shelfName)
                                                intent.putExtra("palletName",palletName)
                                                intent.putExtra("scanCarton",true)
                                                startActivity(intent)
                                                val error = it.data[0].error
                                                val status = it.data[0].status

                                                Log.i("palletHierarchy","whName :$whName rackName $rackName shelfName $shelfName $palletName\n $error $status")
                                            }
                                            else { }
                                        }
                                    }
                                    else { }
                                }
                                Status.ERROR ->{
                                    dialog.dismiss()
                                    Log.i("error","${Exception().message}")
                                }
                            }
                        })
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                setupControls()
            }
            else
            {
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