package com.example.wms_scan.ui

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
import com.example.wms_scan.R
import com.example.wms_scan.adapter.shelf.ShelfAdapter
import com.example.wms_scan.databinding.ActivityAddUpdateShelfDetailsBinding
import java.util.*
import java.util.regex.Pattern

class AddUpdateShelfDetails : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var binding: ActivityAddUpdateShelfDetailsBinding
    private var selectedBusLocNo:String? = ""
    private var selectedWareHouseNo:String? = ""
    private var selectedRackNo:String? = ""
    private var selectedShelveNo:String? = ""
    private var selectedBusLocName:String? = ""
    private var selectedWareHouseName:String? = ""
    private var selectedRackName:String? = ""
    private var selectedShelveName:String? = ""
    private var updatedBusLocNo:String? = ""
    private var updatedBusName:String? = ""
    private var updatedWHNo:String? = ""
    private var updatedWhName:String? = ""
    private var updatedRackName:String? = ""
    private var updatedRackNo:String? = ""
    private var updatedShelfNo:String? = ""
    private var updatedShelfName:String? = ""
    private var shelfNameInput = ""
    private var palletCapacity = ""
    private var updatedShelveCode = ""
    private var updatedPalletCapacity = ""
    private var shelveCode = ""
    private var deviceId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateShelfDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListeners()
        initObserver()
    }

    private fun setupUi(){

        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        dialog = CustomProgressDialog(this)
        deviceId = UUID.randomUUID().toString()

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
            intent.extras?.getBoolean("AddShelfKey") == true -> {

                selectedBusLocName = intent.extras?.getString("addBusLocName")
                selectedBusLocNo = intent.extras?.getString("addBusLocNo")
                selectedWareHouseName = intent.extras?.getString("addWHName")
                selectedWareHouseNo = intent.extras?.getString("addWHNo")
                selectedRackName = intent.extras?.getString("addRackName")
                selectedRackNo = intent.extras?.getString("addRackNo")
                selectedShelveName = intent.extras?.getString("addShelfName")
                selectedShelveNo = intent.extras?.getString("addShelfNo")
                shelveCode = intent.extras?.getString("shelfCode").toString()

                binding.busLocTV.text = selectedBusLocName
                binding.warehouseTV.text = selectedWareHouseName
                binding.rackTV.text = selectedRackName

                Log.i("warehouseValues","THE KEYS VALUES ARE :" +
                        "$selectedBusLocName\n $selectedWareHouseName\n $selectedRackName \n $selectedShelveName")


            }

            intent.extras?.getBoolean("UpdateShelfKey") == true -> {
                //add button data

                updatedBusName = intent.extras?.getString("updateBusinessLocName")
                updatedBusLocNo = intent.extras?.getString("updateBusLocNo")
                updatedWhName = intent.extras?.getString("updateWHName")
                updatedWHNo = intent.extras?.getString("updateWHNo")
                updatedRackName = intent.extras?.getString("updateRackName")
                updatedRackNo = intent.extras?.getString("updateRackNo")
                updatedShelfName = intent.extras?.getString("updateShelfName")
                updatedShelfNo = intent.extras?.getString("updateShelfNo")
                updatedShelveCode = intent.extras?.getString("updatedShelfCode").toString()
                updatedPalletCapacity = intent.extras?.getString("updatedPalletCapacity").toString()

                binding.busLocTV.text = updatedBusName
                binding.warehouseTV.text = updatedWhName
                binding.rackTV.text = updatedRackName
                binding.editDetailTV.text = "Update to"
                binding.shelfNameET.text = updatedShelfName?.toEditable()
                binding.palletCapacityET.text = updatedPalletCapacity.toEditable()
                binding.addShelfBtn.gone()
                binding.updateShelfBtn.visible()
                binding.shelfNameET.hint= "Update Shelf"

                Log.i("warehouseValues","THE KEYS VALUES ARE :$updatedBusName\n $updatedWhName\n $updatedRackName \n $updatedShelfName")

            }
        }

    }

    private fun initListeners(){

        binding.addShelfBtn.click {

            shelfNameInput = binding.shelfNameET.text.toString()
            palletCapacity = binding.palletCapacityET.text.toString()

            if(binding.palletCapacityET.text.toString() == "0")
            {
                toast("Please enter greater capacity")
            }
            else if (binding.palletCapacityET.text.isNullOrEmpty())
            {
                toast("Field must not be empty")
            }
            else if (binding.shelfNameET.text.isNullOrEmpty())
            {
                toast("Field must not be empty")
            }

            else
            {
                viewModel.addShelf(
                    Utils.getSimpleTextBody("0"),
                    Utils.getSimpleTextBody("$selectedRackNo"),
                    Utils.getSimpleTextBody(shelfNameInput),
                    Utils.getSimpleTextBody("0"),
                    Utils.getSimpleTextBody(palletCapacity),
                    Utils.getSimpleTextBody("$selectedBusLocNo"),
                    Utils.getSimpleTextBody(
                        LocalPreferences.getInt(this, LocalPreferences.AppLoginPreferences.userNo).toString()
                    ),
                    Utils.getSimpleTextBody(deviceId),
                )
            }

            binding.backBtn.click {
                onBackPressed()
            }


            Log.i("addshelf","1. 0 \n 2.$selectedRackNo\n 3.$shelfNameInput\n 4.$shelveCode\n 5.$palletCapacity\n 6.$selectedBusLocNo\n 7.2\n 8.$deviceId" )
            Log.i("shelfValues","THE KEYS ARE VALUES WITH selectedBusLocNo :$selectedBusLocNo\n $selectedBusLocName\n")
        }

        binding.updateShelfBtn.click {
            shelfNameInput = binding.shelfNameET.text.toString()
            palletCapacity = binding.palletCapacityET.text.toString()
            val regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]")

            if(binding.palletCapacityET.text.toString() == "0")
            {
                toast("Please enter greater capacity")
                binding.palletCapacityET.error = "Please enter greater capacity"
            }
            else if (binding.palletCapacityET.text.isNullOrEmpty())
            {
                toast("Field must not be empty")
                binding.palletCapacityET.error = "Field must not be empty"
            }
            else if (regex.matcher(binding.shelfNameET.toString()).find())
            {
                binding.shelfNameET.error = "Special characters are not allowed"
            }
            else if (regex.matcher(binding.palletCapacityET.toString()).find())
            {
                binding.palletCapacityET.error = "Special characters are not allowed"
            }
            else if(binding.shelfNameET.text.isNullOrEmpty())
            {
                toast("Field must not be empty")
                binding.shelfNameET.error = "Field must not be empty"
            }
            else
            {
                viewModel.addShelf(
                    Utils.getSimpleTextBody("$updatedShelfNo"),
                    Utils.getSimpleTextBody("$updatedRackNo"),
                    Utils.getSimpleTextBody(shelfNameInput),
                    Utils.getSimpleTextBody(updatedShelveCode),
                    Utils.getSimpleTextBody(palletCapacity),
                    Utils.getSimpleTextBody("$updatedBusLocNo"),
                    Utils.getSimpleTextBody(
                        LocalPreferences.getInt(this, LocalPreferences.AppLoginPreferences.userNo).toString()
                    ),
                    Utils.getSimpleTextBody(deviceId)
                )
                finish()
            }
        }
    }

    private fun initObserver(){

        /**
         *       ADD SHELF OBSERVER
         */

        viewModel.addShelf.observe(this, Observer
        {
            when(it.status){
                Status.LOADING ->{
                    dialog.show()
                    dialog.setCanceledOnTouchOutside(true)
                }
                Status.SUCCESS ->
                {

                    if (it.data?.status == true)
                    {
                        dialog.dismiss()
                        LocalPreferences.put(this,
                            LocalPreferences.AppLoginPreferences.isRefreshRequired, true)
                        finish()
                        toast(it.data.error!!)
                    }
                    else
                    {
                        toast(it.data?.error!!)
                    }
                }
                Status.ERROR ->
                {
                    dialog.dismiss()
                }
            }
        })
    }

    override fun onBackPressed() {
        finish()
    }

}