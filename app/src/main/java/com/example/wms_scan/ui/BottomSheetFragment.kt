package com.example.wms_scan.ui

import android.content.ContentValues
import android.content.Intent.getIntent
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.print.PrintHelper
import com.example.scanmate.util.LocalPreferences
import com.example.wms_scan.R
import com.example.wms_scan.databinding.BottomSheetDialogViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel


class BottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetDialogViewBinding
    private var qRBit: Bitmap? = null
    private var selectedPalletNo:String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogViewBinding.inflate(inflater, container, false)
        selectedPalletNo = LocalPreferences.getString(requireContext(),"qrData")
        Log.i("Qrdata",LocalPreferences.getString(requireContext(),"qrData").toString())
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