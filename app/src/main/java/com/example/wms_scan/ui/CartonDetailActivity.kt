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
import com.example.scanmate.data.response.GetShelfResponse
import com.example.scanmate.extensions.*
import com.example.scanmate.util.Constants
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.pallets
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.rack
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.shelf
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.warehouse
import com.example.scanmate.util.Utils
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.data.response.GetPalletResponse
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
    private var selectedPalletNo = ""
    private var selectedPalletCode = ""

    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartonDetailBinding.inflate(layoutInflater)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setContentView(binding.root)
        permissionDialog = PermissionDialog(this)
        initListener()
        initObserver()
        setupUi()

        val scannedValue =  intent.extras?.getString("scannedValue")
        val palletNo = scannedValue?.substringAfter("SF-")
        Log.i("qrcode", scannedValue.toString())
        Log.i("palletNo", palletNo.toString())

        /**
         *  PALLET HIERARCHY API
         */

        viewModel.palletHierarchy(
            Utils.getSimpleTextBody("$palletNo-27")
        )
        viewModel.palletHierarchy.observe(this, Observer {

            when(it.status){

                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    Log.i("palletCode","${it.data?.get(0)?.pilotCode}")

                    binding.palletCode.text = "Pallet Code : ${it.data?.get(0)?.pilotCode}"
                    binding.palletName.text = "Pallet Name : ${it.data?.get(0)?.pilotName}"
                    binding.palletNo.text = "Pallet no : ${it.data?.get(0)?.pilotNo}"
                }
                Status.ERROR ->{

                }

            }
        })


        val analyticalNum =  intent.extras?.getString("scanAnalyticalNum")
        Log.i("analyticalNum", analyticalNum.toString())

        /**
         *  CARTON DETAIL API
         */

        viewModel.getCartonDetails(
            "$analyticalNum"
        )
        viewModel.getCartonDetails.observe(this, Observer{

            when(it.status){

                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    binding.materialNumTV.text = it.data?.get(0)?.materialName
                    binding.analyticalNumTV.text = it.data?.get(0)?.analyticalNo
                    binding.cartonNumTV.text = it.data?.get(0)?.cartonNo.toString()
                    binding.stockTV.text = it.data?.get(0)?.matStock.toString()
                }
                Status.ERROR ->{

                }

            }

        })

        viewModel.addCarton.observe(this, Observer {
            when(it.status){

                Status.LOADING ->{

                }
                Status.SUCCESS ->{

                }
                Status.ERROR ->{

                }

            }
        })

    }

    private fun initListener(){

        binding.backBtn.click {
            onBackPressed()
        }

        binding.saveBtn.click {
            with(viewModel) {

                this.addCarton(
                    CartonNo = Utils.getSimpleTextBody("0"),
                    CartonCode = Utils.getSimpleTextBody(cartonCode),
                    ItemCode = Utils.getSimpleTextBody("TEST"), //
                    PilotNo = Utils.getSimpleTextBody(pilotNo),
                    AnalyticalNo = Utils.getSimpleTextBody(analyticalNo), //
                    Carton_SNo = Utils.getSimpleTextBody(cartonSNo), //
                    TotCarton = Utils.getSimpleTextBody(totCarton), //
                    LocationNo = Utils.getSimpleTextBody("1"),
                    DMLUserNo = Utils.getSimpleTextBody("2"),
                    DMLPCName = Utils.getSimpleTextBody("test")
                )
                Log.i("IntentSave","\n$cartonCode\n $cartonNo\n $cartonSNo\n $itemCode\n $pilotNo\n $analyticalNo\n $cartonSNo\n $totCarton")
                binding.saveBtn.gone()
                binding.updateBtn.visible()
            }
        }

        binding.updateBtn.click {

            viewModel.addCarton(
                Utils.getSimpleTextBody(cartonNo),
                CartonCode = Utils.getSimpleTextBody(cartonCode),
                ItemCode = Utils.getSimpleTextBody("TEST"), //
                PilotNo = Utils.getSimpleTextBody(selectedPalletNo),
                AnalyticalNo = Utils.getSimpleTextBody(analyticalNo), //
                Carton_SNo = Utils.getSimpleTextBody(cartonSNo), //
                TotCarton = Utils.getSimpleTextBody(totCarton), //
                LocationNo = Utils.getSimpleTextBody("1"),
                DMLUserNo = Utils.getSimpleTextBody("2"),
                DMLPCName = Utils.getSimpleTextBody("test"),
            )

            viewModel.palletHierarchy(
                Utils.getSimpleTextBody("$selectedPalletCode-$selectedPalletNo")
            )

            Log.i("IntentUpdate","$cartonCode $cartonNo $cartonSNo $itemCode $pilotNo $analyticalNo $cartonSNo $totCarton")
        }

        binding.logout.click {
            clearPreferences(this)
        }

        binding.closeBtn.click {
            binding.selectPalletCont.gone()
            binding.palletValuesCont.visible()
            binding.updateBtn.gone()
        }

        binding.changeTV.click {
            binding.palletValuesCont.gone()
            binding.selectPalletCont.visible()
            binding.updateBtn.visible()
            binding.saveBtn.gone()
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
                                Log.i("addCarton",it.data.status.toString())
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

        viewModel.getPallet(
            Utils.getSimpleTextBody(""),
            Utils.getSimpleTextBody("48"),
            Utils.getSimpleTextBody("1"),
        )
        viewModel.getPallet.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{
                    Log.i(Constants.LogMessages.loading,"Loading")
                }
                Status.SUCCESS ->{
                    try
                    {
                        LocalPreferences.put(this,
                            LocalPreferences.AppLoginPreferences.isRefreshRequired, true)
                        if(it.data?.get(0)?.status == true)
                        {
                            Log.i(Constants.LogMessages.success,"Success")
                            showPalletSpinner(it.data)
                        }
                        else { }
                    }
                    catch (e:Exception)
                    {
                        Log.i("exception","${e.message}")
                        Log.i("rackAdapter","${e.stackTrace}")
                    }

                }
                Status.ERROR ->{
                    Log.i(Constants.LogMessages.error,"Success")
                }
            }
        })

        viewModel.palletHierarchy.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{

                    val warehouse = it.data?.get(0)?.wHName.toString()
                    val  racks = it.data?.get(0)?.rackName.toString()
                    val shelf = it.data?.get(0)?.shelfName.toString()
                    val pallet = it.data?.get(0)?.pilotName.toString()

                    binding.WHTV.text = warehouse
                    binding.rackTV.text = racks
                    binding.shelfTV.text = shelf
                    binding.palletTV.text = pallet
                }
                Status.ERROR ->{

                }
            }
        })

        viewModel.getCartonDetails.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{

                }
                Status.ERROR ->{

                }
            }
        })

    }

    private fun setupUi(){

        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        Log.i("pilotNo",pilotNo)

        binding.analyticalNumTV.text = analyticalNo
//        binding.materialNumTV.text = materialId
        binding.stockTV.text = stock
        binding.cartonNumTV.text = cartonNo
//        binding.palletCode.text = "Pallet Code : $pilotCode"
//        binding.palletName.text = "Pallet Name : $pilotName"
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

        when
        {
            intent.extras?.getInt("isExist") == 1 ->{
                binding.saveBtn.gone()
                binding.palletNo.visible()

            }

            intent.extras?.getInt("isExist") == 0 ->{
                binding.palletCode.gone()
                binding.palletName.gone()
                binding.palletNo.gone()
                binding.palletView.gone()
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
                    if (Utils.isNetworkConnected(this@CartonDetailActivity))
                    {
                        Log.i("PalletNo","This is pallet pos ${adapter?.getItemAtPosition(position)}")
                        selectedPalletNo = data[position].pilotNo.toString()
                        selectedPalletCode = data[position].pilotCode.toString()

                    }
                    else
                    {
                        toast(Constants.Toast.NoInternetFound)
                    }

                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }



    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        gotoActivity(LoginActivity::class.java)
    }

}