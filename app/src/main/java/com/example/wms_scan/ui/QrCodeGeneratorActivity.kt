package com.example.wms_scan.ui

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.PermissionRequest
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import okio.IOException
import java.io.File
import java.io.FileOutputStream
import java.util.*


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

//        var sellerNameEditText = binding.sellerNameEdit
//        var taxNumberEditText = binding.taxNumberEdit
//        var dateTimeEditText = binding.dateTimeEdit
//        var totalAmountWithVatEditText = binding.totalEdit
//        var vatAmountEditText = binding.vatAmountEdit
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
        }
    }

    private fun initObserver(){

    }

    private fun showBottomSheet(base64: String) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnClose = view.findViewById<Button>(R.id.idBtnDismiss)
        var base64TextView = view.findViewById<TextView>(R.id.base64text)
        var qrCodeImageView = view.findViewById<ImageView>(R.id.qrcode_img)
        var zatcaButton = view.findViewById<Button>(R.id.zatca_app_button)

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

//    private fun openEInvoiceAApp() {
//        try {
//            startActivity(
//                Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id=com.posbankbh.einvoiceqrreader")
//                )
//            )
//        } catch (e: Exception) {
//        }
//    }


//    private fun createTable(doc: Document, itemSum: Double) {
//        val f = Font(Font.FontFamily.HELVETICA, 30.0f, Font.BOLD, BaseColor.RED)
//        val g = Font(Font.FontFamily.COURIER, 16.0f, Font.NORMAL, BaseColor.BLACK)
//        val h = Font(Font.FontFamily.TIMES_ROMAN, 16.0f, Font.BOLD, BaseColor.BLACK)
//        val i = Font(Font.FontFamily.TIMES_ROMAN, 14.0f, Font.NORMAL, BaseColor.BLACK)
//        val k = Font(Font.FontFamily.TIMES_ROMAN, 10.0f, Font.NORMAL, BaseColor.BLACK)
//        try {
//            val title = Chunk("NAVINTA SUPERMARKET ", f)
//            val Description = Chunk("THE ULTIMATE SHOPPING SOLUTION", g)
//            val date = Chunk("Date: 23:-0-00", i)
//            val orderNo = Chunk(
//                """
//                  OrderNo: ${23123123}
//
//                  """.trimIndent(), k
//            )
//            val CustomerNamee: Chunk = Chunk("Karachi" + "\n\n", h)
//            val Heading = Chunk("\n\n\nOrder Reciept \n\n\n", h)
//            val TotalSum = Chunk("\n\nTotal: $23", h)
//            val CName = Chunk("Name : Zohaib", h)
//            val CCont = Chunk("Contact: 0340", h)
//            val paragraphTitle = Paragraph(title)
//            paragraphTitle.alignment = Element.ALIGN_CENTER
//            paragraphTitle.spacingAfter = 20f
//            val paragraphDescription = Paragraph(Description)
//            paragraphDescription.alignment = Element.ALIGN_CENTER
//            paragraphDescription.spacingAfter = 20f
//            val pHeading = Paragraph(Heading)
//            pHeading.alignment = Element.ALIGN_CENTER
//            val pdate = Paragraph(date)
//            pdate.alignment = Element.ALIGN_LEFT
//            val pONo = Paragraph(orderNo)
//            pONo.alignment = Element.ALIGN_LEFT
//            val pName = Paragraph(CustomerNamee)
//            pName.alignment = Element.ALIGN_LEFT
//            val pCName = Paragraph(CName)
//            pCName.alignment = Element.ALIGN_LEFT
//            val pCCont = Paragraph(CCont)
//            pCCont.alignment = Element.ALIGN_LEFT
//            val pTotal = Paragraph(TotalSum.toString())
//            pTotal.alignment = Element.ALIGN_RIGHT
//            doc.add(paragraphTitle)
//            doc.add(paragraphDescription)
//            doc.add(pHeading)
//            doc.add(pdate)
//            doc.add(pONo)
//            doc.add(pCName)
//            doc.add(pCCont)
//            doc.add(pName)
//            val table = PdfPTable(5)
//            table.widthPercentage = 100f
//            table.setWidths(floatArrayOf(1f, 6f, 3f, 3f, 3f))
//            var cell: PdfPCell
//            cell = PdfPCell(Phrase("No", h))
//            cell.horizontalAlignment = Element.ALIGN_CENTER
//            cell.backgroundColor = BaseColor.LIGHT_GRAY
//            cell.rowspan = 3
//            table.addCell(cell)
//            cell = PdfPCell(Phrase("Description", h))
//            cell.horizontalAlignment = Element.ALIGN_CENTER
//            cell.rowspan = 3
//            cell.backgroundColor = BaseColor.LIGHT_GRAY
//            table.addCell(cell)
//            cell = PdfPCell(Phrase("Quantity", h))
//            cell.horizontalAlignment = Element.ALIGN_CENTER
//            cell.rowspan = 3
//            cell.backgroundColor = BaseColor.LIGHT_GRAY
//            table.addCell(cell)
//            cell = PdfPCell(Phrase("Price", h))
//            cell.horizontalAlignment = Element.ALIGN_CENTER
//            cell.rowspan = 3
//            cell.backgroundColor = BaseColor.LIGHT_GRAY
//            table.addCell(cell)
//            cell = PdfPCell(Phrase("Amount", h))
//            cell.horizontalAlignment = Element.ALIGN_CENTER
//            cell.rowspan = 3
//            cell.backgroundColor = BaseColor.LIGHT_GRAY
//            table.addCell(cell)
////            for (j in 0 until arrayList.size()) {
////                cell = PdfPCell(Phrase((j + 1).toString()))
////                cell.horizontalAlignment = Element.ALIGN_CENTER
////                table.addCell(cell)
////                cell = PdfPCell(Phrase(arrayList.get(j).getItemName()))
////                cell.horizontalAlignment = Element.ALIGN_LEFT
////                table.addCell(cell)
////                cell =
////                    PdfPCell(Phrase(java.lang.String.valueOf(arrayList.get(j).getItemQuantity())))
////                cell.horizontalAlignment = Element.ALIGN_CENTER
////                table.addCell(cell)
////                cell = PdfPCell(
////                    Phrase(
////                        java.lang.String.valueOf(arrayList.get(j).getItemPrice())
////                            .toString() + " Pkr"
////                    )
////                )
////                cell.horizontalAlignment = Element.ALIGN_CENTER
////                table.addCell(cell)
////                cell =
////                    PdfPCell(Phrase(java.lang.String.valueOf(arrayList.get(j).getItemTotalSum())))
////                cell.horizontalAlignment = Element.ALIGN_CENTER
////                table.addCell(cell)
////            }
//
//            //document.add(table);
//            doc.add(table)
//            doc.add(pTotal)
//            //Toast.makeText(getApplicationContext(), "Generated !", Toast.LENGTH_SHORT).show();
//        } catch (e: DocumentException) {
//            e.printStackTrace()
//        }
//    }

}