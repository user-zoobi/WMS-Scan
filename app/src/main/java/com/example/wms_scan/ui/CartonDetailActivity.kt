package com.example.wms_scan.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.scanmate.data.callback.Status
import com.example.scanmate.extensions.*
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.pallets
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.rack
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.shelf
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.warehouse
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityCartonDetailBinding
import com.example.wms_scan.utils.PermissionDialog
import java.lang.Exception


class CartonDetailActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCartonDetailBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var permissionDialog: PermissionDialog
    private var analyticalNo = ""
    private var totCarton = ""
    private var pilotNo = ""
    private var cartonSNo = ""
    private var cartonCode = ""
    private var cartonNo = ""
    private var itemCode = ""
    private var stock = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartonDetailBinding.inflate(layoutInflater)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setContentView(binding.root)
        permissionDialog = PermissionDialog(this)
        initListener()
        initObserver()
        setupUi()

    }

    private fun initListener(){

        binding.saveBtn.click {
            with(viewModel) {
                this.addCarton(
                    CartonNo = Utils.getSimpleTextBody("0"),
                    CartonCode = Utils.getSimpleTextBody(cartonCode),
                    ItemCode = Utils.getSimpleTextBody(itemCode), //
                    PilotNo = Utils.getSimpleTextBody(pilotNo),
                    AnalyticalNo = Utils.getSimpleTextBody(analyticalNo), //
                    Carton_SNo = Utils.getSimpleTextBody(cartonSNo), //
                    TotCarton = Utils.getSimpleTextBody(totCarton), //
                    LocationNo = Utils.getSimpleTextBody("1"),
                    DMLUserNo = Utils.getSimpleTextBody("2"),
                    DMLPCName = Utils.getSimpleTextBody("test")
                )

                Log.i("Intent","$cartonCode $cartonNo $cartonSNo $itemCode $pilotNo $analyticalNo $cartonSNo $totCarton")
            }
        }

        binding.updateBtn.click {
            viewModel.addCarton(
                Utils.getSimpleTextBody(cartonNo),
                Utils.getSimpleTextBody(cartonCode),
                Utils.getSimpleTextBody(itemCode),
                Utils.getSimpleTextBody(pilotNo),
                Utils.getSimpleTextBody(analyticalNo),
                Utils.getSimpleTextBody(cartonSNo),
                Utils.getSimpleTextBody(totCarton),
                Utils.getSimpleTextBody("1"),
                Utils.getSimpleTextBody("2"),
                Utils.getSimpleTextBody("test"),
            )
            Log.i("Intent","$cartonCode $cartonNo $cartonSNo $itemCode $pilotNo $analyticalNo $cartonSNo $totCarton")
        }

        binding.logout.click {
            clearPreferences(this)
        }

    }

    private fun initObserver(){

        viewModel.addCarton.observe(this, Observer {
            when(it.status){
                Status.LOADING->{

                }
                Status.SUCCESS->{
                    it.let {
                        try
                        {
                            if(it.data?.status == true)
                            {
                                toast(it.data.error.toString())
                            }
                        }
                        catch (e:Exception)
                        {

                        }
                    }
                }
                Status.ERROR->{

                }
            }
        })

    }

    private fun setupUi(){

        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)

        analyticalNo = intent.extras?.getString("Analytical_No").toString()
        val materialId = intent.extras?.getString("material_id")
        stock = intent.extras?.getString("matStock").toString()
        itemCode = intent.extras?.getString("itemCode").toString()
        totCarton = intent.extras?.getString("totCarton").toString()
        pilotNo = intent.extras?.getString("pilotNo").toString()
        cartonSNo = intent.extras?.getString("cartonSNo").toString()
        cartonCode = intent.extras?.getString("cartonCode").toString()
        cartonNo = intent.extras?.getString("cartonNo").toString()
        val pilotName = intent.extras?.getString("pilotName").toString()
        val pilotCode = intent.extras?.getString("pilotCode").toString()

        binding.analyticalNumTV.text = analyticalNo
        binding.materialNumTV.text = materialId
        binding.stockTV.text = stock
        binding.palletCode.text = "Pallet Code : $pilotCode"
        binding.palletName.text = "Pallet Name : $pilotName"
        binding.palletNo.text = "Pallet no : $pilotNo"

        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )

        binding.WHTV.text = LocalPreferences.getString(this, warehouse)
        binding.rackTV.text =  LocalPreferences.getString(this, rack)
        binding.shelfTV.text =  LocalPreferences.getString(this, shelf)
        binding.palletTV.text =  LocalPreferences.getString(this, pallets)

        when
        {
            intent.extras?.getInt("isExist") == 1 ->{
                binding.updateBtn.visible()
                binding.saveBtn.gone()
                binding.palletNo.visible()
            }
        }
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        gotoActivity(LoginActivity::class.java)
    }

    override fun onBackPressed() {
        finish()
    }

}