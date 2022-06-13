package com.example.wms_scan.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wms_scan.databinding.ActivityPdfViewBinding


class PdfViewActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPdfViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}