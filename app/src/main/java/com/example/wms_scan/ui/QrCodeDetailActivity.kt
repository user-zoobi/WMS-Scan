package com.example.wms_scan.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.util.CustomProgressDialog
import com.example.wms_scan.databinding.BottomSheetDialogViewBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter


class QrCodeDetailActivity : AppCompatActivity() {
    lateinit var binding: BottomSheetDialogViewBinding
    private var qRBit: Bitmap? = null
    private var selectedPalletNo:String? = ""

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
        binding = BottomSheetDialogViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setTransparentStatusBarColor(com.example.wms_scan.R.color.transparent)

        whCode = intent.extras?.getString("whQrCode")
        whNo = intent.extras?.getString("whNo")
        whName = intent.extras?.getString("whName")

        rackCode = intent.extras?.getString("rackQrCode")
        rackNo = intent.extras?.getString("rackQrNo")
        rackName = intent.extras?.getString("rackQrName")

        shelfCode = intent.extras?.getString("shelfQrCode")
        shelfName = intent.extras?.getString("shelfQrNo")
        shelfNo = intent.extras?.getString("shelfQrName")

        palletCode = intent.extras?.getString("palletQrCode")
        palletName = intent.extras?.getString("palletQrNo")
        palletNo = intent.extras?.getString("palletQrName")

        when{
            intent.extras?.getBoolean("warehouseKey") == true->{
                generateQRCode("$whName")
                binding.qrCodeNameTV.text = "warehouse name is $whName"
                binding.qrCodeTV.text = "warehouse code is $whCode"
                binding.qrCodeNoTV.text = "warehouse number is $whNo"
            }
            intent.extras?.getBoolean("rackKey") == true->{
                generateQRCode("$rackName")
                binding.qrCodeNameTV.text = "rack code is $rackName"
                binding.qrCodeTV.text = "rack code is $rackCode"
                binding.qrCodeNoTV.text = "rack code is $rackNo"
            }
            intent.extras?.getBoolean("shelfKey") == true->{
                generateQRCode("$shelfName")
                binding.qrCodeNameTV.text = "shelf number is $shelfName"
                binding.qrCodeTV.text = "shelf number is $shelfCode"
                binding.qrCodeNoTV.text = "shelf number is $shelfNo"
            }
            intent.extras?.getBoolean("palletKey") == true->{
                generateQRCode("$palletName")
                binding.qrCodeNameTV.text = "pallet number is $palletNo"
                binding.qrCodeTV.text = "pallet number is $palletCode"
                binding.qrCodeNoTV.text = "pallet number is $palletNo"
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
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
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


}