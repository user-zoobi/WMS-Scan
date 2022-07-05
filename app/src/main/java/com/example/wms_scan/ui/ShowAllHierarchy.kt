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
import com.example.scanmate.extensions.gone
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
        Log.i("Scannedvalue",scannedValue)

    }

    private fun initObserver(){

        val location    = intent.extras?.getString("l")
        val warehouse   = intent.extras?.getString("w")
        val rack        = intent.extras?.getString("r")
        val shelve      = intent.extras?.getString("s")
        val pallete     = intent.extras?.getString("p")
        scannedValue    = "$location-$warehouse-$rack-$shelve-$pallete"


        Log.i("data","$warehouse")
        Log.i("data","$location-$warehouse-$rack-$shelve-$pallete")

        when
        {
            scannedValue.contains("P") -> viewModel.scanAll(pallete!!, "0")
            scannedValue.contains("S") -> viewModel.scanAll(shelve!!, "0")
            scannedValue.contains("R") -> viewModel.scanAll(rack!!, "0")
            scannedValue.contains("W") -> viewModel.scanAll("$location$warehouse", "0")
            scannedValue.contains("L") -> viewModel.scanAll(location!!, "0")
        }

        Log.i("scannedValue",scannedValue)

        viewModel.scanAll.observe(this, Observer {
            when(it.status){

                Status.LOADING -> { }

                Status.SUCCESS ->
                {
                    it.let {
                        try
                        {
                            val whName = it.data?.get(0)?.wHName.toString()
                            val rackName = it.data?.get(0)?.rackName.toString()
                            val shelfName = it.data?.get(0)?.shelfName.toString()
                            val palletName = it.data?.get(0)?.pilotName.toString()

                            binding.WHTV.text = whName
                            binding.rackTV.text = rackName
                            binding.shelfTV.text = shelfName
                            binding.palletTV.text = palletName

                            Log.i("allHierarchy",it.data?.get(0)?.analyticalNo.toString())

                            when
                            {
                                scannedValue.contains("P") ->{
                                    viewModel.getPallet(
                                        Utils.getSimpleTextBody(""),
                                        Utils.getSimpleTextBody("48"),
                                        Utils.getSimpleTextBody("1")
                                    )
                                }

                                scannedValue.contains("S") ->{
                                    viewModel.getShelf(
                                        Utils.getSimpleTextBody(""),
                                        Utils.getSimpleTextBody("50"),
                                        Utils.getSimpleTextBody("1")
                                    )
                                    binding.view7.gone()
                                    binding.view8.gone()
                                    binding.palletCont.gone()
                                }
                                scannedValue.contains("R") ->{
                                    viewModel.getRack(
                                        Utils.getSimpleTextBody(""),
                                        Utils.getSimpleTextBody("60"),
                                        Utils.getSimpleTextBody("1")
                                    )
                                    binding.view5.gone()
                                    binding.view6.gone()
                                    binding.shelfCont.gone()
                                    binding.view7.gone()
                                    binding.view8.gone()
                                    binding.palletCont.gone()
                                }
                                scannedValue.contains("W") -> {
                                    viewModel.getWarehouse("","1")
                                    binding.view3.gone()
                                    binding.view4.gone()
                                    binding.rackCont.gone()
                                    binding.view5.gone()
                                    binding.view6.gone()
                                    binding.shelfCont.gone()
                                    binding.view7.gone()
                                    binding.view8.gone()
                                    binding.palletCont.gone()
                                }

                                scannedValue.contains("L") ->{
                                    viewModel.userLocation(
                                        Utils.getSimpleTextBody("2")
                                    )
                                }
                                else -> { }
                            }
                        }
                        catch (e:Exception)
                        {
                            Log.i("scanAllHierarchy","${e.message}")
                        }

                    }
                }

                Status.ERROR ->
                {
                    Log.i("scanAllHierarchy","${Exception().message}")
                }
            }
        })

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
                    Log.i("warehouseCode", it.data[0].wHName.toString())
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
                    Log.i("warehouseCode", it.data[0].wHName.toString())
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