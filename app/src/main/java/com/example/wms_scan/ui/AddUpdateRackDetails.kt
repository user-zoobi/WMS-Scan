package com.example.wms_scan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.scanmate.data.callback.Status
import com.example.scanmate.data.response.GetRackResponse
import com.example.scanmate.data.response.GetWarehouseResponse
import com.example.scanmate.data.response.UserLocationResponse
import com.example.scanmate.extensions.*
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.isRefreshRequired
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.userNo
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityAddUpdateRackDetailsBinding
import java.util.*

class AddUpdateRackDetails : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog
    private lateinit var binding:ActivityAddUpdateRackDetailsBinding
    private var selectedBusLocNo = ""
    private var selectedBusLocName = ""
    private var selectedWareHouseNo = ""
    private var selectedWHName = ""
    private var selectedRackNo = ""
    private var selectedRackName = ""
    private var updatedBusLocNo:String? = ""
    private var updatedBusLocName:String? = ""
    private var updatedWHName:String? = ""
    private var updatedWHNo:String? = ""
    private var updatedRackNo:String? = ""
    private var updatedRackName:String? = ""
    private var rackName = ""
    private var rackCode = ""
    private var shelfCap = ""
    private var deviceId = ""
    private var updatedRackCode = ""
    private var updatedShelfCap = ""
    private var addShelfCap = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateRackDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setupUi()
        initListeners()
        initObserver()
    }

    private fun initListeners(){

        binding.addRackBtn.click {

                rackName = binding.rackNameET.text.toString()
                shelfCap = binding.shelfCapacityET.text.toString()
                if(binding.shelfCapacityET.text.toString() == "0")
                {
                    toast("Please enter greater capacity")
                }
                else
                {
                    viewModel.addRack(
                        Utils.getSimpleTextBody("0"),
                        Utils.getSimpleTextBody(rackName),
                        Utils.getSimpleTextBody("0"),
                        Utils.getSimpleTextBody(selectedWareHouseNo),
                        Utils.getSimpleTextBody(shelfCap),
                        Utils.getSimpleTextBody(selectedBusLocNo),
                        Utils.getSimpleTextBody(
                            LocalPreferences.getInt(this,userNo).toString()),
                        Utils.getSimpleTextBody(deviceId),
                    )
                }

                LocalPreferences.getBoolean(this, isRefreshRequired)
                Log.i("addRack","1. 0 \n 2.$rackName\n 3.\n 4.$selectedWareHouseNo\n 5.$shelfCap\n 6.$selectedBusLocNo\n 7.2\n 8.$deviceId" )
            }

        binding.updateRackBtn.click {

                updatedRackName = binding.rackNameET.text.toString()
                shelfCap = binding.shelfCapacityET.text.toString()
                if(binding.shelfCapacityET.text.toString() == "0")
                {
                    toast("Please enter greater capacity")
                }
                else
                {
                    viewModel.addRack(
                        Utils.getSimpleTextBody("$updatedRackNo"),
                        Utils.getSimpleTextBody("$updatedRackName"),
                        Utils.getSimpleTextBody("0"),
                        Utils.getSimpleTextBody("$updatedWHNo"),
                        Utils.getSimpleTextBody(shelfCap),
                        Utils.getSimpleTextBody("$updatedBusLocNo"),
                        Utils.getSimpleTextBody("${LocalPreferences.getInt(this, userNo)}"),
                        Utils.getSimpleTextBody(deviceId),

                        )
                }

                Log.i("updateRack","1. $updatedRackNo \n 2.$updatedRackName\n 3.\n 4.$updatedWHNo\n 5.$shelfCap\n 6.$updatedBusLocNo\n 7.2\n 8.$deviceId" )
                finish()
            }

        binding.backBtn.click {
            onBackPressed()
        }
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
            intent.extras?.getBoolean("updateRackKey") == true -> {
                updatedBusLocNo = intent.extras?.getString("updateBusNo")
                updatedBusLocName = intent.extras?.getString("updateBusName")
                updatedWHNo = intent.extras?.getString("updateWHNo")
                updatedWHName = intent.extras?.getString("updateWhName")
                updatedRackName = intent.extras?.getString("updateRackName")
                updatedRackNo = intent.extras?.getString("updateRackNo")
                updatedRackCode = intent.extras?.getString("updateRackCode").toString()
                updatedShelfCap = intent.extras?.getString("updateShelfCapacity").toString()

                binding.warehouseTV.text = updatedWHName
                binding.businessLocTV.text = updatedBusLocName
                binding.rackNameET.text = updatedRackName?.toEditable()
                binding.shelfCapacityET.text = updatedShelfCap.toEditable()
                binding.rackNameET.hint = "Update rack"
                binding.editDetailTV.text = "Update to"
                binding.addRackBtn.gone()
                binding.updateRackBtn.visible()
            }

            intent.extras?.getBoolean("AddRackKey") == true -> {
                selectedBusLocNo = intent.extras?.getString("addBusNo")!!
                selectedBusLocName = intent.extras?.getString("addBusName")!!
                selectedWareHouseNo = intent.extras?.getString("addWHNo")!!
                selectedWHName = intent.extras?.getString("addWHName")!!
                rackCode = intent.extras?.getString("addRackCode")!!
                binding.businessLocTV.text = selectedBusLocName
                binding.warehouseTV.text = selectedWHName
            }
        }

    }

    private fun initObserver(){

        viewModel.addRack.observe(this, Observer {
            it.let {
                when(it.status){
                    Status.LOADING ->{
                        dialog.show()
                        dialog.setCanceledOnTouchOutside(true)
                    }
                    Status.SUCCESS ->{

                        if (it.data?.status == true)
                        {
                            dialog.dismiss()
                            LocalPreferences.put(this,isRefreshRequired, true)
                            finish()
                            toast(it.data.error!!)
                        }
                        else
                        {
                            toast(it.data?.error!!)
                        }


                    }
                    Status.ERROR ->{
                        dialog.dismiss()
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        finish()
    }
}