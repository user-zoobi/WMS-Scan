package com.example.wms_scan.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.*
import com.example.scanmate.util.Constants
import com.example.scanmate.util.Constants.Toast.NoInternetFound
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.Utils
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityAddUpdateCartonBinding
import com.example.wms_scan.databinding.ActivityMenuBinding

class AddUpdateCarton : AppCompatActivity() {
    private lateinit var binding: ActivityAddUpdateCartonBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private var selectedBusLocNo:String? = ""
    private var selectedWareHouseNo:String?  = ""
    private var selectedRackNo:String?  = ""
    private var selectedShelveNo:String?  = ""
    private var selectedBusLocName:String?  = ""
    private var selectedWHName:String?  = ""
    private var selectedRackName:String?  = ""
    private var selectedShelfName:String?  = ""
    private var selectedPalletName:String?  = ""
    private var selectedPalletNo:String?  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateCartonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initObserver()
    }

    private fun initObserver(){
        viewModel.addCarton.observe(this, Observer {
            if(isNetworkConnected(this)){
                when(it.status){
                    Status.LOADING ->
                    {
                        dialog.show()
                        dialog.setCanceledOnTouchOutside(true);
                    }
                    Status.SUCCESS ->
                    {
                        Log.i("addShelf","${it.data?.error}")
                    }
                    Status.ERROR ->
                    {
                        Log.i("RACK_OBSERVER","${Exception().message}")
                        Log.i("RACK_OBSERVER","${Exception().stackTrace}")
                    }
                }
            }
            else
            {
                toast(NoInternetFound)
            }

        })
    }

    private fun setupUi(){
        dialog = CustomProgressDialog(this)
        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        initListener()

        when{
            intent.extras?.getBoolean("updateCartonKey") == true -> {
                //edit button data
                val busLocName = intent.extras?.getString("updateBusLoc")
                binding.busLocTV.text = busLocName

                val warehouseName = intent.extras?.getString("updateWH")
                binding.warehouseTV.text = warehouseName

                val rackName = intent.extras?.getString("updateRack")
                binding.rackTV.text = rackName

                val shelfName = intent.extras?.getString("updateShelf")
                binding.shelfTV.text = shelfName

                val palletName = intent.extras?.getString("updatePallet")
                binding.palletTV.text = palletName

                val cartonName = intent.extras?.getString("updateCarton")
                binding.cartonNameET.text = cartonName?.toEditable()

                binding.addCartonBtn.gone()
                binding.updateCartonBtn.visible()
                binding.editDetailTV.text = "Update to"

            }

            intent.extras?.getBoolean("AddCartonKey") == true -> {


                selectedBusLocName = intent.extras?.getString("addBusLocName")
                selectedWHName = intent.extras?.getString("addWHName")
                selectedRackName = intent.extras?.getString("addRackName")
                selectedShelfName = intent.extras?.getString("addShelfName")
                selectedPalletName = intent.extras?.getString("addPalletName")

                binding.busLocTV.text = selectedBusLocName
                binding.warehouseTV.text = selectedWHName
                binding.rackTV.text = selectedRackName
                binding.shelfTV.text = selectedShelfName
                binding.palletTV.text = selectedPalletName


            }
        }

    }

    private fun initListener(){
        binding.toolbar.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            clearPreferences(this)
            true
        }
        binding.addCartonBtn.click {
            val cartonName = binding.cartonNameET.text.toString()
            if(isNetworkConnected(this)){
                viewModel.addCarton(
                    Utils.getSimpleTextBody("0"),
                    Utils.getSimpleTextBody("01"),
                    Utils.getSimpleTextBody("TEST"),
                    Utils.getSimpleTextBody("2"),
                    Utils.getSimpleTextBody(cartonName),
                    Utils.getSimpleTextBody("1"),
                    Utils.getSimpleTextBody("4"),
                    Utils.getSimpleTextBody("1"),
                    Utils.getSimpleTextBody("2"),
                    Utils.getSimpleTextBody("test"),
                )
                toast("carton added")
            }
            else{
                toast(NoInternetFound)
            }

        }
        binding.updateCartonBtn.click {
            val cartonName = binding.cartonNameET.text.toString()
            if(isNetworkConnected(this)){
                viewModel.addCarton(
                    Utils.getSimpleTextBody("1"),
                    Utils.getSimpleTextBody("01"),
                    Utils.getSimpleTextBody("TEST"),
                    Utils.getSimpleTextBody("2"),
                    Utils.getSimpleTextBody(cartonName),
                    Utils.getSimpleTextBody("1"),
                    Utils.getSimpleTextBody("4"),
                    Utils.getSimpleTextBody("1"),
                    Utils.getSimpleTextBody("2"),
                    Utils.getSimpleTextBody("test"),
                )
                toast("carton updated")
            }
            else{
                toast(NoInternetFound)
            }
        }
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        finish()
    }



    override fun onBackPressed() {
        finish()
    }

}