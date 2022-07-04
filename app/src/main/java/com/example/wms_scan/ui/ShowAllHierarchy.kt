package com.example.wms_scan.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scanmate.data.callback.Status
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.data.response.GetShelfResponse
import com.example.scanmate.data.response.GetWarehouseResponse
import com.example.scanmate.extensions.obtainViewModel
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppConstants.orgBusLocNo
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.busLocNo
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.userNo
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.adapter.pallets.ScanPalletAdapter
import com.example.wms_scan.adapter.racks.RackAdapter
import com.example.wms_scan.adapter.racks.ScanRackAdapter
import com.example.wms_scan.adapter.shelf.ScanShelfAdapter
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.adapter.warehouse.ScanWarehouseAdapter
import com.example.wms_scan.adapter.warehouse.WarehouseAdapter
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityScanAllHierarchyBinding
import java.util.ArrayList

class ShowAllHierarchy : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityScanAllHierarchyBinding
    private lateinit var warehouseAdapter: ScanWarehouseAdapter
    private lateinit var racksAdapter: ScanRackAdapter
    private lateinit var shelfAdapter: ScanShelfAdapter
    private lateinit var palletAdapter: ScanPalletAdapter
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
        scannedValue = intent.extras?.getString("rackCode").toString()
        Log.i("Scannedvalue",scannedValue)

    }

    private fun initObserver(){

        val rackCode = intent.extras?.getString("rackCode")
        val shelfCode = intent.extras?.getString("shelfCode")
        val palletCode = intent.extras?.getString("palletCode")

        if (scannedValue.contains("WH"))
        {
            viewModel.getWarehouse(
                "",
                "1"
            )
        }

        if (scannedValue.contains("RK"))
        {
            viewModel.getRack(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody("60"),
                Utils.getSimpleTextBody("1")
            )
        }

        if (scannedValue.contains("SF"))
        {
            viewModel.getShelf(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody("50"),
                Utils.getSimpleTextBody("1"),
            )
        }

        if (scannedValue.contains("PL"))
        {
            viewModel.getPallet(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody("48"),
                Utils.getSimpleTextBody("1"),
            )
        }

        viewModel.getWarehouse.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    warehouseAdapter = ScanWarehouseAdapter(this,
                        it.data as ArrayList<GetWarehouseResponse>
                    )
                    binding.showAllRV.apply {
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = warehouseAdapter
                    }
                }
                Status.ERROR ->{

                }
            }
        })

        viewModel.getRack.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    racksAdapter = ScanRackAdapter(this,
                        it.data as ArrayList<GetRackResponse>
                    )
                    binding.showAllRV.apply {
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = racksAdapter
                    }
                }
                Status.ERROR ->{

                }
            }
        })


        viewModel.getShelf.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    shelfAdapter = ScanShelfAdapter(this,
                        it.data as ArrayList<GetShelfResponse>
                    )
                    binding.showAllRV.apply {
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = shelfAdapter
                    }
                }
                Status.ERROR ->{

                }
            }
        })


        viewModel.getPallet.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    palletAdapter = ScanPalletAdapter(this,
                        it.data as ArrayList<GetPalletResponse>
                    )
                    binding.showAllRV.apply {
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = palletAdapter
                    }
                }
                Status.ERROR ->{

                }
            }
        })


    }
}