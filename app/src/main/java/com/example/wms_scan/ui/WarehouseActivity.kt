package com.example.wms_scan.ui

import android.R
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
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.adapter.racks.RackAdapter
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.adapter.warehouse.WarehouseAdapter
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityRacksBinding
import com.example.wms_scan.databinding.ActivityWarehouseBinding

class WarehouseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWarehouseBinding
    private lateinit var warehouseAdapter: WarehouseAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var list: ArrayList<GetWarehouseResponse>
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var businessLocName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWarehouseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initObserver()
        initListeners()
    }

    private fun setupUi(){

        dialog = CustomProgressDialog(this)
        supportActionBar?.hide()
        setTransparentStatusBarColor(com.example.wms_scan.R.color.transparent)
        list = ArrayList()
        warehouseAdapter = WarehouseAdapter(this, list)


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
                        toast("No result found")
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

                    try {
                        if(it.data?.get(0)?.status == true)
                        {
                            it.data[0].wHName?.let { it1 -> Log.i("warehouseResponse", it1) }

                            list = ArrayList()
                            list = it.data as ArrayList<GetWarehouseResponse>
                            warehouseAdapter = WarehouseAdapter(this, list)
                            binding.warehouseRV.apply {
                                layoutManager = LinearLayoutManager(this@WarehouseActivity)
                                adapter = warehouseAdapter
                            }
                        }
                        else
                        {
                            binding.warehouseRV.adapter = null
                            toast("No result found")
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
    }

    private fun initListeners(){

        binding.toolbar.menu.findItem(com.example.wms_scan.R.id.logout).setOnMenuItemClickListener {
            clearPreferences(this)
            true
        }

        binding.refresh.click {
            if (Utils.isNetworkConnected(this@WarehouseActivity))
            {
                viewModel.getWarehouse("", selectedBusLocNo)
            }
        }

        binding.whAddBTN.click{
            val intent = Intent(this, WarehouseDetailsActivity::class.java)

            intent.putExtra("addBusName",businessLocName)
            intent.putExtra("addBusLocNo",selectedBusLocNo)
            intent.putExtra("AddWHKey",true)
            startActivity(intent)
        }
    }

    fun performAction(whName: String?, whNo: String)
    {
        toast("Values from adapter: v1: $whName, v2: $whNo")
        val intent = Intent(this, WarehouseDetailsActivity::class.java)
        intent.putExtra("updateBusName",businessLocName)
        intent.putExtra("updateBusLocNo",selectedBusLocNo)
        intent.putExtra("updateWhName",whName)
        intent.putExtra("updateWhNo",whNo)
        intent.putExtra("UpdateWHKey",true)
        startActivity(intent)
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
                if (Utils.isNetworkConnected(this@WarehouseActivity))
                {
                    Log.i("LocBus","business Location no ${data[position].orgBusLocNo}")
                    // binding.rackSpinnerCont.visible()
                    selectedBusLocNo = data[position].orgBusLocNo.toString()
                    viewModel.getWarehouse("", selectedBusLocNo)
                    businessLocName = data[position].busLocationName.toString()
                }
                else{
                    businessLocSpinner.adapter = null
                }

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


}