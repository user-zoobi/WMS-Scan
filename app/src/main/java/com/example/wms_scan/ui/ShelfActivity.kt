package com.example.wms_scan.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
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
import com.example.scanmate.util.Constants
import com.example.scanmate.util.Constants.Toast.NoInternetFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.isRefreshRequired
import com.example.scanmate.util.Utils
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.adapter.racks.RackAdapter
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.adapter.warehouse.WarehouseAdapter
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityRacksBinding
import com.example.wms_scan.databinding.ActivityShelfBinding

class ShelfActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShelfBinding
    private lateinit var shelfAdapter: ShelfAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var list: ArrayList<GetWarehouseResponse>
    private lateinit var shelfList: ArrayList<GetShelfResponse>
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var selectedRackNo = ""
    private var busLocName = ""
    private var warehouseName = ""
    private var rackName = ""
    private var shelfName = ""
    private var selectedShelveNo = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShelfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListener()
        initObserver()
    }

    private fun setupUi(){
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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

    }

    private fun initListener(){

        binding.toolbar.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            clearPreferences(this)
            true
        }

        binding.shelfAddBTN.click {
            if (isNetworkConnected(this))
            {
                val intent = Intent(this, AddUpdateShelfDetails::class.java)
                intent.putExtra("addBusLocNo",selectedBusLocNo)
                intent.putExtra("addWHNo",selectedWareHouseNo)
                intent.putExtra("addRackNo",selectedRackNo)
                intent.putExtra("addShelfNo",selectedShelveNo)
                intent.putExtra("addBusLocName",busLocName)
                intent.putExtra("addWHName",warehouseName)
                intent.putExtra("addRackName",rackName)
                intent.putExtra("AddShelfKey",true)
                startActivity(intent)
            }
            else
            {
                toast(NoInternetFound)
            }
        }

        binding.refresh.click {

            if (isNetworkConnected(this))
            {
                viewModel.userLocation(
                    Utils.getSimpleTextBody(
                        LocalPreferences.getInt(this, LocalPreferences.AppLoginPreferences.userNo).toString()
                    )
                )
                viewModel.getWarehouse("", selectedBusLocNo)

                viewModel.getRack(
                    Utils.getSimpleTextBody(""),
                    Utils.getSimpleTextBody(selectedWareHouseNo),
                    Utils.getSimpleTextBody(selectedBusLocNo)
                )
            }
        }
    }

    fun showAction(shelfName:String,shelfNo:String){
        if (Utils.isNetworkConnected(this))
        {
            val intent = Intent(this, AddUpdateShelfDetails::class.java)

            intent.putExtra("updateBusLocNo",selectedBusLocNo)
            intent.putExtra("updateWHNo",selectedWareHouseNo)
            intent.putExtra("updateRackNo",selectedRackNo)
            intent.putExtra("updateShelfNo",shelfNo)
            intent.putExtra("updateBusinessLocName",busLocName)
            intent.putExtra("updateWHName",warehouseName)
            intent.putExtra("updateRackName",rackName)
            intent.putExtra("updateShelfName",shelfName)
            intent.putExtra("UpdateShelfKey",true)
            startActivity(intent)
        }
        else
        {
            toast(NoInternetFound)
        }
    }

    private fun initObserver(){

        /**
         *      USER LOCATION OBSERVER
         */

        viewModel.userLocation(
            Utils.getSimpleTextBody(
                LocalPreferences.getInt(this,
                    LocalPreferences.AppLoginPreferences.userNo
                ).toString()
            ))
        viewModel.userLoc.observe(this, Observer {
            when(it.status){
                Status.LOADING->{
                    dialog.show()
                }
                Status.SUCCESS ->{
                    if (isNetworkConnected(this)){
                        try
                        {
                            if(it.data?.get(0)?.status == true)
                            {
                                dialog.dismiss()
                                showBusLocSpinner(it.data)
                            }
                            else
                            {
                                binding.shelfRV.adapter = null
                            }
                        }
                        catch (e:Exception)
                        {

                        }
                    }
                    else
                    {
                        binding.shelfRV.adapter = null
                    }
                }
                Status.ERROR ->{
                    dialog.dismiss()
                }
            }
        })


        /**
         *      GET WAREHOUSE OBSERVER
         */

        viewModel.getWarehouse.observe(this, Observer{
            when(it.status){
                Status.LOADING->{
                }
                Status.SUCCESS ->{
                    if (isNetworkConnected(this)){

                        try {
                            if(it.data?.get(0)?.status == true)
                            {
                                it.data[0].wHName?.let { it1 -> Log.i("warehouseResponse", it1) }
                                showWarehouseSpinner(it.data)
                            }
                            else
                            {
                                binding.shelfRV.adapter = null
                            }
                        }

                        catch(e:Exception){
                            Log.i("rackAdapter","${e.message}")
                            Log.i("rackAdapter","${e.stackTrace}")
                        }
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
                    if (isNetworkConnected(this)){
                        try
                        {
                            if(it.data?.get(0)?.status == true)
                            {
                                showRackSpinner(it.data)
                            }
                            else
                            {
                                binding.shelfRV.adapter = null
                            }
                        }
                        catch (e: Exception)
                        {
                            Log.i("RACK_OBSERVER","${e.message}")
                            Log.i("RACK_OBSERVER","${e.stackTrace}")
                        }
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

        viewModel.getShelf.observe(this, Observer{
            when(it.status){
                Status.LOADING ->{
                }
                Status.SUCCESS ->{
                    it.let{
                        if (isNetworkConnected(this)){
                            LocalPreferences.put(this, isRefreshRequired, true)
                            try {
                                if(it.data?.get(0)?.status == true) {
                                    showShelfSpinner(it.data)
                                    shelfList = ArrayList()
                                    shelfList = it.data as ArrayList<GetShelfResponse>
                                    shelfAdapter = ShelfAdapter(this, shelfList)

                                    binding.shelfRV.apply {
                                        adapter = shelfAdapter
                                        layoutManager = LinearLayoutManager(this@ShelfActivity)
                                    }
                                }else{
                                    binding.shelfRV.adapter = null
                                }
                            }
                            catch (e:Exception){
                                Log.i("","${e.message}")
                                Log.i("rackAdapter","${e.stackTrace}")
                            }
                        }
                        else
                        {
                            binding.shelfRV.adapter = null
                        }
                    }
                }
                Status.ERROR ->{

                }
            }
        })

    }

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

                if (Utils.isNetworkConnected(this@ShelfActivity))
                {
                    Log.i("LocBus","business Location no ${data[position].orgBusLocNo}")
                    // binding.rackSpinnerCont.visible()
                    selectedBusLocNo = data[position].orgBusLocNo.toString()
                    viewModel.getWarehouse("", selectedBusLocNo)
                    busLocName = data[position].busLocationName.toString()
                }
                else{
                    binding.shelfRV.adapter = null
                    toast(NoInternetFound)
                }

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
                if (Utils.isNetworkConnected(this@ShelfActivity))
                {
                    selectedWareHouseNo = data[position].wHNo.toString()
                    warehouseName = data[position].wHName.toString()
                    viewModel.getRack(
                        Utils.getSimpleTextBody(""),
                        Utils.getSimpleTextBody(selectedWareHouseNo),
                        Utils.getSimpleTextBody(selectedBusLocNo)
                    )
                    Log.i("LocBus","This is warehouse name is ${adapter?.getItemAtPosition(position)}")
                    Log.i("LocBus","This is warehouse pos is ${data[position].wHNo}")
                }
                else
                {
                    binding.shelfRV.adapter = null
                    toast(NoInternetFound)
                }

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
                if (Utils.isNetworkConnected(this@ShelfActivity))
                {
                    selectedRackNo = data[position].rackNo.toString()
                    rackName = data[position].rackName.toString()
                    viewModel.getShelf(
                        Utils.getSimpleTextBody(""),
                        Utils.getSimpleTextBody(selectedRackNo),
                        Utils.getSimpleTextBody(selectedBusLocNo)
                    )

                    Log.i("LocBus","This is rack pos ${adapter?.getItemAtPosition(position)}")
                }
                else
                {
                    binding.shelfRV.adapter = null
                    toast(NoInternetFound)
                }

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
                    if (Utils.isNetworkConnected(this@ShelfActivity))
                    {
                        Log.i("LocBus","This is shelf pos ${adapter?.getItemAtPosition(position)}")
                        selectedShelveNo = data[position].shelfNo.toString()
//                    viewModel.getPallet("",selectedShelveNo,selectedBusLocNo)
                    }
                    else
                    {
                        binding.shelfRV.adapter = null
                        toast(NoInternetFound)
                    }

                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (LocalPreferences.getBoolean(this, isRefreshRequired)){

            viewModel.getShelf(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody(selectedRackNo),
                Utils.getSimpleTextBody(selectedBusLocNo)
            )
        }
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        onBackPressed()
    }

}