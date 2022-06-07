package com.example.wms_scan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityAddUpdateCartonBinding
import com.example.wms_scan.databinding.ActivityMenuBinding

class AddUpdateCarton : AppCompatActivity() {
    private lateinit var binding: ActivityAddUpdateCartonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateCartonBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initListener(){

    }

}