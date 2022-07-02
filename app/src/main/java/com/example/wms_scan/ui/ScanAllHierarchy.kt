package com.example.wms_scan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.obtainViewModel
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityScanAllHierarchyBinding
import com.example.wms_scan.databinding.ActivityScannerCameraBinding

class ScanAllHierarchy : AppCompatActivity() {
    private lateinit var dialog: CustomProgressDialog
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityScanAllHierarchyBinding
    private var scannedValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanAllHierarchyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUi()
        initObserver()
    }

    private fun setupUi(){

        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        viewModel = obtainViewModel(MainViewModel::class.java)
        scannedValue = intent.extras?.getString("subStringBefore").toString()
        Log.i("Scannedvalue",scannedValue.toString())
        val hierarchyData = arrayOf("")

    }

    private fun initObserver(){
        viewModel.scanAll("$scannedValue", "0")
        viewModel.scanAll.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    it.let {

                        val scanValue = arrayOf("")
                        for (i in 0 until it.data?.size!!){

                        }

                        val warehouse = it.data[0].wHName.toString()
                        val rack = it.data[0].rackName.toString()
                        val shelf = it.data[0].shelfName.toString()
                        val pallet = it.data[0].pilotName.toString()

                        binding.WHTV.text = warehouse
                        binding.rackTV.text = rack
                        binding.shelfTV.text = shelf
                        binding.palletTV.text = pallet

                    }
                }
                Status.ERROR ->{

                }
            }
        })


    }
}