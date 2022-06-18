package com.example.wms_scan.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.obtainViewModel
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityPdflistViewBinding
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


class TestActivity : AppCompatActivity() {
    lateinit var binding: ActivityPdflistViewBinding
    private var STORAGE_CODE = 1001
    private lateinit var viewModel: MainViewModel
    private lateinit var bmp:Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdflistViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
            generatePDF()
        viewModel = obtainViewModel(MainViewModel::class.java)


        viewModel.getPallet(
            Utils.getSimpleTextBody(""),
            Utils.getSimpleTextBody("11"),
            Utils.getSimpleTextBody("1")
        )
        viewModel.getPallet.observe(this, androidx.lifecycle.Observer {
            when(it.status){
                Status.LOADING->{

                }
                Status.SUCCESS->{

                }
                Status.ERROR->{

                }
            }
        })
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
        val mFileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        //pdf file path
        val mFilePath = Environment.getExternalStorageDirectory().toString() + "/" + mFileName +".pdf"

        try {
            val mDoc = Document()
            //create instance of PdfWriter class
            PdfWriter.getInstance(mDoc, FileOutputStream(mFilePath))

            //open the document for writing
            mDoc.open()
            mDoc.newPage()

            //add paragraph to the document
            for (i in 0..9){

                val imgView = ImageView(this)
                Glide.with(this).load(R.drawable.qr_code).into(imgView)
                binding.lLCont.addView(imgView)


                // Creating imagedata from image on disk(from given
                // path) using ImageData object

                // Creating imagedata from image on disk(from given
                // path) using ImageData object
                val ims: InputStream = assets.open("bosch.png")
                val bmp = BitmapFactory.decodeStream(ims)
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val image: Image = Image.getInstance(stream.toByteArray())
                mDoc.add(image)
                mDoc.add(Paragraph("This is an image"))
            }
            //close document
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