package com.example.wms_scan.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scanmate.data.callback.Status
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.data.response.GetShelfResponse
import com.example.scanmate.data.response.GetWarehouseResponse
import com.example.scanmate.data.response.UserLocationResponse
import com.example.scanmate.extensions.*
import com.example.scanmate.util.Constants.Toast.NoInternetFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.isRefreshRequired
import com.example.scanmate.util.Utils
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.databinding.ActivityShelfBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ShelfActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShelfBinding
    private lateinit var shelfAdapter: ShelfAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private val textList = mutableListOf<String>()
    private lateinit var shelfList: ArrayList<GetShelfResponse>
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var selectedRackNo = ""
    private var busLocName = ""
    private var warehouseName = ""
    private var rackName = ""
    private var selectedShelveNo = ""
    private lateinit var bmp: Bitmap
    private val bmpList = mutableListOf<Bitmap>()
    private var STORAGE_CODE = 1001
    private var shelfNo = ""
    private var shelfName = ""
    private var shelfCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShelfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListener()
        initObserver()
    }

    private fun setupUi(){
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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

    }

    private fun initListener(){

        binding.toolbar.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            clearPreferences(this)
            true
        }

        binding.swipeRefresh.setOnRefreshListener {
            if (isNetworkConnected(this))
            {
                viewModel.userLocation(
                    Utils.getSimpleTextBody(
                        LocalPreferences.getInt(this, LocalPreferences.AppLoginPreferences.userNo).toString()
                    )
                )
                viewModel.getWarehouse("", selectedBusLocNo)

                viewModel.getRack(
                    Utils.getSimpleTextBody(""),
                    Utils.getSimpleTextBody(selectedWareHouseNo),
                    Utils.getSimpleTextBody(selectedBusLocNo)
                )
            }
            else
            {
                binding.swipeRefresh.isRefreshing = false
            }
        }

        binding.shelfAddBTN.click {
            if (isNetworkConnected(this))
            {
                val intent = Intent(this, AddUpdateShelfDetails::class.java)
                intent.putExtra("addBusLocNo",selectedBusLocNo)
                intent.putExtra("addWHNo",selectedWareHouseNo)
                intent.putExtra("addRackNo",selectedRackNo)
                intent.putExtra("addShelfNo",selectedShelveNo)
                intent.putExtra("addBusLocName",busLocName)
                intent.putExtra("addWHName",warehouseName)
                intent.putExtra("addRackName",rackName)
                intent.putExtra("AddShelfKey",true)
                startActivity(intent)
            }
            else
            {
                toast(NoInternetFound)
            }
        }

        binding.printIV.click {
            generatePDF()
        }

    }

    fun showAction(shelfName:String,shelfNo:String){
        if (Utils.isNetworkConnected(this))
        {
            val intent = Intent(this, AddUpdateShelfDetails::class.java)

            intent.putExtra("updateBusLocNo",selectedBusLocNo)
            intent.putExtra("updateWHNo",selectedWareHouseNo)
            intent.putExtra("updateRackNo",selectedRackNo)
            intent.putExtra("updateShelfNo",shelfNo)
            intent.putExtra("updateBusinessLocName",busLocName)
            intent.putExtra("updateWHName",warehouseName)
            intent.putExtra("updateRackName",rackName)
            intent.putExtra("updateShelfName",shelfName)
            intent.putExtra("UpdateShelfKey",true)
            startActivity(intent)
        }
        else
        {
            toast(NoInternetFound)
        }
    }

    fun showQrCode(shelfCode:String, shelfName: String, shelfNo: String){
        val intent = Intent(this, QrCodeDetailActivity::class.java)
        intent.putExtra("shelfKey",true)
        intent.putExtra("shelfQrCode",shelfCode)
        intent.putExtra("shelfQrNo",shelfNo)
        intent.putExtra("shelfQrName",shelfName)
        startActivity(intent)
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
                    if (isNetworkConnected(this)){
                        try
                        {
                            if(it.data?.get(0)?.status == true)
                            {
                                dialog.dismiss()
                                showBusLocSpinner(it.data)
                            }
                            else
                            {
                                binding.shelfRV.adapter = null
                            }
                        }
                        catch (e:Exception)
                        {

                        }
                    }
                    else
                    {
                        binding.shelfRV.adapter = null
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
                    if (isNetworkConnected(this)){

                        try {
                            if(it.data?.get(0)?.status == true)
                            {
                                it.data[0].wHName?.let { it1 -> Log.i("warehouseResponse", it1) }
                                showWarehouseSpinner(it.data)
                            }
                            else
                            {
                                binding.shelfRV.adapter = null
                            }
                        }

                        catch(e:Exception){
                            Log.i("rackAdapter","${e.message}")
                            Log.i("rackAdapter","${e.stackTrace}")
                        }
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
                    if (isNetworkConnected(this)){
                        try
                        {
                            if(it.data?.get(0)?.status == true)
                            {
                                showRackSpinner(it.data)
                            }
                            else
                            {
                                binding.shelfRV.adapter = null
                            }
                        }
                        catch (e: Exception)
                        {
                            Log.i("RACK_OBSERVER","${e.message}")
                            Log.i("RACK_OBSERVER","${e.stackTrace}")
                        }
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
                    binding.swipeRefresh.isRefreshing = true
                }
                Status.SUCCESS ->{
                    binding.swipeRefresh.isRefreshing = false
                    it.let{
                        if (isNetworkConnected(this)){
                            LocalPreferences.put(this, isRefreshRequired, true)
                            try {
                                if(it.data?.get(0)?.status == true) {

                                    shelfName = it.data[0].shelfName.toString()
                                    shelfNo = it.data[0].shelfNo.toString()
                                    shelfCode = it.data[0].shelfCode.toString()

                                    showShelfSpinner(it.data)
                                    shelfList = ArrayList()
                                    shelfList = it.data as ArrayList<GetShelfResponse>
                                    shelfAdapter = ShelfAdapter(this, shelfList)

                                    bmpList.clear()
                                    textList.clear()

                                    for (i in it.data)
                                    {
                                        generateQRCode("${i.shelfCode}-${i.shelfNo}")
                                        textList.add(i.shelfCode!!)
                                        Log.i("shelfList","${i.shelfCode}-${i.shelfNo}")
                                    }

                                    binding.shelfRV.apply {
                                        adapter = shelfAdapter
                                        layoutManager = LinearLayoutManager(this@ShelfActivity)
                                    }
                                }else{
                                    binding.shelfRV.adapter = null
                                }
                            }
                            catch (e:Exception){
                                Log.i("","${e.message}")
                                Log.i("rackAdapter","${e.stackTrace}")
                            }
                        }
                        else
                        {
                            binding.shelfRV.adapter = null
                        }
                    }
                }
                Status.ERROR ->{

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

                if (Utils.isNetworkConnected(this@ShelfActivity))
                {
                    Log.i("LocBus","business Location no ${data[position].orgBusLocNo}")
                    // binding.rackSpinnerCont.visible()
                    selectedBusLocNo = data[position].orgBusLocNo.toString()
                    viewModel.getWarehouse("", selectedBusLocNo)
                    busLocName = data[position].busLocationName.toString()
                }
                else{
                    binding.shelfRV.adapter = null
                    toast(NoInternetFound)
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
                if (Utils.isNetworkConnected(this@ShelfActivity))
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
                }
                else
                {
                    binding.shelfRV.adapter = null
                    toast(NoInternetFound)
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
                if (Utils.isNetworkConnected(this@ShelfActivity))
                {
                    selectedRackNo = data[position].rackNo.toString()
                    rackName = data[position].rackName.toString()
                    viewModel.getShelf(
                        Utils.getSimpleTextBody(""),
                        Utils.getSimpleTextBody(selectedRackNo),
                        Utils.getSimpleTextBody(selectedBusLocNo)
                    )

                    Log.i("LocBus","This is rack pos ${adapter?.getItemAtPosition(position)}")
                }
                else
                {
                    binding.shelfRV.adapter = null
                    toast(NoInternetFound)
                }

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
                    if (Utils.isNetworkConnected(this@ShelfActivity))
                    {
                        Log.i("LocBus","This is shelf pos ${adapter?.getItemAtPosition(position)}")
                        selectedShelveNo = data[position].shelfNo.toString()
//                    viewModel.getPallet("",selectedShelveNo,selectedBusLocNo)
                    }
                    else
                    {
                        binding.shelfRV.adapter = null
                        toast(NoInternetFound)
                    }

                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (LocalPreferences.getBoolean(this, isRefreshRequired)){

            viewModel.getShelf(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody(selectedRackNo),
                Utils.getSimpleTextBody(selectedBusLocNo)
            )
        }
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        gotoActivity(LoginActivity::class.java)
    }


    private fun generateQRCode(text:String) {
        val qrWriter = QRCodeWriter()
        try
        {
            val bitMatrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, 512,512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            bmpList.add(bmp)

            bmpList.add(bmp)

            for (x in 0 until width)
            {
                for(y in 0 until height)
                {
                    bmp.setPixel(x,y, if (bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                }
            }
            binding.qrImageView.setImageBitmap(bmp)
        }
        catch (e:Exception) { }
    }

    private fun generatePDF(){
        //handle button click
        //we need to handle runtime permission for devices with marshmallow and above
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            //system OS >= Marshmallow(6.0), check permission is enabled or not
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
                //permission was not granted, request it
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions, STORAGE_CODE)
            }
            else{
                //permission already granted, call savePdf() method
                savePdf()
            }
        }
        else{
            //system OS < marshmallow, call savePdf() method
            savePdf()
        }
    }

    private fun savePdf() {
        //create object of Document class
        //pdf file name
        val file = File(Environment.getExternalStorageDirectory().toString() + "/" + "QrGeneratedFile" +".pdf")
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        //pdf file path
        val mFilePath = Environment.getExternalStorageDirectory().toString() + "/" + "QrGeneratedFile" +".pdf"
        try {

            val file = File(mFilePath)
            val mDoc = Document()
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()

            val pdfTable = PdfPTable(2)

            for (i in bmpList.indices)
            {
                val stream = ByteArrayOutputStream()
                bmpList[i].compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val myImg: Image = Image.getInstance(stream.toByteArray())
                myImg.scaleAbsolute(100f,100f)
                myImg.setAbsolutePosition(100f,100f)
                myImg.alignment = Element.ALIGN_CENTER

                val headingPara = Paragraph(Chunk("Shelf"))
                headingPara.alignment = Element.ALIGN_CENTER

                val shelfcode = Paragraph(Chunk("1001"))
                shelfcode.alignment = Element.ALIGN_CENTER

//                val rackcode = Paragraph(Chunk("${textList[i]}"))
//                rackcode.alignment = Element.ALIGN_CENTER


                val pdfcell = PdfPCell()
                with(pdfcell)
                {
                    rowspan = 2
                    addElement(headingPara)
                    addElement(myImg)
                    addElement(shelfcode)
                    paddingBottom = 10f
                }

                pdfTable.addCell(pdfcell)
            }

            mDoc.add(pdfTable)

            mDoc.close()

            openPDF(file, "QrGeneratedFile.pdf\nis saved to\n$mFilePath")

            //show file saved message with file name and path
            Toast.makeText(this, "$mFileName.pdf\nis saved to\n$mFilePath", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception)
        {
            Log.i("pdfException","${e.message}")
            //if anything goes wrong causing exception, get and show exception message
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            Log.i("pdfException","${e.message}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted, call savePdf() method
                    savePdf()
                }
                else{
                    //permission from popup was denied, show error message
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openPDF(file: File, text: String)
    {
        val path = FileProvider.getUriForFile(
            this,
            this.applicationContext.packageName.toString() + ".provider",
            file
        )
        val pdfOpenIntent = Intent(Intent.ACTION_VIEW)
        pdfOpenIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pdfOpenIntent.setDataAndType(path, "application/pdf")
        try
        {
            startActivity(pdfOpenIntent)
        }
        catch (e: ActivityNotFoundException)
        {
            Log.i("openPDFException","${e.message}")
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
        }
    }

}