package com.example.wms_scan.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Matrix
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
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
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityScanCartonBinding
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.IOException


class ScanCartonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanCartonBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var palletList : ArrayList<GetPalletResponse>
    private lateinit var palletAdapter : PalletsAdapter
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var selectedRackNo = ""
    private var selectedShelveNo = ""
    private var selectedPalletNo = ""
    private var selectedPalletName = ""
    private var busLocName = ""
    private var warehouseName = ""
    private var rackName = ""
    private var shelfName = ""
    var itemCode = ""
    var cartonCode = ""
    var paletteName = ""
    var analyticalNo = ""
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private lateinit var bottomSheet:BottomSheetFragment

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

        selectedBusLocNo = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.busLocNo
        ).toString()
        selectedPalletNo = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.palletNo
        ).toString()
        Log.i("prefBusLocNo","$selectedBusLocNo   $selectedPalletNo")

    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        finish()
    }

    private fun initListeners(){

        binding.scanBtn.click {

            binding.scanBtn.gone()
            binding.scanCartonTV.gone()
            binding.surfaceCont.visible()

        }

        binding.closeIV.click {

            binding.surfaceCont.gone()
            binding.scanBtn.visible()
            binding.scanCartonTV.visible()

        }

        binding.qrGenerateIV.click {
            onBtnClick(binding.root)
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
                            showBusLocSpinner(it.data!!)
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
                            showWarehouseSpinner(it.data)
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
                            showRackSpinner(it.data!!)
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
                            showShelfSpinner(it.data!!)
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
                            showPalletSpinner(it.data)
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
                        cartonCode = it.data?.get(0)?.cartonCode.toString()
                        itemCode = it.data?.get(0)?.itemCode.toString()
                        paletteName = it.data?.get(0)?.pilotNo.toString()
                        analyticalNo = it.data?.get(0)?.analyticalNo.toString()
                        selectedBusLocNo = intent.extras?.getString("addBusLocNo").toString()
                        Log.i("GetCartonSuccess","$cartonCode $itemCode $paletteName")
                        dialog.dismiss()
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

    private fun showBusLocSpinner(data:List<UserLocationResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val businessLocSpinner = binding.businessLocationSpinner

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].busLocationName
        }
        val adapter: ArrayAdapter<String?> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        //setting adapter to spinner
        businessLocSpinner.adapter = adapter

        businessLocSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                Log.i("LocBus","business Location no ${data[position].orgBusLocNo}")
                // binding.rackSpinnerCont.visible()
                if (Utils.isNetworkConnected(this@ScanCartonActivity))
                {
                    selectedBusLocNo = data[position].orgBusLocNo.toString()
                    LocalPreferences.put(
                        this@ScanCartonActivity,
                        LocalPreferences.AppLoginPreferences.busLocNo, selectedBusLocNo
                    )
                    busLocName = data[position].busLocationName.toString()
                    viewModel.getWarehouse("", selectedBusLocNo)
                }else
                {
                    businessLocSpinner.adapter = null
                }

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun showWarehouseSpinner(data:List<GetWarehouseResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val warehouseSpinner = binding.warehouseSpinner

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].wHName
        }
        val adapter: ArrayAdapter<String?> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        //setting adapter to spinner
        warehouseSpinner.adapter = adapter
        warehouseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                if (Utils.isNetworkConnected(this@ScanCartonActivity))
                {
                    selectedWareHouseNo = data[position].wHNo.toString()
                    warehouseName = data[position].wHName.toString()
                    viewModel.getRack(
                        Utils.getSimpleTextBody(""),
                        Utils.getSimpleTextBody(selectedWareHouseNo),
                        Utils.getSimpleTextBody(selectedBusLocNo)
                    )
                    Log.i("LocBus","This is warehouse name is ${adapter?.getItemAtPosition(position)}")
                    Log.i("LocBus","This is warehouse pos is ${data[position].wHNo}")
                }else
                {
                    warehouseSpinner.adapter = null
                }


            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun showRackSpinner(data:List<GetRackResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val rackSpinner = binding.rackSpinner

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].rackName
        }
        val adapter: ArrayAdapter<String?> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        //setting adapter to spinner
        rackSpinner.adapter = adapter

        rackSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                if (Utils.isNetworkConnected(this@ScanCartonActivity))
                {
                    selectedRackNo = data[position].rackNo.toString()
                    rackName = data[position].rackName.toString()
                    viewModel.getShelf(
                        Utils.getSimpleTextBody(""),
                        Utils.getSimpleTextBody(selectedRackNo),
                        Utils.getSimpleTextBody(selectedBusLocNo)
                    )
                }
                else
                {
                    rackSpinner.adapter = null
                }


                Log.i("LocBus","This is rack pos ${adapter?.getItemAtPosition(position)}")
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun showShelfSpinner(data:List<GetShelfResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val shelfResponse = binding.shelfSpinner

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].shelfName
            val adapter: ArrayAdapter<String?> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
            //setting adapter to spinner
            shelfResponse.adapter = adapter
            shelfResponse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

                override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                    if (Utils.isNetworkConnected(this@ScanCartonActivity))
                    {
                        Log.i("LocBus","This is shelf pos ${adapter?.getItemAtPosition(position)}")
                        selectedShelveNo = data[position].shelfNo.toString()
                        shelfName = data[position].shelfName.toString()
                        viewModel.getPallet(
                            Utils.getSimpleTextBody(""),
                            Utils.getSimpleTextBody(selectedShelveNo),
                            Utils.getSimpleTextBody(selectedBusLocNo)
                        )
                    }
                    else
                    {
                        shelfResponse.adapter = null
                    }

                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun showPalletSpinner(data:List<GetPalletResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val shelfResponse = binding.palletSpinner

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].pilotName
            val adapter: ArrayAdapter<String?> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
            //setting adapter to spinner
            shelfResponse.adapter = adapter
            shelfResponse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

                override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                    if (Utils.isNetworkConnected(this@ScanCartonActivity))
                    {
                        Log.i("LocBus","This is shelf pos ${adapter?.getItemAtPosition(position)}")
                        selectedPalletNo = data[position].pilotNo.toString()
                        selectedPalletName = data[position].pilotName.toString()
                        LocalPreferences.put(
                            this@ScanCartonActivity,
                            LocalPreferences.AppLoginPreferences.palletNo, selectedPalletNo
                        )
                        LocalPreferences.put(this@ScanCartonActivity,"qrData",selectedPalletNo)
                    }
                    else
                    {
                        toast(NoInternetFound)
                    }

                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
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
            override fun release() {
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
                        Toast.makeText(this@ScanCartonActivity, "value- $scannedValue", Toast.LENGTH_SHORT).show()
                        when{
                            intent.extras?.getBoolean("scanCarton") == true -> {
                                gotoActivity(QrCodeGeneratorActivity::class.java)
                            }
                        }
                    }
                }else
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

    fun onBtnClick(view: View?) {
        bottomSheet = BottomSheetFragment()
        bottomSheet.show(supportFragmentManager,"")
    }

    override fun onBackPressed() {
        finish()
    }

}