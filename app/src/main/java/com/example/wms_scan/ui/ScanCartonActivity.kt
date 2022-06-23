package com.example.wms_scan.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.palletNo
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.adapter.racks.RackAdapter
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.adapter.warehouse.WarehouseAdapter
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityScanCartonBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class ScanCartonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanCartonBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private var selectedBusLocNo = ""
    private var selectedPalletNo = ""
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private lateinit var wrhAdapter : WarehouseAdapter
    private lateinit var rackAdapter: RackAdapter
    private lateinit var shelfAdapter : ShelfAdapter
    private lateinit var palletAdapter : PalletsAdapter

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

    }

    private fun setupUi(){
        dialog = CustomProgressDialog(this)
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)

//        binding.busLocTV.text = intent.extras?.getString("whName")
//        binding.whTV.text = intent.extras?.getString("rackName")
//        binding.rackTV.text = intent.extras?.getString("shelfName")
//        binding.shelfTV.text = intent.extras?.getString("palletName")
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        finish()
    }

    private fun initListeners()
    {

        binding.scanBtn.click {

            binding.scanBtn.gone()
            binding.scanCartonTV.gone()
            binding.surfaceCont.visible()

        }
        binding.closeIV.click {
            binding.surfaceCont.gone()
            binding.scanBtn.visible()
            binding.scanCartonTV.visible()
            cameraSource.stop()
        }

        binding.addBtn.click {
            binding.view1.visible()
            binding.view2.visible()
            binding.whCont.visible()
            binding.view3.visible()
            binding.view4.visible()
            binding.rackCont.visible()
            binding.view5.visible()
            binding.view6.visible()
            binding.view7.visible()
            binding.view8.visible()
            binding.shelfCont.visible()
            binding.palletCont.visible()
            binding.minusBtn.visible()
            binding.qrScanCont.gone()
            binding.viewRV.visible()
            viewModel.getPallet(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody("11"),
                Utils.getSimpleTextBody("1"),
            )
            LocalPreferences.put(this,"isHierarchy",true)
        }

        binding.minusBtn.click {

            binding.view1.gone()
            binding.view2.gone()
            binding.whCont.gone()
            binding.view3.gone()
            binding.view4.gone()
            binding.rackCont.gone()
            binding.view5.gone()
            binding.view6.gone()
            binding.view7.gone()
            binding.view8.gone()
            binding.shelfCont.gone()
            binding.minusBtn.gone()
            binding.palletCont.gone()
            binding.addBtn.visible()
            binding.qrScanCont.visible()
            binding.viewRV.gone()

        }
        binding.WHTV2.click {
            viewModel.getWarehouse(
                "",
                "2"
            )
            binding.viewRV.visible()
            LocalPreferences.put(this,"isHierarchy",true)

        }

        binding.rackTV.click {
            viewModel.getRack(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody("8"),
                Utils.getSimpleTextBody("2"),
            )
            binding.viewRV.visible()
            LocalPreferences.put(this,"isHierarchy",true)
        }

        binding.shelfTV.click {
            viewModel.getShelf(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody("4"),
                Utils.getSimpleTextBody("1"),
            )
            binding.viewRV.visible()
            LocalPreferences.put(this,"isHierarchy",true)
        }

        binding.palletCont.click {
            viewModel.getPallet(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody("11"),
                Utils.getSimpleTextBody("1"),
            )
            LocalPreferences.put(this,"isHierarchy",true)
        }

    }

    private fun initObserver(){

        /**
         *      USER LOCATION OBSERVER
         */

        viewModel.userLocation(
            Utils.getSimpleTextBody(
                LocalPreferences.getInt(this,
                    LocalPreferences.AppLoginPreferences.userNo
                ).toString()
            ))
        viewModel.userLoc.observe(this, Observer {
            when(it.status){
                Status.LOADING->{
                    dialog.show()
                }
                Status.SUCCESS ->{
                    it.let {
                        if(it.data?.get(0)?.status == true) {
                            dialog.dismiss()

                        }
                        else
                        {
                            toast("no result found")
                        }
                    }
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }
        })

        /**
         *      GET WAREHOUSE OBSERVER
         */

        viewModel.getWarehouse.observe(this, Observer{
            when(it.status){
                Status.LOADING->{
                }
                Status.SUCCESS ->{

                    try {
                        if(it.data?.get(0)?.status == true)
                        {
                            it.data[0].wHName?.let { it1 -> Log.i("warehouseResponse", it1) }
                            wrhAdapter = WarehouseAdapter(
                                this,
                                it.data as ArrayList<GetWarehouseResponse>
                            )
                            binding.viewRV.apply {
                                layoutManager = LinearLayoutManager(this@ScanCartonActivity)
                                adapter = wrhAdapter
                            }
                        }
                        else
                        {
                            toast("no result found")
                        }
                    }
                    catch(e:Exception){
                        Log.i("rackAdapter","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }
                    //warehouseAdapter.addItems(list)
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }
        })

        /**
         *      GET RACK OBSERVER
         */

        viewModel.getRack.observe(this, Observer{
            when(it.status){
                Status.LOADING ->{
                }
                Status.SUCCESS ->{
                    // Log.i("getRack",it.data?.get(0)?.rackNo.toString())
                    try
                    {
                        if(it.data?.get(0)?.status == true)
                        {
                            rackAdapter = RackAdapter(
                                this,
                                it.data as ArrayList<GetRackResponse>
                            )
                            binding.viewRV.apply {
                                layoutManager = LinearLayoutManager(this@ScanCartonActivity)
                                adapter = rackAdapter
                            }
                        }
                        else
                        {
                            toast("no result found")
                        }
                    }
                    catch (e: Exception)
                    {
                        Log.i("RACK_OBSERVER","${e.message}")
                        Log.i("RACK_OBSERVER","${e.stackTrace}")
                    }
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }
        })


        /**
         *      GET SHELF OBSERVER
         */

        viewModel.getShelf.observe(this, Observer{
            when(it.status){
                Status.LOADING ->{
                }
                Status.SUCCESS ->{
                    try {
                        if(it.data?.get(0)?.status == true)
                        {
                            shelfAdapter = ShelfAdapter(
                                this,
                                it.data as ArrayList<GetShelfResponse>
                            )
                            binding.viewRV.apply {
                                layoutManager = LinearLayoutManager(this@ScanCartonActivity)
                                adapter = shelfAdapter
                            }
                        }
                        else
                        {
                            toast("no result found")
                        }
                    }catch (e:Exception){
                        Log.i("","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }
                }
                Status.ERROR ->{

                }
            }
        })

        /**
         *      GET PALLET OBSERVER
         */

        viewModel.getPallet.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{
                    Log.i(Constants.LogMessages.loading,"Success")
                }
                Status.SUCCESS ->{
                    try
                    {
                        if(it.data?.get(0)?.status == true)
                        {
                            palletAdapter = PalletsAdapter(
                                this,
                                it.data as ArrayList<GetPalletResponse>
                            )
                            binding.viewRV.apply {
                                layoutManager = LinearLayoutManager(this@ScanCartonActivity)
                                adapter = palletAdapter
                            }
                        }
                        else
                        {
                            toast("no result found")
                        }
                    }
                    catch (e:Exception)
                    {
                        Log.i("","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }

                }
                Status.ERROR ->{
                    Log.i(Constants.LogMessages.error,"Success")
                }
            }
        })

        //GET CARTON
        viewModel.getCarton(
            Utils.getSimpleTextBody(selectedPalletNo),
            Utils.getSimpleTextBody(selectedBusLocNo)
        )
        viewModel.getCarton.observe(this, Observer {
            when(it.status){

                Status.LOADING ->{
                    Log.i(loading,"Loading")
                }
                Status.SUCCESS ->{
                    try
                    {

                    }
                    catch (e:Exception){
                        Log.i("","${e.message}")
                        Log.i("cartonException","${e.stackTrace}")
                    }

                }
                Status.ERROR ->{
                    Log.i(error,"Error")
                }

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
                        when{
                            intent.extras?.getBoolean("scanCarton") == true -> {
                                gotoActivity(CartonDetailActivity::class.java)
                                cameraSource.stop()
                            }
                        }
                    }
                }
                else
                {

                }
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
    ) {
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