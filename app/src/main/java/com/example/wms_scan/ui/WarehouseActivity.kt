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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scanmate.data.callback.Status
import com.example.scanmate.data.response.GetWarehouseResponse
import com.example.scanmate.data.response.UserLocationResponse
import com.example.scanmate.extensions.*
import com.example.scanmate.util.Constants.Toast.NoInternetFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.isRefreshRequired
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.userNo
import com.example.scanmate.util.Utils
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.adapter.warehouse.WarehouseAdapter
import com.example.wms_scan.databinding.ActivityWarehouseBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class WarehouseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWarehouseBinding
    private lateinit var warehouseAdapter: WarehouseAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private var selectedBusLocNo = ""
    private var businessLocName = ""
    private lateinit var bottomSheet: QrCodeDetailActivity
    private lateinit var bmp:Bitmap
    private val bmpList = mutableListOf<Bitmap>()
    private val textList = mutableListOf<String>()
    private var STORAGE_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWarehouseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initObserver()
        initListeners()
    }

    private fun setupUi(){

        dialog = CustomProgressDialog(this)
        supportActionBar?.hide()
        setTransparentStatusBarColor(com.example.wms_scan.R.color.transparent)


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

    private fun initObserver() {

        /**
         *      USER LOCATION OBSERVER
         */

        viewModel.userLocation(
            Utils.getSimpleTextBody(
                LocalPreferences.getInt(this, userNo).toString()
            ))
        viewModel.userLoc.observe(this, Observer {
            when(it.status){
                Status.LOADING->{
                    binding.swipeRefresh.isRefreshing = true
                    dialog.setCanceledOnTouchOutside(true);
                }
                Status.SUCCESS ->{
                    binding.swipeRefresh.isRefreshing = false
                    if(it.data?.get(0)?.status == true)
                    {
                    dialog.dismiss()
                    showBusLocSpinner(it.data)
                    }
                    else
                    {
                        toast("No result found")
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
                    binding.swipeRefresh.isRefreshing = isNetworkConnected(this)
                }
                Status.SUCCESS ->{
                    binding.swipeRefresh.isRefreshing = false
                    try {
                        LocalPreferences.put(this, isRefreshRequired, true)
                        if (isNetworkConnected(this))
                        {
                            Log.i("Data",it.data?.get(0).toString())
                            if(it.data?.get(0)?.status == true)
                            {
                                it.data[0].wHName?.let { it1 -> Log.i("warehouseResponse", it1) }

                                // DATA FOR QR CODE /////////////////

                                bmpList.clear()
                                textList.clear()

                                binding.printIV.click { btn ->
                                    try
                                    {
                                        for (i in it.data)
                                        {
                                            generateQRCode("${i.wHCode}-${i.wHNo}")
                                            textList.add(i.wHCode!!)
                                        }

                                        generatePDF()
                                    }
                                    catch (e:Exception)
                                    {
                                        Log.i("exception","${e.message}")
                                    }
                                }



                                warehouseAdapter = WarehouseAdapter(this,
                                    it.data as ArrayList<GetWarehouseResponse>
                                )
                                binding.warehouseRV.apply {
                                    layoutManager = LinearLayoutManager(this@WarehouseActivity)
                                    adapter = warehouseAdapter
                                }
                            }
                            else{
                                binding.warehouseRV.adapter = null
                            }
                        }
                        else
                        {
                            binding.warehouseRV.adapter = null
                            toast("No result found")
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
    }

    private fun initListeners() {

        binding.toolbar.menu.findItem(com.example.wms_scan.R.id.logout).setOnMenuItemClickListener {
            clearPreferences(this)
            true
        }

        binding.swipeRefresh.setOnRefreshListener {
            if (isNetworkConnected(this@WarehouseActivity))
            {
                viewModel.userLocation(
                    Utils.getSimpleTextBody(
                        LocalPreferences.getInt(this, userNo).toString()
                    )
                )
            }
            else{
                binding.swipeRefresh.isRefreshing = false
            }
        }

        binding.whAddBTN.click{
            if (isNetworkConnected(this)){
                val intent = Intent(this, WarehouseDetailsActivity::class.java)

                intent.putExtra("addBusName",businessLocName)
                intent.putExtra("addBusLocNo",selectedBusLocNo)
                intent.putExtra("AddWHKey",true)
                startActivity(intent)
            }
            else
            {
                toast(NoInternetFound)
            }
        }



    }

    fun performAction(whName: String?, whNo: String) {
        val intent = Intent(this, WarehouseDetailsActivity::class.java)
        intent.putExtra("updateBusName",businessLocName)
        intent.putExtra("updateBusLocNo",selectedBusLocNo)
        intent.putExtra("updateWhName",whName)
        intent.putExtra("updateWhNo",whNo)
        intent.putExtra("UpdateWHKey",true)
        startActivity(intent)
    }

    fun showQrCode(warehouseCode:String, whName:String, whNo:String) {
       val intent = Intent(this, QrCodeDetailActivity::class.java)
        intent.putExtra("warehouseKey",true)
        intent.putExtra("whQrCode",warehouseCode)
        intent.putExtra("whNo",whNo)
        intent.putExtra("whName",whName)
        startActivity(intent)
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
                if (Utils.isNetworkConnected(this@WarehouseActivity))
                {
                    Log.i("LocBus","business Location no ${data[position].orgBusLocNo}")
                    // binding.rackSpinnerCont.visible()
                    selectedBusLocNo = data[position].orgBusLocNo.toString()
                    viewModel.getWarehouse("", selectedBusLocNo)
                    businessLocName = data[position].busLocationName.toString()
                }
                else{
                    binding.warehouseRV.adapter = null
                    toast(NoInternetFound)
                }

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun clearPreferences(context: Context) {
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        gotoActivity(LoginActivity::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (LocalPreferences.getBoolean(this, isRefreshRequired ))
        {
            viewModel.getWarehouse("", selectedBusLocNo)
        }
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
                for (x in 0 until width)
                {
                    for(y in 0 until height)
                    {
                        bmp.setPixel(x,y, if (bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                    }
                }

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
        //val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
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

                val headingPara = Paragraph(Chunk("Ware House"))
                headingPara.alignment = Element.ALIGN_CENTER

                val paragraph = Paragraph(Chunk("Code: ${textList[i]}"))
                paragraph.alignment = Element.ALIGN_CENTER

                val pdfcell = PdfPCell()
                with(pdfcell)
                {
                    rowspan = 2
                    addElement(headingPara)
                    addElement(myImg)
                    addElement(paragraph)
                    paddingBottom = 10f
                }
                //pdfcell.horizontalAlignment = Element.ALIGN_CENTER;
                //pdfcell.verticalAlignment = Element.ALIGN_CENTER;
                //pdfcell.isNoWrap = false
                pdfTable.addCell(pdfcell)
            }

            mDoc.add(pdfTable)
            mDoc.close()

            //show file saved message with file name and path
            openPDF(file, "QrGeneratedFile.pdf\nis saved to\n$mFilePath")
        }
        catch (e: Exception)
        {
            Log.i("pdfException","${e.message}")
            //if anything goes wrong causing exception, get and show exception message
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