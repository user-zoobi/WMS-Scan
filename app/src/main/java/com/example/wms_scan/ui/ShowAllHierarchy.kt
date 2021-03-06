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
import com.example.scanmate.util.Constants.Toast.noRecordFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.userNo
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.carton.CartonDetailAdapter
import com.example.wms_scan.adapter.carton.ScanCartonAdapter
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.adapter.pallets.ScanPalletAdapter
import com.example.wms_scan.adapter.racks.RackAdapter
import com.example.wms_scan.adapter.racks.ScanRackAdapter
import com.example.wms_scan.adapter.shelf.ScanShelfAdapter
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.adapter.warehouse.ScanWarehouseAdapter
import com.example.wms_scan.adapter.warehouse.WarehouseAdapter
import com.example.wms_scan.data.response.GetCartonQnWiseResponse
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
    private lateinit var cartonQnWiseAdapter: CartonDetailAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanAllHierarchyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUi()
        initObserver()
        initListener()



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
                    binding.hierarchyCont.visible()
                    it.let {
                        try
                        {
                            val whName = it.data?.get(0)?.wHName.toString()
                            val rackName = it.data?.get(0)?.rackName.toString()
                            val shelfName = it.data?.get(0)?.shelfName.toString()
                            val palletName = it.data?.get(0)?.pilotName.toString()
                            val whNo = it.data?.get(0)?.wHNo.toString()
                            val rackNo = it.data?.get(0)?.rackNo.toString()
                            val shelfNo = it.data?.get(0)?.shelfNo.toString()
                            val palletNo = it.data?.get(0)?.pilotNo.toString()
                            val busLocNo = it.data?.get(0)?.locationNo.toString()

                            binding.WHTV.text = whName
                            binding.rackTV.text = rackName
                            binding.shelfTV.text = shelfName
                            binding.palletTV.text = palletName

                            Log.i("allHierarchy",it.data?.get(0)?.rackCode.toString())

                            var warehouse = intent.extras?.getString("w").toString()
                            var rack = intent.extras?.getString("r").toString()
                            var shelve = intent.extras?.getString("s").toString()
                            var palette = intent.extras?.getString("p").toString()

                            Log.i("scannerCameraActivity2",rack.toString())
                            Log.i("scannerCameraActivity2",warehouse.toString())
                            Log.i("scannerCameraActivity2",shelve.toString())
                            Log.i("scannerCameraActivity2",palette.toString())
                            Log.i("scannerCameraActivity2",busLocNo.toString())

                            when
                            {

                                palette.contains("PL") ->{
                                    viewModel.getCarton(
                                        Utils.getSimpleTextBody(palletNo),
                                        Utils.getSimpleTextBody(busLocNo)
                                    )
                                    Log.i("palLoc","${it.data?.get(0)?.pilotCode}")
                                    Log.i("palletNoScan",palletNo)
                                }

                                shelve.contains("SF") ->{

                                    viewModel.getPallet(
                                        Utils.getSimpleTextBody(""),
                                        Utils.getSimpleTextBody(shelfNo),
                                        Utils.getSimpleTextBody(busLocNo),
                                    )
                                    binding.view7.gone()
                                    binding.view8.gone()
                                    binding.palletCont.gone()
                                }

                                rack.contains("RK") ->{

                                    viewModel.getShelf(
                                        Utils.getSimpleTextBody(""),
                                        Utils.getSimpleTextBody(rackNo),
                                        Utils.getSimpleTextBody(busLocNo),
                                    )

                                    binding.view5.gone()
                                    binding.view6.gone()
                                    binding.shelfCont.gone()
                                    binding.view7.gone()
                                    binding.view8.gone()
                                    binding.palletCont.gone()

                                }

                                warehouse.contains("WH") -> {

                                    viewModel.getRack(
                                        Utils.getSimpleTextBody(""),
                                        Utils.getSimpleTextBody(whNo),
                                        Utils.getSimpleTextBody(busLocNo)
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

                            }

                            binding.WHTV.click {
                                viewModel.getRack(
                                    Utils.getSimpleTextBody(""),
                                    Utils.getSimpleTextBody(whNo),
                                    Utils.getSimpleTextBody(busLocNo),
                                )
                            }

                            binding.rackTV.click {
                                viewModel.getShelf(
                                    Utils.getSimpleTextBody(""),
                                    Utils.getSimpleTextBody(rackNo),
                                    Utils.getSimpleTextBody(busLocNo)
                                )
                            }

                            binding.shelfTV.click {
                                viewModel.getPallet(
                                    Utils.getSimpleTextBody(""),
                                    Utils.getSimpleTextBody(shelfNo),
                                    Utils.getSimpleTextBody(busLocNo)
                                )
                            }

                            binding.palletTV.click {
                                viewModel.getCarton(
                                    Utils.getSimpleTextBody(palletNo),
                                    Utils.getSimpleTextBody(busLocNo),
                                )
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
                Status.SUCCESS ->
                {
                    binding.hierarchyCont.visible()
                    warehouseAdapter = ScanWarehouseAdapter(this,
                        it.data as ArrayList<GetWarehouseResponse>
                    )
                    binding.showAllRV.apply{
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = warehouseAdapter



                        if (it.data[0].wHNo.toString() == "0")
                        {
                            adapter = null
                            toast(noRecordFound)
                        }
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
                Status.LOADING ->{}

                Status.SUCCESS ->
                {
                    binding.hierarchyCont.visible()
                    racksAdapter = ScanRackAdapter(this,
                        it.data as ArrayList<GetRackResponse>
                    )
                    binding.showAllRV.apply {
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = racksAdapter

                        if (it.data[0].rackNo.toString() == "0")
                        {
                            adapter = null
                            toast(noRecordFound)
                        }
                    }
                    binding.itemTV.text = it.data[0].wHName
                    Log.i("warehouseCode", it.data[0].wHName.toString())
                }
                Status.ERROR ->
                {

                }
            }
        })

        viewModel.getShelf.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{}

                Status.SUCCESS ->{
                    binding.hierarchyCont.visible()
                    shelfAdapter = ScanShelfAdapter(this,
                        it.data as ArrayList<GetShelfResponse>
                    )
                    binding.showAllRV.apply {
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = shelfAdapter

                        if (it.data[0].shelfNo.toString() == "0")
                        {
                            adapter = null
                            toast(noRecordFound)
                        }
                    }
                    binding.itemTV.text = it.data[0].rackName
                    Log.i("shelfData", it.data[0].shelfName.toString())
                }
                Status.ERROR ->
                {
                }
            }
        })

        viewModel.getPallet.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    binding.hierarchyCont.visible()
                    palletAdapter = ScanPalletAdapter(this,
                        it.data as ArrayList<GetPalletResponse>
                    )
                    binding.showAllRV.apply {
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = palletAdapter

                        if (it.data[0].pilotNo.toString() == "0")
                        {
                            adapter = null
                            toast(noRecordFound)
                        }
                    }
                    binding.itemTV.text = it.data[0].shelfName
                }
                Status.ERROR ->
                {
                    binding.hierarchyTree.gone()
                    binding.treeView.gone()
                    binding.hierarchyCont.gone()
                }
            }
        })

        viewModel.getCarton.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    binding.hierarchyCont.visible()
                    cartonAdapter = ScanCartonAdapter(this,
                        it.data as ArrayList<GetCartonResponse>
                    )
                    binding.showAllRV.apply {
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = cartonAdapter

                        if (it.data[0].cartonNo.toString() == "0")
                        {
                            adapter = null
                            toast(noRecordFound)
                        }
                    }

                    binding.itemTV.text = it.data[0].pilotName

                    Log.i("getCartonData", it.data[0].toString())
                }

                Status.ERROR ->
                {
                    binding.hierarchyTree.gone()
                    binding.treeView.gone()
                }
            }
        })

        viewModel.getCartonQnWise.observe(this, Observer{

            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    cartonQnWiseAdapter = CartonDetailAdapter(this,
                        it.data as ArrayList<GetCartonQnWiseResponse>
                    )
                    binding.showAllRV.apply {
                        layoutManager = LinearLayoutManager(this@ShowAllHierarchy)
                        adapter = cartonQnWiseAdapter
                        Log.i("analyticalNo",it.data.get(0).analyticalNo.toString())
                        binding.itemCode.visible()
                        binding.slash.visible()

                        if (it.data.get(0).cartonNo.toString() == "0")
                        {
                            adapter = null

                        }

                        binding.itemTV.text = it.data.get(0).analyticalNo.toString()
                        binding.itemCode.text = it.data.get(0).itemCode.toString()
                    }
                }
                Status.ERROR -> {
                    toast("onError")
                    binding.hierarchyTree.gone()
                    binding.treeView.gone()
                }
            }
        })
    }

    private fun initListener(){
        binding.scanIV.click {
            val intent = Intent(this, ScannerCameraActivity::class.java)
            finish()
            startActivity(intent)
        }
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

    fun analyticalNoAction(analyticalNo:String){
        Log.i("analyticalNo",analyticalNo)
        viewModel.getCartonQnWise(analyticalNo)
    }

    override fun onBackPressed()
    {
        finish()
    }

    override fun onResume() {
        super.onResume()

        var warehouse = intent.extras?.getString("w").toString()
        var rack = intent.extras?.getString("r").toString()
        var shelve = intent.extras?.getString("s").toString()
        var palette = intent.extras?.getString("p").toString()
        var scannedData = intent.extras?.getString("scannedData").toString()
        val locationNo = scannedData.substringBefore("L")

        if(palette.contains("PL") )
        {
            viewModel.scanAll("$palette", "$locationNo")

        }
        if (shelve.contains("SF"))
        {
            viewModel.scanAll("$shelve", "$locationNo")

        }
        if (rack.contains("RK"))
        {
            viewModel.scanAll("$rack", "$locationNo")

        }
        if (warehouse.contains("WH"))
        {
            viewModel.scanAll("$warehouse", "$locationNo")

        }

        Log.i("scannerCameraActivity1",rack.toString())
        Log.i("scannerCameraActivity1",warehouse.toString())
        Log.i("scannerCameraActivity1",shelve.toString())
        Log.i("scannerCameraActivity1",palette.toString())
        Log.i("scannerCameraActivity1",locationNo.toString())
    }


}