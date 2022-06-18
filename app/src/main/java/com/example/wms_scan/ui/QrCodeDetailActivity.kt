package com.example.wms_scan.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.wms_scan.databinding.ActivityQrCodeDetailActivityBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class QrCodeDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityQrCodeDetailActivityBinding
    private var qRBit: Bitmap? = null
    private var selectedPalletNo:String? = ""
    private val REQUEST_EXTERNAL_STORAGe = 1
    private lateinit var bmp:Bitmap


    private val permissionstorage = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var whName:String? = ""
    private var whNo:String? = ""
    private var whCode:String? = ""

    private var rackName:String? = ""
    private var rackNo:String? = ""
    private var rackCode:String? = ""

    private var shelfName:String? = ""
    private var shelfNo:String? = ""
    private var shelfCode:String? = ""

    private var palletName:String? = ""
    private var palletNo:String? = ""
    private var palletCode:String? = ""

    private var whFlag:Boolean = false
    private var palletname:String? = ""
    private var STORAGE_CODE = 1001



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setTransparentStatusBarColor(com.example.wms_scan.R.color.transparent)
        generatePDF()

        whCode = intent.extras?.getString("whQrCode")
        whNo = intent.extras?.getString("whNo")
        whName = intent.extras?.getString("whName")

        rackCode = intent.extras?.getString("rackQrCode")
        rackNo = intent.extras?.getString("rackQrNo")
        rackName = intent.extras?.getString("rackQrName")

        shelfCode = intent.extras?.getString("shelfQrCode")
        shelfNo = intent.extras?.getString("shelfQrNo")
        shelfName   = intent.extras?.getString("shelfQrName")

        palletCode = intent.extras?.getString("palletQrCode")
        palletNo  = intent.extras?.getString("palletQrNo")
        palletName = intent.extras?.getString("palletQrName")

        when{
            intent.extras?.getBoolean("warehouseKey") == true->{
                generateQRCode("$whCode-$whName")
                binding.qrCodeNameTV.text = "warehouse : $whName"
                binding.qrCodeTV.text = "warehouse code: $whCode"
                binding.qrCodeNoTV.text = "warehouse number: $whNo"
            }
            intent.extras?.getBoolean("rackKey") == true->{
                generateQRCode("$rackCode-$rackNo")
                binding.qrCodeNameTV.text = "rack code: $rackName"
                binding.qrCodeTV.text = "rack code: $rackCode"
                binding.qrCodeNoTV.text = "rack no: $rackNo"
            }
            intent.extras?.getBoolean("shelfKey") == true->{
                generateQRCode("$shelfCode-$shelfNo")
                binding.qrCodeNameTV.text = "shelf name: is $shelfName"
                binding.qrCodeTV.text = "shelf code: is $shelfCode"
                binding.qrCodeNoTV.text = "shelf no; $shelfNo"
            }
            intent.extras?.getBoolean("palletKey") == true->{
                generateQRCode("$palletCode-$palletNo")
                binding.qrCodeNameTV.text = "pallet name: $palletNo"
                binding.qrCodeTV.text = "pallet code: $palletCode"
                binding.qrCodeNoTV.text = "pallet no: $palletNo"
            }
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

            for (x in 0 until width)
            {
                for(y in 0 until height)
                {
                    bmp.setPixel(x,y, if (bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                }
            }
            binding.qrImageView.setImageBitmap(bmp)

        }
        catch (e:Exception)
        {

        }
    }

    private fun generatePDF(){
        //handle button click
        binding.printIV.setOnClickListener {
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
    }

    private fun savePdf() {
        //create object of Document class

        //pdf file name
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        //pdf file path
        val mFilePath = Environment.getExternalStorageDirectory().toString() + "/" + "QrGeneratedFile" +".pdf"
        try {
            val mDoc = Document()
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))
            mDoc.open()
            for (i in 0..9){
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val myImg: Image = Image.getInstance(stream.toByteArray())
                myImg.alignment = Image.MIDDLE
                mDoc.add(myImg)
            }
            val qrText = mDoc.add(Paragraph("$palletCode-$palletNo"))
            binding.qrText.text = qrText.toString()
            mDoc.close()

            //show file saved message with file name and path
            Toast.makeText(this, "$mFileName.pdf\nis saved to\n$mFilePath", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception){
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


}