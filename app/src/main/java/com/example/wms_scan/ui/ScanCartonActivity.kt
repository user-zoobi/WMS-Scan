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
import com.example.scanmate.util.Constants
import com.example.scanmate.util.Constants.LogMessages.error
import com.example.scanmate.util.Constants.LogMessages.loading
import com.example.scanmate.util.Constants.Toast.NoInternetFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityScanCartonBinding

class ScanCartonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanCartonBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var palletList : ArrayList<GetPalletResponse>
    private lateinit var palletAdapter : PalletsAdapter
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanCartonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        initListeners()
        setupUi()
        initObserver()


    }

    private fun setupUi(){
        dialog = CustomProgressDialog(this)
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        finish()
    }

    private fun initListeners()
    {

        binding.scanBtn.click {

            binding.scanBtn.gone()
            binding.scanCartonTV.gone()
            binding.cartonDetails.root.visible()


        }
        binding.cartonDetails.closeIV.click {
            binding.cartonDetails.root.gone()
            binding.scanBtn.visible()
            binding.scanCartonTV.visible()
        }
        binding.qrGenerateIV.click {
            gotoActivity(QrCodeGeneratorActivity::class.java)
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
                    it.let {
                        if(it.data?.get(0)?.status == true) {
                            dialog.dismiss()
                            showBusLocSpinner(it.data!!)
                        }
                        else
                        {
                            toast("no result found")
                        }
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
                            showWarehouseSpinner(it.data)
                        }
                        else
                        {
                            toast("no result found")
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
                        if(it.data?.get(0)?.status == true)
                        {
                            showRackSpinner(it.data!!)
                        }
                        else
                        {
                            toast("no result found")
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

        viewModel.getShelf.observe(this, Observer{
            when(it.status){
                Status.LOADING ->{
                }
                Status.SUCCESS ->{
                    try {
                        if(it.data?.get(0)?.status == true)
                        {
                            showShelfSpinner(it.data!!)
                        }
                        else
                        {
                            toast("no result found")
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
                Status.LOADING ->{
                    Log.i(Constants.LogMessages.loading,"Success")
                }
                Status.SUCCESS ->{
                    try
                    {
                        if(it.data?.get(0)?.status == true)
                        {
                            showPalletSpinner(it.data)
                        }
                        else
                        {
                            toast("no result found")
                        }
                    }
                    catch (e:Exception)
                    {
                        Log.i("","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }

                }
                Status.ERROR ->{
                    Log.i(Constants.LogMessages.error,"Success")
                }
            }
        })

        //GET CARTON
        viewModel.getCarton(
            Utils.getSimpleTextBody(selectedPalletNo),
            Utils.getSimpleTextBody(selectedBusLocNo)
        )
        viewModel.getCarton.observe(this, Observer {
            when(it.status){

                Status.LOADING ->{
                    Log.i(loading,"Loading")
                }
                Status.SUCCESS ->{
                    try
                    {

                    }
                    catch (e:Exception){
                        Log.i("","${e.message}")
                        Log.i("cartonException","${e.stackTrace}")
                    }

                }
                Status.ERROR ->{
                    Log.i(error,"Error")
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
                Log.i("LocBus","business Location no ${data[position].orgBusLocNo}")
                // binding.rackSpinnerCont.visible()
                if (Utils.isNetworkConnected(this@ScanCartonActivity))
                {
                    selectedBusLocNo = data[position].orgBusLocNo.toString()
                    LocalPreferences.put(
                        this@ScanCartonActivity,
                        LocalPreferences.AppLoginPreferences.busLocNo, selectedBusLocNo
                    )
                    busLocName = data[position].busLocationName.toString()
                    viewModel.getWarehouse("", selectedBusLocNo)
                }else
                {
                    businessLocSpinner.adapter = null
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
                if (Utils.isNetworkConnected(this@ScanCartonActivity))
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
                    warehouseSpinner.adapter = null
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
                if (Utils.isNetworkConnected(this@ScanCartonActivity))
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
                    rackSpinner.adapter = null
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
                    if (Utils.isNetworkConnected(this@ScanCartonActivity))
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
                        shelfResponse.adapter = null
                    }

                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun showPalletSpinner(data:List<GetPalletResponse>) {
        //String array to store all the book names
        val items = arrayOfNulls<String>(data.size)
        val shelfResponse = binding.palletSpinner

        //Traversing through the whole list to get all the names
        for (i in data.indices) {
            //Storing names to string array
            items[i] = data[i].pilotName
            val adapter: ArrayAdapter<String?> =
                ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
            //setting adapter to spinner
            shelfResponse.adapter = adapter
            shelfResponse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

                override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, long: Long) {
                    if (Utils.isNetworkConnected(this@ScanCartonActivity))
                    {
                        Log.i("LocBus","This is shelf pos ${adapter?.getItemAtPosition(position)}")
                        selectedPalletNo = data[position].pilotNo.toString()
                        selectedPalletName = data[position].pilotName.toString()
                        LocalPreferences.put(
                            this@ScanCartonActivity,
                            LocalPreferences.AppLoginPreferences.palletNo, selectedPalletNo
                        )
                    }
                    else
                    {
                        toast(NoInternetFound)
                    }

                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }




    override fun onBackPressed() {
        finish()
    }

}