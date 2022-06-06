package com.example.wms_scan.ui

import android.content.Context
import android.content.Intent
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
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.busLocNo
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.userNo
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.adapter.racks.RackAdapter
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.adapter.warehouse.WarehouseAdapter
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityBusinessLocationBinding

class BusinessLocationActivity : AppCompatActivity() {

    /**
     *      INITIALIZERS
     */

    private lateinit var binding: ActivityBusinessLocationBinding
    private lateinit var warehouseAdapter: WarehouseAdapter
    private lateinit var racksAdapter: RackAdapter
    private lateinit var shelfAdapter: ShelfAdapter
    private lateinit var palletAdapter: PalletsAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var list: ArrayList<GetWarehouseResponse>
    private lateinit var rackList: ArrayList<GetRackResponse>
    private lateinit var shelfList: ArrayList<GetShelfResponse>
    private lateinit var palletList: ArrayList<GetPalletResponse>
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var selectedRackNo = ""
    private var selectedShelveNo = ""
    private var selectedPalletNo = ""
    private var screen = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusinessLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListeners()
        Log.i("getBusLocNo", LocalPreferences.getInt(this, busLocNo).toString())

        /**
         *      USER LOCATION OBSERVER
         */

        viewModel.userLocation(
            Utils.getSimpleTextBody(
                LocalPreferences.getInt(this@BusinessLocationActivity, userNo).toString()
            ))
        viewModel.userLoc.observe(this, Observer {
            when(it.status){
                Status.LOADING->{
                    dialog.show()
                }
                Status.SUCCESS ->{
                    dialog.dismiss()
                    showBusLocSpinner(it.data!!)
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }
        })

        /**
         *      GET WAREHOUSE OBSERVER
         */

        viewModel.getWarehouse.observe(this,Observer{
            when(it.status){
                Status.LOADING->{
                }
                Status.SUCCESS ->{

                    try {
                        it.data?.get(0)?.wHName?.let { it1 -> Log.i("warehouseResponse", it1) }
                        showWarehouseSpinner(it.data!!)

                        list = ArrayList()
                        list = it.data as ArrayList<GetWarehouseResponse>
                        warehouseAdapter = WarehouseAdapter(this,list)
                        binding.warehouseRV.apply {
                            layoutManager = LinearLayoutManager(this@BusinessLocationActivity)
                            adapter = warehouseAdapter
                        }
                    }
                    catch(e:Exception){
                        Log.i("rackAdapter","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }
                    //warehouseAdapter.addItems(list)
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }
        })

        /**
         *      GET RACK OBSERVER
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
                        rackList = ArrayList()
                        rackList = it.data as ArrayList<GetRackResponse>
                        racksAdapter = RackAdapter(rackList)

                        binding.racksRV.apply {

                            layoutManager = LinearLayoutManager(this@BusinessLocationActivity)
                            adapter = racksAdapter
                        }
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
         *      GET SHELF OBSERVER
         */

        viewModel.getShelf.observe(this,Observer{
            when(it.status){
                Status.LOADING ->{
                }
                Status.SUCCESS ->{
                    try {
                        showShelfSpinner(it.data!!)
                        shelfList = ArrayList()
                        shelfList = it.data as ArrayList<GetShelfResponse>
                        shelfAdapter = ShelfAdapter(shelfList)

                        binding.shelfRV.apply {
                            adapter = shelfAdapter
                            layoutManager = LinearLayoutManager(this@BusinessLocationActivity)
                        }
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
                Status.LOADING->{
                    Log.i("palletResponse","Data Loading")
                }
                Status.SUCCESS ->{
                    try
                    {
                        Log.i("palletResponse","Pallet Respond Found")
                        palletList = ArrayList()
                        palletList = it.data as ArrayList<GetPalletResponse>
                        palletAdapter = PalletsAdapter(palletList)

                        binding.palletsRV.apply {
                            adapter = palletAdapter
                            layoutManager = LinearLayoutManager(this@BusinessLocationActivity)
                        }

                        Log.i("palletResponse","${it.data[0].pilotName}")
                    }
                    catch (e:Exception){
                        Log.i("palletException","${e.message}")
                        Log.i("palletException","${e.stackTrace}")
                    }

                }
                Status.ERROR ->{
                    toast("Pallet error")
                }
            }
        })

    }


    private fun setupUi(){

        /**
         *      VIEWS FOR ACTIVITY
         */

        dialog = CustomProgressDialog(this)
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)

        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )

        /**
         *      VISIBILITY INTENTS VALUES
         */

        when {
            intent.extras?.getBoolean("warehouseKey") == true -> {
                binding.tvHeader.text = "Warehouse"
                binding.warehouseSpinnerCont.gone()
                binding.palletAddBTN.gone()
                binding.rackAddBTN.gone()
                binding.shelfAddBTN.gone()
                binding.warehouseRV.visible()
                screen = "W"
            }

            intent.extras?.getBoolean("rackKey") == true -> {
                binding.tvHeader.text = "Racks"
                binding.businessLocationSpinner.visible()
                binding.warehouseSpinnerCont.visible()
                binding.palletAddBTN.gone()
                binding.whAddBTN.gone()
                binding.shelfAddBTN.gone()
                binding.rackAddBTN.visible()
                binding.racksRV.visible()
                binding.whEditTV.gone()
                binding.rackEditTV.visible()
                screen = "R"
            }

            intent.extras?.getBoolean("shelfKey") == true -> {
                binding.tvHeader.text = "Shelves"
                binding.businessLocationSpinner.visible()
                binding.warehouseSpinnerCont.visible()
                binding.rackSpinnerCont.visible()
                binding.palletAddBTN.gone()
                binding.rackAddBTN.gone()
                binding.whAddBTN.gone()
                binding.shelfAddBTN.visible()
                binding.shelfRV.visible()
                binding.whEditTV.gone()
                binding.shelfEditTV.visible()
                screen = "S"
            }

            intent.extras?.getBoolean("palletKey") == true -> {
                binding.tvHeader.text = "Pallets"
                binding.businessLocationSpinner.visible()
                binding.warehouseSpinnerCont.visible()
                binding.rackSpinnerCont.visible()
                binding.shelfSpinnerCont.visible()
                binding.palletAddBTN.visible()
                binding.rackAddBTN.gone()
                binding.whAddBTN.gone()
                binding.shelfAddBTN.gone()
                binding.palletsRV.visible()
                binding.whEditTV.gone()
                binding.palletEditTV.visible()
                screen = "P"
            }
        }


    }

    /**
     *      LISTENERS FOR VIEWS
     */

    private fun initListeners(){

        list = ArrayList()
        warehouseAdapter = WarehouseAdapter(this, list)

        binding.toolbar.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            clearPreferences(this)
            true
        }

        // -------------------------------------------------------------------------------------//
        // ADD DETAILS LISTENERS

        binding.whAddBTN.click{
            gotoActivity(WarehouseDetailsActivity::class.java,"warehouseAdd", true)
        }

        binding.rackAddBTN.click {
            gotoActivity(AddUpdateRackDetails::class.java,"rackAdd", true)
        }

        binding.shelfAddBTN.click {
            gotoActivity(AddUpdateShelfDetails::class.java,"shelfAdd", true)
        }

        binding.palletAddBTN.click {
            gotoActivity(AddUpdatePalletDetails::class.java,"palletAdd", true)
        }

        // ------------------------------------------------------------------------------//
        // EDIT OR UPDATE LISTENERS

        binding.whEditTV.click {
            gotoActivity(WarehouseDetailsActivity::class.java, "warehouseUpdate", true)
        }

        binding.rackEditTV.click {
            gotoActivity(AddUpdateRackDetails::class.java, "racksUpdate", true)
        }

        binding.shelfEditTV.click {
            gotoActivity(AddUpdateShelfDetails::class.java, "shelfUpdate", true)
        }

        binding.palletEditTV.click {
            gotoActivity(AddUpdatePalletDetails::class.java, "palletsUpdate", true)
        }
    }

    /**
     *      SPINNERS SECTIONS
     */

    private fun showBusLocSpinner(data:List<UserLocationResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val businessLocSpinner = binding.businessLocationSpinner

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].busLocationName
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
        val warehouseSpinner = binding.warehouseSpinner

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
        val rackSpinner = binding.rackSpinner

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
                    Utils.getSimpleTextBody(selectedBusLocNo)
                )

                Log.i("LocBus","This is rack pos ${adapter?.getItemAtPosition(position)}")
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }


    private fun showShelfSpinner(data:List<GetShelfResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val shelfResponse = binding.shelfSpinner

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
                    viewModel.getPallet("",selectedShelveNo,selectedBusLocNo)
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    /**
     *  CLEAR ALL PREFERENCES
     */

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        onBackPressed()
    }

}