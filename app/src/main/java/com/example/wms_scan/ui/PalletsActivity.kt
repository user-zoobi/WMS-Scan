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
import com.example.scanmate.util.Constants.LogMessages.error
import com.example.scanmate.util.Constants.LogMessages.loading
import com.example.scanmate.util.Constants.LogMessages.success
import com.example.scanmate.util.Constants.Toast.NoInternetFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.isRefreshRequired
import com.example.scanmate.util.Utils
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityPalletsBinding

class PalletsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPalletsBinding
    private lateinit var palletAdapter: PalletsAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var palletList: ArrayList<GetPalletResponse>
    private var selectedBusLocNo = ""
    private var selectedWareHouseNo = ""
    private var selectedRackNo = ""
    private var selectedShelveNo = ""
    private var selectedPalletNo = ""
    private var selectedPalletName = ""
    private var busLocName = ""
    private var warehouseName = ""
    private var rackName = ""
    private var shelfName = ""
    private lateinit var bottomSheet: QrCodeDetailActivity

    private var palletNo = ""
    private var palletName = ""
    private var palletCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPalletsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListeners()
        initObservers()

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

        binding.swipeRefresh.setOnRefreshListener {
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

                viewModel.getShelf(
                    Utils.getSimpleTextBody(""),
                    Utils.getSimpleTextBody(selectedRackNo),
                    Utils.getSimpleTextBody(selectedBusLocNo)
                )
            }
            else
            {
                binding.swipeRefresh.isRefreshing = false
            }
        }

        binding.palletAddBTN.click {
            if (isNetworkConnected(this)){
                val intent = Intent(this, AddUpdatePalletDetails::class.java)
                intent.putExtra("addBusLocNo",selectedBusLocNo)
                intent.putExtra("addWHNo",selectedWareHouseNo)
                intent.putExtra("addRackNo",selectedRackNo)
                intent.putExtra("addShelfNo",selectedShelveNo)
                intent.putExtra("addBusLocName",busLocName)
                intent.putExtra("addWHName",warehouseName)
                intent.putExtra("addRackName",rackName)
                intent.putExtra("addShelfName",shelfName)
                intent.putExtra("AddPalletKey",true)
                startActivity(intent)
            }

        }

    }

    private fun initObservers(){

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
                        it.let {
                            if(it.data?.get(0)?.status == true) {
                                dialog.dismiss()
                                showBusLocSpinner(it.data!!)
                            }
                            else
                            {
                            }
                        }
                    }
                    else
                    {
                        binding.palletsRV.adapter = null
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

                                binding.palletsRV.adapter = null
                            }
                        }
                        catch(e:Exception){
                            Log.i("rackAdapter","${e.message}")
                            Log.i("rackAdapter","${e.stackTrace}")
                        }
                        //warehouseAdapter.addItems(list)
                    }
                    else
                    {
                        binding.palletsRV.adapter = null
                    }
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
                                showRackSpinner(it.data!!)
                            }
                            else
                            {
                                binding.palletsRV.adapter = null
                            }
                        }
                        catch (e: Exception)
                        {
                            Log.i("RACK_OBSERVER","${e.message}")
                            Log.i("RACK_OBSERVER","${e.stackTrace}")
                        }
                    }
                    else
                    {
                        binding.palletsRV.adapter = null
                    }
                    // Log.i("getRack",it.data?.get(0)?.rackNo.toString())

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
                    if (isNetworkConnected(this)){
                        try {
                            if(it.data?.get(0)?.status == true)
                            {
                                showShelfSpinner(it.data!!)
                            }
                            else
                            {
                                binding.palletsRV.adapter = null
                            }
                        }
                        catch (e:Exception){
                            Log.i("","${e.message}")
                            Log.i("rackAdapter","${e.stackTrace}")
                        }
                    }
                    else
                    {
                        binding.palletsRV.adapter = null
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
                    binding.swipeRefresh.isRefreshing = true
                    Log.i(loading,"Loading")
                }
                Status.SUCCESS ->{
                    binding.swipeRefresh.isRefreshing = false
                    try
                    {
                        LocalPreferences.put(this,isRefreshRequired, true)
                        if(it.data?.get(0)?.status == true)
                        {

                            palletName = it.data[0].pilotName.toString()
                            palletCode = it.data[0].pilotCode.toString()
                            palletNo = it.data[0].pilotNo.toString()

                            Log.i(success,"Success")
                            palletList = ArrayList()
                            palletList = it.data as ArrayList<GetPalletResponse>
                            palletAdapter = PalletsAdapter(this,palletList)

                            binding.palletsRV.apply {
                                adapter = palletAdapter
                                layoutManager = LinearLayoutManager(this@PalletsActivity)
                            }
                        }
                        else
                        {
                            binding.palletsRV.adapter = null
                        }
                    }
                    catch (e:Exception)
                    {
                        Log.i("","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }

                }
                Status.ERROR ->{
                    Log.i(error,"Success")
                }
            }
        })

    }

    fun showAction(palletName:String,palletNo:String){

        val intent = Intent(this, AddUpdatePalletDetails::class.java)
        intent.putExtra("updatedBusLocNo",selectedBusLocNo)
        intent.putExtra("updatedWHNo",selectedWareHouseNo)
        intent.putExtra("updatedRackNo",selectedRackNo)
        intent.putExtra("updatedShelveNo",selectedShelveNo)
        intent.putExtra("updatedBusLocName",busLocName)
        intent.putExtra("updatedWHName",warehouseName)
        intent.putExtra("updatedRackName",rackName)
        intent.putExtra("updatedShelveName",shelfName)
        intent.putExtra("updatedPalletName",palletName)
        intent.putExtra("updatedPalletNo",palletNo)
        intent.putExtra("UpdatePalletKey",true)
        startActivity(intent)

    }

    fun showQrCode(palletCode:String){
        val intent = Intent(this, QrCodeDetailActivity::class.java)
        intent.putExtra("palletKey",true)
        intent.putExtra("palletQrCode",palletCode)
        intent.putExtra("palletQrNo",palletNo)
        intent.putExtra("palletQrName",palletName)
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
                Log.i("LocBus","business Location no ${data[position].orgBusLocNo}")
                // binding.rackSpinnerCont.visible()
                if (isNetworkConnected(this@PalletsActivity))
                {
                    selectedBusLocNo = data[position].orgBusLocNo.toString()
                    busLocName = data[position].busLocationName.toString()
                    viewModel.getWarehouse("", selectedBusLocNo)
                }else
                {
                    binding.palletsRV.adapter = null
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
                if (isNetworkConnected(this@PalletsActivity))
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
                }else
                {
                    binding.palletsRV.adapter = null
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
                if (isNetworkConnected(this@PalletsActivity))
                {
                    selectedRackNo = data[position].rackNo.toString()
                    rackName = data[position].rackName.toString()
                    viewModel.getShelf(
                        Utils.getSimpleTextBody(""),
                        Utils.getSimpleTextBody(selectedRackNo),
                        Utils.getSimpleTextBody(selectedBusLocNo)
                    )
                }
                else
                {
                    binding.palletsRV.adapter = null
                    toast(NoInternetFound)
                }


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
                    if (isNetworkConnected(this@PalletsActivity))
                    {
                        Log.i("LocBus","This is shelf pos ${adapter?.getItemAtPosition(position)}")
                        selectedShelveNo = data[position].shelfNo.toString()
                        shelfName = data[position].shelfName.toString()
                        viewModel.getPallet(
                            Utils.getSimpleTextBody(""),
                            Utils.getSimpleTextBody(selectedShelveNo),
                            Utils.getSimpleTextBody(selectedBusLocNo)
                        )
                    }
                    else
                    {
                        binding.palletsRV.adapter = null
                        toast(NoInternetFound)
                    }

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
        gotoActivity(LoginActivity::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (LocalPreferences.getBoolean(this, isRefreshRequired)){

            viewModel.getPallet(
                Utils.getSimpleTextBody(""),
                Utils.getSimpleTextBody(selectedShelveNo),
                Utils.getSimpleTextBody(selectedBusLocNo)
            )

        }
    }

}