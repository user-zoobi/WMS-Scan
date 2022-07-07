package com.example.wms_scan.ui

import android.Manifest
import android.R.attr
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.scanmate.extensions.click
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.util.LocalPreferences
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityQrCodeDetailActivityBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfChunk
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class QrCodeDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityQrCodeDetailActivityBinding
    private lateinit var bmp:Bitmap
    private lateinit var palletList: ArrayList<GetPalletResponse>


    private val permissionstorage = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private var busLocNo = ""

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
        setupUi()
        generatePDF()
        palletList = ArrayList()

        whCode = intent.extras?.getString("whQrCode")
        whNo = intent.extras?.getString("whNo")
        whName = intent.extras?.getString("whName")
        busLocNo = intent.extras?.getString("busLoc").toString()

        rackCode = intent.extras?.getString("rackQrCode")
        rackNo = intent.extras?.getString("rackQrNo")
        rackName = intent.extras?.getString("rackQrName")

        shelfCode = intent.extras?.getString("shelfQrCode")
        shelfNo = intent.extras?.getString("shelfQrNo")
        shelfName   = intent.extras?.getString("shelfQrName")

        palletCode = intent.extras?.getString("palletQrCode")
        palletNo  = intent.extras?.getString("palletQrNo")
        palletName = intent.extras?.getString("palletQrName")

        when
        {
            intent.extras?.getBoolean("warehouseKey") == true->
            {
                generateQRCode("${busLocNo}-${whCode}")
                binding.qrCodeNameTV.text = whName
            }
            intent.extras?.getBoolean("rackKey") == true->
            {
                generateQRCode("${busLocNo}-${whCode}-$rackCode")
                binding.qrCodeNameTV.text = rackName
            }
            intent.extras?.getBoolean("shelfKey") == true->
            {
                generateQRCode("${busLocNo}-${whCode}-${rackCode}-${shelfCode}")
                binding.qrCodeNameTV.text = shelfName
            }
            intent.extras?.getBoolean("palletKey") == true->
            {
                generateQRCode("${busLocNo}-${whCode}-${rackCode}-${shelfCode}-${palletCode}")
                binding.qrCodeNameTV.text = palletNo

            }
        }
    }

    private fun setupUi(){
        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )
        binding.backBtn.click {
            onBackPressed()

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
            binding.qrCodeNameTV.text = Paragraph("$palletCode").toString()
        }
        catch (e:Exception) { }
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

            val stream = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val myImg: Image = Image.getInstance(stream.toByteArray())
            myImg.scaleAbsolute(100f,100f)
            myImg.setAbsolutePosition(100f,100f)

            val pdfTable = PdfPTable(2)
            for (i in 0 until 6){
                pdfTable.addCell(myImg)
                val font = Font()
                font.color = BaseColor.BLACK;
            }
            mDoc.add(pdfTable)

            mDoc.close()

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


}