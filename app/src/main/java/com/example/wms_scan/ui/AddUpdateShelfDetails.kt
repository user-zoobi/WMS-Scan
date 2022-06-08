package com.example.wms_scan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.databinding.ActivityAddUpdateShelfDetailsBinding

class AddUpdateShelfDetails : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var binding: ActivityAddUpdateShelfDetailsBinding
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var selectedRackNo = ""
    private var selectedShelveNo = ""
    private var selectedPalletNo = ""
    private var shelfNameInput = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateShelfDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListeners()
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

        when {
            intent.extras?.getBoolean("SHELFKey") == true -> {
                binding.addShelfBtn.gone()
                binding.updateShelfBtn.visible()

                //edit button data
                val busLocName = intent.extras?.getString("sBusinessLocName")
                binding.busLocTV.text = busLocName

                val warehouseName = intent.extras?.getString("sWarehouse")
                binding.warehouseTV.text = warehouseName

                val rackName = intent.extras?.getString("sRackName")
                binding.rackTV.text = rackName

                binding.editDetailTV.text = "Update to"
            }

            intent.extras?.getBoolean("shelfAddKey") == true -> {
                //add button data

                val addBusLocName = intent.extras?.getString("addBusinessLocName")
                binding.busLocTV.text = addBusLocName

                val addWHName = intent.extras?.getString("addWarehouse")
                binding.warehouseTV.text = addWHName

                val addRackName = intent.extras?.getString("addRackName")
                binding.rackTV.text = addRackName
            }
        }

    }

    private fun initListeners(){

        binding.addShelfBtn.click {
            shelfNameInput = binding.shelfNameET.text.toString()
            if (shelfNameInput.isNullOrEmpty())
            {
                toast("Field must not be empty")
            }
            else
            {
                viewModel.addShelf(
                    Utils.getSimpleTextBody("0"),
                    Utils.getSimpleTextBody(selectedRackNo),
                    Utils.getSimpleTextBody(shelfNameInput),
                    Utils.getSimpleTextBody("S-1"),
                    Utils.getSimpleTextBody("10"),
                    Utils.getSimpleTextBody(selectedBusLocNo),
                    Utils.getSimpleTextBody(
                        LocalPreferences.getInt(this,LocalPreferences.AppLoginPreferences.userNo).toString()
                    ),
                    Utils.getSimpleTextBody("TEST")
                )
                toast("Shelf Added")
            }

        }

        binding.updateShelfBtn.click {
            shelfNameInput = binding.shelfNameET.text.toString()
            if (shelfNameInput.isNullOrEmpty())
            {
                toast("Field must not be empty")
            }
            else
            {
                viewModel.addShelf(
                    Utils.getSimpleTextBody(selectedShelveNo),
                    Utils.getSimpleTextBody(selectedRackNo),
                    Utils.getSimpleTextBody(shelfNameInput),
                    Utils.getSimpleTextBody("S-1"),
                    Utils.getSimpleTextBody("10"),
                    Utils.getSimpleTextBody(selectedBusLocNo),
                    Utils.getSimpleTextBody(
                        LocalPreferences.getInt(this,LocalPreferences.AppLoginPreferences.userNo).toString()
                    ),
                    Utils.getSimpleTextBody("TEST")
                )
                toast("Shelf Updated")
            }

        }
    }

    private fun initObserver(){

        /**
         *       GET USER LOCATION OBSERVER
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
                Status.LOADING ->{
                }
                Status.SUCCESS ->{

                    Log.i("getWarehouse","${it.data?.get(0)?.wHName}")

                }
                Status.ERROR ->{

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
                    dialog.dismiss()
                }
            }
        })

        /**
         *       ADD SHELF OBSERVER
         */

        viewModel.addShelf.observe(this, Observer{
            when(it.status){
                Status.LOADING ->{
                    dialog.show()
                }
                Status.SUCCESS ->{
                    dialog.dismiss()
                    Log.i("addPallet","${it.data?.error}")
                }
                Status.ERROR ->{
                    dialog.dismiss()
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
    }

}