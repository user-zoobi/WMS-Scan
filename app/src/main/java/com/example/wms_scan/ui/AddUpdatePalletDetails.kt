package com.example.wms_scan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import com.example.scanmate.data.callback.Status
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.data.response.GetShelfResponse
import com.example.scanmate.data.response.GetWarehouseResponse
import com.example.scanmate.data.response.UserLocationResponse
import com.example.scanmate.extensions.*
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.isRefreshRequired
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.userNo
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityAddUpdatePalletDetailsBinding
import com.example.wms_scan.databinding.ActivityAddUpdateShelfDetailsBinding

class AddUpdatePalletDetails : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var binding:ActivityAddUpdatePalletDetailsBinding
    private var selectedBusLocNo:String? = ""
    private var selectedWareHouseNo:String?  = ""
    private var selectedRackNo:String?  = ""
    private var selectedShelveNo:String?  = ""
    private var selectedBusLocName:String?  = ""
    private var selectedWHName:String?  = ""
    private var selectedRackName:String?  = ""
    private var selectedShelfName:String?  = ""
    private var selectedPalletName:String?  = ""
    private var selectedPalletNo:String?  = ""
    private var updatedBusLocNo:String?  = ""
    private var updatedBusLocName:String?  = ""
    private var updatedWarehouseName:String?  = ""
    private var updatedWarehouseNo:String?  = ""
    private var updatedRackName:String?  = ""
    private var updatedRackNo:String?  = ""
    private var updatedShelfName:String?  = ""
    private var updatedShelfNo:String?  = ""
    private var updatedPalletNo:String?  = ""
    private var updatedPalletName:String?  = ""
    private var palletName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdatePalletDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListener()
        initObserver()
    }

    private fun setupUi(){

        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        dialog = CustomProgressDialog(this)

        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )

        when{
            intent.extras?.getBoolean("UpdatePalletKey") == true -> {

                updatedBusLocNo = intent.extras?.getString("updatedBusLocNo")
                updatedWarehouseNo = intent.extras?.getString("updatedWHNo")
                updatedRackNo = intent.extras?.getString("updatedRackNo")
                updatedShelfNo = intent.extras?.getString("updatedShelveNo")
                updatedBusLocName = intent.extras?.getString("updatedBusLocName")
                updatedWarehouseName = intent.extras?.getString("updatedWHName")
                updatedRackName = intent.extras?.getString("updatedRackName")
                updatedShelfName = intent.extras?.getString("updatedShelveName")
                updatedPalletName= intent.extras?.getString("updatedPalletName")
                updatedPalletNo = intent.extras?.getString("updatedPalletNo")

                binding.businessTV.text = updatedBusLocName
                binding.warehouseTV.text = updatedWarehouseName
                binding.rackTV.text = updatedRackName
                binding.shelfTV.text = updatedShelfName
                binding.palletNameET.text = updatedPalletName?.toEditable()
                binding.addPalletBtn.gone()
                binding.updatePalletBtn.visible()
                binding.palletNameET.hint = "Update pallet"
            }

            intent.extras?.getBoolean("AddPalletKey") == true ->{

                selectedBusLocNo = intent.extras?.getString("addBusLocNo")
                selectedWareHouseNo = intent.extras?.getString("addWHNo")
                selectedRackNo = intent.extras?.getString("addRackNo")
                selectedShelveNo = intent.extras?.getString("addShelfNo")
                selectedBusLocName = intent.extras?.getString("addBusLocName")
                selectedWHName = intent.extras?.getString("addWHName")
                selectedRackName = intent.extras?.getString("addRackName")
                selectedShelfName = intent.extras?.getString("addShelfName")

                binding.businessTV.text = selectedBusLocName
                binding.warehouseTV.text = selectedWHName
                binding.rackTV.text = selectedRackName
                binding.shelfTV.text = selectedShelfName

            }
        }
    }

    private fun initListener(){

        // ADD YOUR PALLET

        binding.addPalletBtn.click {
            LocalPreferences.put(this,isRefreshRequired, true)
            palletName = binding.palletNameET.text.toString()
            viewModel.addPallet(
                Utils.getSimpleTextBody("0"),
                Utils.getSimpleTextBody(palletName),
                Utils.getSimpleTextBody("P-1"),
                Utils.getSimpleTextBody("$selectedShelveNo"),
                Utils.getSimpleTextBody("20"),
                Utils.getSimpleTextBody("$selectedBusLocNo"),
                Utils.getSimpleTextBody("${LocalPreferences.getInt(this, userNo)}"),
                Utils.getSimpleTextBody("TEST"),
            )
            toast("pallet added")
            finish()

        }

        // UPDATE YOUR PALLET
        binding.updatePalletBtn.click {
            palletName = binding.palletNameET.text.toString()
            viewModel.addPallet(
                Utils.getSimpleTextBody("$updatedPalletNo"),
                Utils.getSimpleTextBody(palletName),
                Utils.getSimpleTextBody("P-1"),
                Utils.getSimpleTextBody("$updatedShelfNo"),
                Utils.getSimpleTextBody("20"),
                Utils.getSimpleTextBody("$updatedBusLocNo"),
                Utils.getSimpleTextBody("${LocalPreferences.getInt(this, userNo)}"),
                Utils.getSimpleTextBody("TEST"),
            )
            toast("pallet updated")
            finish()
        }


    }

    private fun initObserver(){

        /**
         *       GET BUSINESS LOCATION OBSERVER
         */

        viewModel.userLocation(
            Utils.getSimpleTextBody("2"),
        )
        viewModel.userLoc.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{
                    dialog.show()
                }
                Status.SUCCESS ->{
                    dialog.dismiss()
                    Log.i("addShelf","${it.data?.get(0)?.busLocationName}")
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }
        })

        /**
         *       GET WAREHOUSE OBSERVER
         */

        viewModel.getWarehouse.observe(this, Observer {
            when(it.status){
                Status.LOADING ->
                {

                }
                Status.SUCCESS ->
                {

                    Log.i("getWarehouse","${it.data?.get(0)?.wHName}")
                }
                Status.ERROR ->
                {

                }
            }
        })

        /**
         *       GET RACK OBSERVER
         */

        viewModel.getRack.observe(this, Observer{
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{

                    // Log.i("getRack",it.data?.get(0)?.rackNo.toString())
                    try
                    {

                    }
                    catch (e: Exception)
                    {
                        Log.i("RACK_OBSERVER","${e.message}")
                        Log.i("RACK_OBSERVER","${e.stackTrace}")
                    }
                }
                Status.ERROR ->{

                }
            }
        })

        /**
         *      GET SHELF OBSERVER
         */

        viewModel.getShelf.observe(this,Observer{
            when(it.status)
            {
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    try
                    {

                    }
                    catch (e:Exception)
                    {
                        Log.i("","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }
                }
                Status.ERROR ->{

                }
            }
        })

        /**
         *      GET PALLET OBSERVER
         */
        viewModel.getPallet.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    try
                    {


                    }
                    catch (e:Exception){
                        Log.i("","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }
                }
                Status.ERROR ->{

                }
            }
        })

        /**
         *      ADD AND UPDATE PALLET OBSERVER
         */
        viewModel.addPallet.observe(this, Observer {
            when(it.status)
            {
                Status.LOADING ->
                {
                    dialog.show()
                }
                Status.SUCCESS ->{
                    try
                    {
                        dialog.dismiss()
                        Log.i("","${it.data?.error}")
                    }
                    catch (e:Exception)
                    {
                        Log.i("","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }
                }
                Status.ERROR ->
                {
                    dialog.dismiss()
                }
            }
        })
    }
}