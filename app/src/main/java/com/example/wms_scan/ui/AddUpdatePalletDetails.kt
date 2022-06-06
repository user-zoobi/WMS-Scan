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
                Status.LOADING ->
                {

                }
                Status.SUCCESS ->
                {

                    Log.i("getWarehouse","${it.data?.get(0)?.wHName}")
                    showWarehouseSpinner(it.data!!)
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
                        showRackSpinner(it.data!!)

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

                        showShelfSpinner(it.data!!)

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

                        showPalletSpinner(it.data!!)

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
            items[i] = data[i].rackName
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
                    viewModel.getPallet(
                        "",selectedShelveNo,selectedBusLocNo
                    )
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun showPalletSpinner(data:List<GetPalletResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val palletResponse = binding.palletSpinnerCont

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].pilotName
            val adapter: ArrayAdapter<String?> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
            //setting adapter to spinner
            palletResponse.adapter = adapter
            palletResponse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

                override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                    Log.i("LocBus","This is shelf pos ${adapter?.getItemAtPosition(position)}")
                    selectedPalletNo = data[position].pilotNo.toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

}