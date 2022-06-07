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
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var selectedRackNo = ""
    private var selectedShelveNo = ""
    private var selectedPalletNo = ""
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

                val palletName = intent.extras?.getString("updatePallet")
                val busLocName = intent.extras?.getString("updateBusLoc")
                val warehouseName = intent.extras?.getString("updateWarehouse")
                val rackName = intent.extras?.getString("updateRacks")
                val shelfName = intent.extras?.getString("updateShelf")

                binding.palletTV.text = palletName
                binding.businessTV.text = busLocName
                binding.warehouseTV.text = warehouseName
                binding.rackTV.text = rackName
                binding.shelfTV.text = shelfName
                binding.addPalletBtn.gone()
                binding.updatePalletBtn.visible()
            }

            intent.extras?.getBoolean("AddPalletKey") == true ->{

                val palletName = intent.extras?.getString("updatePallet")
                val busLocName = intent.extras?.getString("addBusLoc")
                val warehouseName = intent.extras?.getString("addWarehouse")
                val rackName = intent.extras?.getString("addRack")
                val shelfName = intent.extras?.getString("addShelf")

                binding.palletTV.text = palletName
                binding.businessTV.text = busLocName
                binding.warehouseTV.text = warehouseName
                binding.rackTV.text = rackName
                binding.shelfTV.text = shelfName
                binding.palletCont.gone()
            }
        }
    }

    private fun initListener(){

        // ADD YOUR PALLET

        binding.addPalletBtn.click {

            palletName = binding.palletNameET.text.toString()
            if (palletName.isNullOrEmpty())
            {
                toast("Field must not be empty")
            }
            else
            {
                viewModel.addPallet(
                    Utils.getSimpleTextBody("0"),
                    Utils.getSimpleTextBody(palletName),
                    Utils.getSimpleTextBody("P-1"),
                    Utils.getSimpleTextBody(selectedShelveNo),
                    Utils.getSimpleTextBody("20"),
                    Utils.getSimpleTextBody(selectedBusLocNo),
                    Utils.getSimpleTextBody(
                        LocalPreferences.getInt(this,LocalPreferences.AppLoginPreferences.userNo).toString()
                    ),
                    Utils.getSimpleTextBody("TEST"),
                )
                toast("Pallet added")
            }

        }

        // UPDATE YOUR PALLET
        binding.updatePalletBtn.click {
            palletName = binding.palletNameET.text.toString()
            if (palletName.isNullOrEmpty())
            {
                toast("Pallet updated")
            }
            else
            {
                viewModel.addPallet(
                    Utils.getSimpleTextBody(selectedPalletNo),
                    Utils.getSimpleTextBody(palletName),
                    Utils.getSimpleTextBody("P-1"),
                    Utils.getSimpleTextBody(selectedShelveNo),
                    Utils.getSimpleTextBody("20"),
                    Utils.getSimpleTextBody(selectedBusLocNo),
                    Utils.getSimpleTextBody(
                        LocalPreferences.getInt(this,LocalPreferences.AppLoginPreferences.userNo).toString()
                    ),
                    Utils.getSimpleTextBody("TEST"),
                )
            }

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
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    try {


                    }catch (e:Exception){
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
                    try {


                    }catch (e:Exception){
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