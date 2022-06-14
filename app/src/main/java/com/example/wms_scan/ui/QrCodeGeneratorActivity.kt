package com.example.wms_scan.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.click
import com.example.scanmate.extensions.obtainViewModel
import com.example.scanmate.util.Constants
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.busLocNo
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.palletNo
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityQrCodeGeneratorBinding
import com.example.wms_scan.utils.ZatcaQRCodeGeneration
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import okio.IOException
import java.io.File
import java.io.FileOutputStream


class QrCodeGeneratorActivity : AppCompatActivity() {
    private lateinit var binding:ActivityQrCodeGeneratorBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    var itemCode = ""
    var cartonCode = ""
    var paletteName = ""
    var analyticalNo = ""
    var selectedBusLocNo:String? = ""
    var selectedPalletNo:String? = ""
    private var STORAGE_CODE = 1001
    private var bitmap: Bitmap? = null
    var targetPdf: String = "/storage/emulated/0//pdffromlayoutview.pdf"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeGeneratorBinding.inflate(layoutInflater)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setContentView(binding.root)
        dialog = CustomProgressDialog(this)
        initListener()
        initObserver()

        selectedBusLocNo = LocalPreferences.getString(this,busLocNo).toString()
        selectedPalletNo = LocalPreferences.getString(this, palletNo).toString()
        Log.i("prefBusLocNo","$selectedBusLocNo   $selectedPalletNo")

        viewModel.getCarton(
            Utils.getSimpleTextBody("2"),
            Utils.getSimpleTextBody("1")
        )

        viewModel.getCarton.observe(this, Observer {
            when(it.status){

                Status.LOADING ->{
                    Log.i(Constants.LogMessages.loading,"Loading")
                    dialog.show()
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
                    catch (e:Exception)
                    {
                        Log.i("exceptionGetCarton","${e.message}")
                        Log.i("cartonException","${e.stackTrace}")
                    }

                }
                Status.ERROR->{
                    Log.i("exceptionGetCarton","${Exception().message}")
                }

            }
        })

        var convertButton = binding.convertButton

        convertButton.setOnClickListener {
            val builder = ZatcaQRCodeGeneration.Builder()
            builder.cartonCode(cartonCode)
                .itemCode(itemCode)
                .paletteName(paletteName)
                .analyticalNo(analyticalNo)
            showBottomSheet(builder.getBase64())
        }


    }

    private fun initListener(){
        binding.printBtn.click {
//            createPdf("$selectedBusLocNo $selectedPalletNo")
        }
    }

    private fun initObserver()
    {

    }

    private fun showBottomSheet(base64: String) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
        var base64TextView = view.findViewById<TextView>(R.id.base64text)
        var qrCodeImageView = view.findViewById<ImageView>(R.id.qrcode_img)
//        var zatcaButton = view.findViewById<Button>(R.id.zatca_app_button)

        base64TextView.text = base64
        qrCodeImageView.setImageBitmap(generateQRCodeFromText(base64))

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

//        zatcaButton?.setOnClickListener {
//            openEInvoiceAApp()
//        }

        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun generateQRCodeFromText(content: String? = ""): Bitmap? {
        try {
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 300, 300)
        } catch (e: Exception) {
        }
        return null
    }

//    private fun createPdf(someText: String) {
//
//        if (ContextCompat.checkSelfPermission(this  ,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED)
//            {
//                // create a new document
//                val document = PdfDocument()
//                // crate a page description
//                var pageInfo = PageInfo.Builder(300, 600, 1).create()
//                // start a page
//                var page = document.startPage(pageInfo)
//                var canvas: Canvas = page.canvas
//                var paint = Paint()
//                paint.setColor(Color.RED)
//                canvas.drawCircle(50F, 50F, 30F, paint)
//                paint.setColor(Color.BLACK)
//                canvas.drawText(someText, 80F, 50F, paint)
//                //canvas.drawt
//                // finish the page
//                document.finishPage(page)
//                // draw text on the graphics object of the page
//                // Create Page 2
//                pageInfo = PageInfo.Builder(300, 600, 2).create()
//                page = document.startPage(pageInfo)
//                canvas = page.canvas
//                paint = Paint()
//                paint.setColor(Color.BLUE)
//                canvas.drawCircle(100F, 100F, 100F, paint)
//                document.finishPage(page)
//                // write the document content
//                val directory_path = Environment.getExternalStorageDirectory().path + "/mypdf/"
//                val file = File(directory_path)
//                if (!file.exists()) {
//                    file.mkdirs()
//                }
//                val targetPdf = directory_path + "file.pdf"
//                val filePath = File(targetPdf)
//                try {
//                    document.writeTo(FileOutputStream(filePath))
//                    Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
//                } catch (e: IOException) {
//                    Log.e("main", "error $e")
//                    Toast.makeText(this, "Something wrong: $e", Toast.LENGTH_LONG).show()
//                }
//                // close the document
//                document.close()
//            }
//    }

}