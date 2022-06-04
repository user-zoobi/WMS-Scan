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
import com.example.wms_scan.databinding.ActivityWarehouseDetailsBinding

class WarehouseDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWarehouseDetailsBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var selectedRackNo = ""
    private var selectedShelveNo = ""
    private var selectedPalleteNo = ""

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
            intent.extras?.getBoolean(warehouse) == true ->
            {
                binding.shelfSpinnerCont.gone()
                binding.rackSpinnerCont.gone()

            }
            intent.extras?.getBoolean(racks) == true ->
            {
                binding.editDetailTV.text = "Edit racks details"
                binding.shelfSpinnerCont.gone()
            }
            intent.extras?.getBoolean(shelf) == true ->
            {
                binding.editDetailTV.text = "Edit shelf details"
            }
            intent.extras?.getBoolean(pallets) == true ->
            {
                binding.editDetailTV.text = "Edit pallets details"
            }
        } }

    private fun initListener(){

        binding.saveBtn.click {

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
                    dialog.show()
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

        /**
         *       GET RACK OBSERVER
         */

        viewModel.getRack.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{
                    dialog.show()
                }
                Status.SUCCESS ->{
                    dialog.dismiss()
                    Log.i("getRack","${it.data?.get(0)?.rackName}")
                    showRackSpinner(it.data!!)
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }

        })

        /**
         *       GET SHELF OBSERVER
         */

        viewModel.getShelf.observe(this,Observer{
            when(it.status){
                Status.LOADING ->{
                    dialog.show()
                }
                Status.SUCCESS ->{
                    try {
                        dialog.dismiss()
                        showShelfSpinner(it.data!!)

                    }catch (e:Exception){
                        Log.i("","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }
        })


        /**
         *       ADD PALLET OBSERVER
         */

        viewModel.addPallet.observe(this, Observer {
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

    private fun showRackSpinner(data:List<GetRackResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val rackSpinner = binding.rackSpinnerCont

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].rackName.toString()
        }
        val adapter: ArrayAdapter<String?> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        //setting adapter to spinner
        rackSpinner.adapter = adapter

        rackSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                selectedRackNo = data[position].rackNo.toString()
                viewModel.getShelf(
                    Utils.getSimpleTextBody(""),
                    Utils.getSimpleTextBody(selectedRackNo),
                    Utils.getSimpleTextBody(selectedBusLocNo),
                )

                Log.i("LocBus","This is rack pos ${adapter?.getItemAtPosition(position)}")
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun showShelfSpinner(data:List<GetShelfResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val shelfResponse = binding.shelfSpinnerCont

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].shelfName
            val adapter: ArrayAdapter<String?> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
            //setting adapter to spinner
            shelfResponse.adapter = adapter
            shelfResponse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

                override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                    Log.i("LocBus","This is shelf pos ${adapter?.getItemAtPosition(position)}")
                    selectedShelveNo = data[position].shelfNo.toString()
                    viewModel.addPallet(
                        Utils.getSimpleTextBody("3"),
                        Utils.getSimpleTextBody("PILOT 2"),
                        Utils.getSimpleTextBody("P-1"),
                        Utils.getSimpleTextBody(selectedBusLocNo),
                        Utils.getSimpleTextBody("20"),
                        Utils.getSimpleTextBody(selectedBusLocNo),
                        Utils.getSimpleTextBody(
                            LocalPreferences.getInt(this@WarehouseDetailsActivity,
                            LocalPreferences.AppLoginPreferences.userNo).toString()),
                        Utils.getSimpleTextBody("TEST")
                    )
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        onBackPressed()
    }


}