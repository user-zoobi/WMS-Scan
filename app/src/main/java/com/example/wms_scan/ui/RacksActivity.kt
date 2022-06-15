package com.example.wms_scan.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scanmate.data.callback.Status
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.data.response.GetWarehouseResponse
import com.example.scanmate.data.response.UserLocationResponse
import com.example.scanmate.extensions.click
import com.example.scanmate.extensions.obtainViewModel
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.extensions.toast
import com.example.scanmate.util.Constants.Toast.NoInternetFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.isRefreshRequired
import com.example.scanmate.util.Utils
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.racks.RackAdapter
import com.example.wms_scan.databinding.ActivityRacksBinding

class RacksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRacksBinding
    private lateinit var racksAdapter: RackAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var rackList: ArrayList<GetRackResponse>
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var selectedRackNo = ""
    private var selectedRackName = ""
    private var businessLocName = ""
    private var warehouseName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRacksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListeners()
        initObserver()


    }

    private fun setupUi(){

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

    private fun initListeners(){

        binding.toolbar.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            clearPreferences(this)
            true
        }
        binding.rackAddBTN.click {
            if (isNetworkConnected(this))
            {
                val intent = Intent(this, AddUpdateRackDetails::class.java)
                intent.putExtra("addBusNo",selectedBusLocNo)
                intent.putExtra("addBusName",businessLocName)
                intent.putExtra("addWHNo",selectedWareHouseNo)
                intent.putExtra("addWHName",warehouseName)
                intent.putExtra("addRackNo",selectedRackNo)
                intent.putExtra("addRackName",selectedRackName)
                intent.putExtra("AddRackKey",true)
                startActivity(intent)
            }
            else
            {
                toast(NoInternetFound)
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            if (isNetworkConnected(this))
            {
                viewModel.userLocation(
                    Utils.getSimpleTextBody(
                        LocalPreferences.getInt(this, LocalPreferences.AppLoginPreferences.userNo).toString()
                    )
                )
                viewModel.getWarehouse("", selectedBusLocNo)
            }
        }

    }

    fun openActivity(rackName: String?, rackNo: String){

        if (isNetworkConnected(this))
        {
            Log.i("Warehouse","$rackName $rackNo")
            val intent = Intent(this, AddUpdateRackDetails::class.java)
            intent.putExtra("updateBusNo",selectedBusLocNo)
            intent.putExtra("updateBusName",businessLocName)
            intent.putExtra("updateWHNo",selectedWareHouseNo)
            intent.putExtra("updateWhName",warehouseName)
            intent.putExtra("updateRackName",rackName)
            intent.putExtra("updateRackNo",rackNo)
            intent.putExtra("updateRackKey",true)
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
            it.let {
                if(isNetworkConnected(this))
                {
                    when(it.status){
                        Status.LOADING->{
                            dialog.show()
                            dialog.setCanceledOnTouchOutside(true);
                        }
                        Status.SUCCESS ->{
                            if(it.data?.get(0)?.status == true)
                            {
                                dialog.dismiss()
                                showBusLocSpinner(it.data)
                            }
                            else
                            {

                                binding.racksRV.adapter = null
                            }
                        }
                        Status.ERROR ->{
                            dialog.dismiss()
                        }
                    }
                }
                else
                {

                }

            }
        })

        /**
         *      GET WAREHOUSE OBSERVER
         */

        viewModel.getWarehouse.observe(this, Observer{
            it.let {
                if(isNetworkConnected(this))
                {
                    when(it.status){
                        Status.LOADING->{
                        }
                        Status.SUCCESS ->{

                            try {
                                if(it.data?.get(0)?.status == true)
                                {
                                    it.data[0].wHName?.let { it1 -> Log.i("warehouseResponse", it1) }
                                    showWarehouseSpinner(it.data)
                                }
                                else
                                {
                                    toast("No record found")
                                    binding.racksRV.adapter = null
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
                }

            }
        })

        /**
         *      GET WAREHOUSE OBSERVER
         */

        viewModel.getWarehouse.observe(this, Observer{
            it.let {
                if(isNetworkConnected(this))
                {
                    when(it.status){
                        Status.LOADING->{
                        }
                        Status.SUCCESS ->{
                            binding.swipeRefresh.isRefreshing = false

                            try {
                                LocalPreferences.put(this, isRefreshRequired, false)

                                if(it.data?.get(0)?.status == true)
                                {
                                    it.data[0].wHName?.let { it1 -> Log.i("warehouseResponse", it1) }
                                    showWarehouseSpinner(it.data)
                                }
                                else
                                {
                                    binding.racksRV.adapter = null
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
                }

            }
        })

        /**
         *      GET RACK OBSERVER
         */

        viewModel.getRack.observe(this, Observer{
            it.let {
                if(isNetworkConnected(this))
                {
                    when(it.status){
                        Status.LOADING ->{
                        }
                        Status.SUCCESS ->{
                            // Log.i("getRack",it.data?.get(0)?.rackNo.toString())
                            try
                            {
                                if(it.data?.get(0)?.status == true)
                                {
                                    showRackSpinner(it.data)
                                    rackList = ArrayList()
                                    rackList = it.data as ArrayList<GetRackResponse>
                                    racksAdapter = RackAdapter(this,rackList)
                                    binding.racksRV.apply {

                                        layoutManager = LinearLayoutManager(this@RacksActivity)
                                        adapter = racksAdapter
                                    }
                                }else{
                                    binding.racksRV.adapter = null
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

                if (isNetworkConnected(this@RacksActivity)){
                    Log.i("LocBus","business Location no ${data[position].orgBusLocNo}")
                    // binding.rackSpinnerCont.visible()
                    businessLocName = data[position].busLocationName.toString()
                    selectedBusLocNo = data[position].orgBusLocNo.toString()
                    viewModel.getWarehouse("", selectedBusLocNo)
                }
                else
                {
                    binding.racksRV.adapter = null

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

                if (Utils.isNetworkConnected(this@RacksActivity)) {
                    selectedWareHouseNo = data[position].wHNo.toString()
                    viewModel.getRack(
                        Utils.getSimpleTextBody(""),
                        Utils.getSimpleTextBody(selectedWareHouseNo),
                        Utils.getSimpleTextBody(selectedBusLocNo)
                    )
                    warehouseName = data[position].wHName.toString()
                    Log.i("LocBus","This is warehouse name is ${adapter?.getItemAtPosition(position)}")
                    Log.i("LocBus","This is warehouse pos is ${data[position].wHNo}")
                }
                else
                {
                    binding.racksRV.adapter = null
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
                if (Utils.isNetworkConnected(this@RacksActivity)) {
                    selectedRackNo = data[position].rackNo.toString()
                    selectedRackName = data[position].rackName.toString()
                    viewModel.getShelf(
                        Utils.getSimpleTextBody(""),
                        Utils.getSimpleTextBody(selectedRackNo),
                        Utils.getSimpleTextBody(selectedBusLocNo)
                    )
                }
                else
                {
                    binding.racksRV.adapter = null
                    toast(NoInternetFound)
                }

                Log.i("LocBus","This is rack pos ${adapter?.getItemAtPosition(position)}")
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    override fun onResume() {
        super.onResume()

        if (LocalPreferences.getBoolean(this, isRefreshRequired))
        {
            viewModel.getRack(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody(selectedWareHouseNo),
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