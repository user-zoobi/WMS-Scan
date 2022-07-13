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
import com.example.scanmate.extensions.*
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.carton.ScanCartonAdapter
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.adapter.pallets.ScanPalletAdapter
import com.example.wms_scan.adapter.racks.RackAdapter
import com.example.wms_scan.adapter.racks.ScanRackAdapter
import com.example.wms_scan.adapter.shelf.ScanShelfAdapter
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.adapter.warehouse.ScanWarehouseAdapter
import com.example.wms_scan.adapter.warehouse.WarehouseAdapter
import com.example.wms_scan.data.response.GetCartonResponse
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
    private lateinit var cartonAdapter: ScanCartonAdapter
    private var location:String? = ""
    private var warehouse:String?  = ""
    private var rack:String?  = ""
    private var shelve:String?  = ""
    private var pallete:String?  = ""
//
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanAllHierarchyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUi()
        initObserver()

        location    = intent.extras?.getString("l")
        warehouse   = intent.extras?.getString("w")
        rack        = intent.extras?.getString("r")
        shelve      = intent.extras?.getString("s")
        pallete     = intent.extras?.getString("p")

        val locationNo = location?.substringBefore("L")
        Log.i("locationNo",locationNo.toString())

        viewModel.scanAll("$location", "$locationNo")
        viewModel.scanAll("$rack", "$locationNo")
        viewModel.scanAll("$warehouse", "$locationNo")
        viewModel.scanAll("$shelve", "$locationNo")
        viewModel.scanAll("$pallete", "$locationNo")

    when
        {
        pallete!!.contains("PL") -> viewModel.scanAll("$pallete", "$locationNo")
        shelve!!.contains("SF") -> viewModel.scanAll("$shelve", "$locationNo")
        rack!!.contains("RK") -> viewModel.scanAll("$rack", "$locationNo")
        warehouse!!.contains("WH") -> viewModel.scanAll("$warehouse", "$locationNo")
        location!!.contains("L") -> viewModel.scanAll("$location", "$locationNo")
        }

        Log.i("location",location.toString())
        Log.i("rack",rack.toString())
        Log.i("warehouse",warehouse.toString())
        Log.i("shelve",shelve.toString())
        Log.i("pallet",pallete.toString())

    }

    private fun setupUi(){

        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        viewModel = obtainViewModel(MainViewModel::class.java)

        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )

        binding.backBtn.click {
            onBackPressed()
        }

    }

    private fun initObserver(){

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

                            Log.i("allHierarchy",it.data?.get(0)?.rackCode.toString())

                            when
                            {
                                pallete!!.contains("PL") ->{
                                    viewModel.getCarton(
                                        Utils.getSimpleTextBody("26"),
                                        Utils.getSimpleTextBody("1")
                                    )
                                    Log.i("palLoc","${it.data?.get(0)?.pilotCode}")
                                }

                                shelve!!.contains("SF") ->{

                                    viewModel.getPallet(
                                        Utils.getSimpleTextBody(""),
                                        Utils.getSimpleTextBody("56"),
                                        Utils.getSimpleTextBody("1"),
                                    )
                                    binding.view7.gone()
                                    binding.view8.gone()
                                    binding.palletCont.gone()
                                }

                                rack!!.contains("RK") ->{

                                    viewModel.getShelf(
                                        Utils.getSimpleTextBody(""),
                                        Utils.getSimpleTextBody("57"),
                                        Utils.getSimpleTextBody("1"),
                                    )

                                    binding.view5.gone()
                                    binding.view6.gone()
                                    binding.shelfCont.gone()
                                    binding.view7.gone()
                                    binding.view8.gone()
                                    binding.palletCont.gone()

                                }

                                warehouse!!.contains("WH") -> {

                                    viewModel.getRack(
                                        Utils.getSimpleTextBody(""),
                                        Utils.getSimpleTextBody("75"),
                                        Utils.getSimpleTextBody("1")
                                    )

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

                                location!!.contains("L") ->{
                                    viewModel.getWarehouse("","1")
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
                    binding.itemTV.text = it.data[0].wHName
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
                    binding.itemTV.text = it.data[0].wHName
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
                    binding.itemTV.text = it.data[0].rackName
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
                    binding.itemTV.text = it.data[0].shelfName
                }
                Status.ERROR ->{

                }
            }
        })

        viewModel.getCarton.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    cartonAdapter = ScanCartonAdapter(this,
                        it.data as ArrayList<GetCartonResponse>
                    )
                    binding.showAllRV.apply {
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = cartonAdapter
                    }
                    binding.itemTV.text = it.data[0].itemCode
                }
                Status.ERROR ->{

                }
            }
        })
    }

    fun warehouseAction(whNo:String){
        viewModel.getRack(
            Utils.getSimpleTextBody(""),
            Utils.getSimpleTextBody(whNo),
            Utils.getSimpleTextBody("1")
        )
    }

    fun rackAction(rackNo:String){
        viewModel.getShelf(
            Utils.getSimpleTextBody(""),
            Utils.getSimpleTextBody(rackNo),
            Utils.getSimpleTextBody("1"),
        )
        binding.view3.visible()
        binding.view4.visible()
        binding.rackCont.visible()
    }

    fun shelfAction(shelfNo:String){
        viewModel.getPallet(
            Utils.getSimpleTextBody(""),
            Utils.getSimpleTextBody(shelfNo),
            Utils.getSimpleTextBody("1"),
        )
        binding.view5.visible()
        binding.view6.visible()
        binding.shelfCont.visible()
        binding.shelfTV.visible()
    }

    fun palletAction(palletNo:String){
        viewModel.getCarton(
            Utils.getSimpleTextBody(palletNo),
            Utils.getSimpleTextBody("1")
        )
        binding.view7.visible()
        binding.view8.visible()
        binding.palletCont.visible()
        binding.palletTV.visible()
    }

    override fun onBackPressed() {
        finish()
    }

}