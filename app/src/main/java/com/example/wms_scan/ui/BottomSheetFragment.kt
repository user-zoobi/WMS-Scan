package com.example.wms_scan.ui

import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.boschscan.extensions.toast
import com.example.scanmate.extensions.click
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.palletNo
import com.example.wms_scan.databinding.BottomSheetDialogViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException

import com.itextpdf.text.Paragraph

import com.itextpdf.text.pdf.PdfWriter
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class BottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetDialogViewBinding
    private var qRBit: Bitmap? = null
    private var selectedPalletNo:String? = ""
    private var STORAGE_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogViewBinding.inflate(inflater, container, false)
        selectedPalletNo = LocalPreferences.getString(requireContext(),palletNo)
        Log.i("qrData",LocalPreferences.getString(requireContext(),palletNo).toString())
        generateQRCode("$selectedPalletNo")
        return binding.root
    }


    private fun generateQRCode(text: String) {
      val qrWriter = QRCodeWriter()
        try
        {
            val bitMatrix = qrWriter.encode(selectedPalletNo, BarcodeFormat.QR_CODE, 512,512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width){
                for(y in 0 until height){
                    bmp.setPixel(x,y, if (bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                }
            }
            binding.qrImageView.setImageBitmap(bmp)
        }
        catch (e:Exception)
        {

        }
    }


}