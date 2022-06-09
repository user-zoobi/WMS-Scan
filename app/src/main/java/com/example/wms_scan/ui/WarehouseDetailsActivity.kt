package com.example.wms_scan.ui

import android.content.Context
import android.content.SharedPreferences
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
import com.example.scanmate.util.Constants.WMSStructure.pallets
import com.example.scanmate.util.Constants.WMSStructure.racks
import com.example.scanmate.util.Constants.WMSStructure.shelf
import com.example.scanmate.util.Constants.WMSStructure.warehouse
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.userNo
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.adapter.warehouse.WarehouseAdapter
import com.example.wms_scan.databinding.ActivityWarehouseDetailsBinding

class WarehouseDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWarehouseDetailsBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private var selectedBusLocNo:String? = ""
    private var selectedWHNo:String? = ""
    private var updatedWarehouseName = ""
    private var selectedBusLocName:String? = ""
    private var selectedWhName:String? = ""
    private var updatedBusLocNo:String? = ""
    private var updatedBusLocName:String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWarehouseDetailsBinding.inflate(layoutInflater)
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

        binding.toolbar.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            clearPreferences(this)
            true
        }

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
            intent.extras?.getBoolean("AddWHKey") == true -> {

                selectedBusLocName = intent.extras?.getString("addBusName")
                selectedBusLocNo = intent.extras?.getString("addBusLocNo")
                selectedWHNo = intent.extras?.getString("addWhNo")

                binding.busLocTV.text = selectedBusLocName
                binding.warehouseTV.text = selectedWhName
                binding.warehouseCont.gone()
                Log.i("warehouseValues","THE KEYS ARE VALUES WITH selectedBusLocNo :$selectedBusLocNo\n $selectedBusLocName\n $selectedWHNo")
            }

            intent.extras?.getBoolean("UpdateWHKey") == true -> {
                updatedBusLocName = intent.extras?.getString("updateBusName")
                updatedBusLocNo = intent.extras?.getString("updateBusLocNo")
                selectedWHNo = intent.extras?.getString("updateWhNo")
                selectedWhName = intent.extras?.getString("updateWhName")

                binding.busLocTV.text = updatedBusLocName
                binding.warehouseTV.text = selectedWhName
                binding.addWarehouseBTN.gone()
                binding.updateWarehouseBtn.visible()
                binding.updateWarehouseET.hint = "Update warehouse"
                Log.i("updateWHValues","THE KEYS ARE VALUES ARE :$selectedWHNo \n$selectedWhName")
            }
        }
    }

    private fun initListener(){

        binding.addWarehouseBTN.click {
            updatedWarehouseName = binding.updateWarehouseET.text.toString()
            viewModel.addUpdateWarehouse(
                "0",
                updatedWarehouseName,
                "WH-1",
                "$selectedBusLocNo",
                LocalPreferences.getInt(this, userNo).toString(),
                "Test-PC"
            )
            toast("warehouse added")
        }

        binding.updateWarehouseBtn.click {
            updatedWarehouseName = binding.updateWarehouseET.text.toString()
            viewModel.addUpdateWarehouse(
                "$selectedWHNo",
                "$updatedWarehouseName",
                "WH-1",
                "$updatedBusLocNo",
                LocalPreferences.getInt(this, userNo).toString(),
                "Test-PC"
            )
            toast("warehouse updated")
        }
    }

    private fun initObserver(){

        /**     GETTING DATA PORTION STARTED HERE
         *  ------------------------------------------------------------------------------------------------------
         */



        /**
         *       ADD WAREHOUSE OBSERVER
         */

        viewModel.addUpdateWarehouse.observe(this, Observer {
            it.let {
                when(it.status){
                    Status.LOADING ->{
                        dialog.show()
                    }
                    Status.SUCCESS ->{
                        dialog.dismiss()
                        Log.i("warehouseAdded",it.data?.error.toString())

                    }
                    Status.ERROR ->{
                        dialog.dismiss()
                        toast("")
                    }
                }
            }
        })
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        onBackPressed()
    }

    private fun validate(){

    }


}