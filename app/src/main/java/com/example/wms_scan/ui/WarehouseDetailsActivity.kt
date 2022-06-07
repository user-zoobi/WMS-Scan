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
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var updatedWarehouseName = ""
    private var spinnerWarehouseName = ""
    private lateinit var warehouseAdapter: WarehouseAdapter
    private lateinit var warehouseList: ArrayList<GetWarehouseResponse>

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
            intent.extras?.getBoolean("WHKey") == true -> {
                binding.addWarehouseBTN.gone()
                binding.updateWarehouseBtn.visible()
                val busName = intent.extras?.getString("busName")
                val wrhName = intent.extras?.getString("whName")
                binding.busLocTV.text = busName
                binding.warehouseTV.text = wrhName

            }
            intent.extras?.getBoolean("AddWHKey") == true -> {
                val busName = intent.extras?.getString("addBusName")
                binding.busLocTV.text = busName
                binding.warehouseCont.gone()
            }
        }

    }

    private fun initListener(){

        binding.addWarehouseBTN.click {
            updatedWarehouseName = binding.updateWarehouseET.text.toString()
            if (updatedWarehouseName.isNullOrEmpty()){
                toast("Field must not be empty")
            }else{
                viewModel.addUpdateWarehouse(
                    "0",
                    updatedWarehouseName,
                    "WH-1",
                    selectedBusLocNo,
                    LocalPreferences.getInt(this, LocalPreferences.AppLoginPreferences.userNo).toString(),
                    "Test-PC"
                )
                toast("Warehouse Added")
            }
        }

        binding.updateWarehouseBtn.click {
            if (updatedWarehouseName.isNullOrEmpty()){
                toast("Field must not be empty")
            }
            else{
                updatedWarehouseName = binding.updateWarehouseET.text.toString()
                viewModel.addUpdateWarehouse(
                    selectedWareHouseNo,
                    updatedWarehouseName,
                    "WH-1",
                    selectedBusLocNo,
                    LocalPreferences.getInt(this, LocalPreferences.AppLoginPreferences.userNo).toString(),
                    "Test-PC"
                )
                toast("Warehouse updated")
            }
        }
    }

    private fun initObserver(){

        /**     GETTING DATA PORTION STARTED HERE
         *  ------------------------------------------------------------------------------------------------------
         */


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
                    showBusLocSpinner(it.data!!)
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
                    dialog.dismiss()
                    Log.i("getWarehouse","${it.data?.get(0)?.wHName}")
                    showWarehouseSpinner(it.data!!)
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }
        })


        /**     ADDING DATA PORTION STARTED HERE
         *  ------------------------------------------------------------------------------------------------------
         *
         *  */


        /**
         *       ADD WAREHOUSE OBSERVER
         */

        viewModel.addUpdateWarehouse.observe(this, Observer {
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
        })
    }

    private fun showBusLocSpinner(data:List<UserLocationResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val businessLocSpinner = binding.businessSpinnerCont

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].busLocationName.toString()
        }
        val adapter: ArrayAdapter<String?> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        //setting adapter to spinner
        businessLocSpinner.adapter = adapter

        businessLocSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                Log.i("LocBus","business Location no ${data[position].orgBusLocNo}")
                // binding.rackSpinnerCont.visible()
                selectedBusLocNo = data[position].orgBusLocNo.toString()
                viewModel.getWarehouse("", selectedBusLocNo)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun showWarehouseSpinner(data:List<GetWarehouseResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val warehouseSpinner = binding.warehouseSpinnerCont

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].wHName
        }
        val adapter: ArrayAdapter<String?> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        //setting adapter to spinner
        warehouseSpinner.adapter = adapter
        warehouseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                selectedWareHouseNo = data[position].wHNo.toString()
                spinnerWarehouseName = data[position].wHName.toString()
                viewModel.getRack(
                    Utils.getSimpleTextBody(""),
                    Utils.getSimpleTextBody(selectedWareHouseNo),
                    Utils.getSimpleTextBody(selectedBusLocNo)
                )
                Log.i("LocBus","This is warehouse name is ${adapter?.getItemAtPosition(position)}")
                Log.i("LocBus","This is warehouse pos is ${data[position].wHNo}")
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
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