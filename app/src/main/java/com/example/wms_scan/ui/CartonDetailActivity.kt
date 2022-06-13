package com.example.wms_scan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.scanmate.util.LocalPreferences
import com.example.wms_scan.R
import com.example.wms_scan.databinding.CartonDetailViewBinding

class CartonDetailActivity : AppCompatActivity() {

    private lateinit var binding:CartonDetailViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CartonDetailViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUi()

    }

    private fun setupUi(){

        val cartonCode = LocalPreferences.getString(this,"CartonCode")
        val itemCode = LocalPreferences.getString(this,"ItemCode")
        val pilotName = LocalPreferences.getString(this,"PilotName")
        val analyticalNo = LocalPreferences.getString(this,"AnalyticalNo")
        val totCarton = LocalPreferences.getBoolean(this,"TotCarton").toString()

        binding.cartonNameTV.text = itemCode
        binding.materialNo.text = pilotName
        binding.analyticalNumTV.text = analyticalNo
        binding.sizeTV.text = totCarton

    }
}