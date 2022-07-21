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
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.userNo
import com.example.scanmate.util.LocalPreferences.AppLoginPreferences.warehouse
import com.example.scanmate.util.Utils
import com.example.scanmate.util.Utils.isNetworkConnected
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.adapter.pallets.PalletsAdapter
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.databinding.ActivityCartonDetailBinding
import com.example.wms_scan.utils.PermissionDialog
import java.lang.Exception
import java.util.*


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
    private var selectedPalletName = ""
    private var hierarchyLoc = ""
    private var deviceId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartonDetailBinding.inflate(layoutInflater)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setContentView(binding.root)
        permissionDialog = PermissionDialog(this)
        initListener()
        setupUi()

        val palletCode =  intent.extras?.getString("palletCode").toString()
        val scannedAnalyticalNo =  intent.extras?.getString("scanAnalyticalNum").toString()
        Log.i("analyticalNum", analyticalNo.toString())
        Log.i("palletCode", palletCode)

        /**
         *  GET PALLET API
         */


        viewModel.getPallet.observe(this, Observer {
            when(it.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    it.let {
                        showPalletSpinner(it.data!!)
                    }
                }
                Status.ERROR ->{

                }
            }
        })

        /**
         *  PALLET HIERARCHY API
         */

        viewModel.palletHierarchy(
            Utils.getSimpleTextBody(palletCode),
            Utils.getSimpleTextBody("0")
        )
        viewModel.palletHierarchy.observe(this, Observer {

            when(it.status){

                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    Log.i("palletCode","${it.data?.get(0)?.pilotCode}")

                    val warehouse = it.data?.get(0)?.wHName.toString()
                    val racks = it.data?.get(0)?.rackName.toString()
                    val shelf = it.data?.get(0)?.shelfName.toString()
                    val shelfNo = it.data?.get(0)?.shelfNo.toString()
                    val pallet = it.data?.get(0)?.pilotName.toString()
                    val palletNo = it.data?.get(0)?.pilotNo.toString()
                    val busLoc = it.data?.get(0)?.locationNo.toString()
                    hierarchyLoc = it.data?.get(0)?.locationNo.toString()
                    Log.i("shelfNoCarton",shelfNo)
                    Log.i("palletHierarchyData","1. $warehouse \n 2.$racks\n 3.$shelf\n 4.$shelfNo\n 5.$pallet\n 6.$palletNo\n 7.$busLoc\n 8.$hierarchyLoc" )

                    binding.WHTV.text = warehouse
                    binding.rackTV.text = racks
                    binding.shelfTV.text = shelf
                    binding.palletTV.text = pallet

                    val hierarchyPilotNo = it.data?.get(0)?.pilotNo.toString()
                    Log.i("hierarchyPilot","$hierarchyPilotNo $busLoc")

                    viewModel.getCarton(
                        Utils.getSimpleTextBody(palletNo),
                        Utils.getSimpleTextBody(busLoc),
                    )

                    viewModel.getPallet(
                        Utils.getSimpleTextBody(""),
                        Utils.getSimpleTextBody(shelfNo),
                        Utils.getSimpleTextBody(busLoc),
                    )

                    Log.i("hierarchyPilotNo","$shelfNo $busLoc")

                }

                Status.ERROR ->{

                }
            }

        })

        /**
         *  CARTON DETAIL API
         */

        viewModel.getCartonDetails("$scannedAnalyticalNo")

        viewModel.getCartonDetails.observe(this, Observer{

            when(it.status){

                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    it.let {
                        if(isNetworkConnected(this)){

                            binding.materialNumTV.text = it.data?.get(0)?.materialName
                            binding.materialNumTV.isSelected = true
                            binding.analyticalNumTV.text = it.data?.get(0)?.analyticalNo
                            binding.cartonNumTV.text = it.data?.get(0)?.cartonNo.toString()
                            binding.stockTV.text = it.data?.get(0)?.matStock.toString()
                            binding.palletName.text = it.data?.get(0)?.pilotName.toString()
                            analyticalNo = it.data?.get(0)?.analyticalNo.toString()

                        }
                    }

                }
                Status.ERROR ->{

                }

            }

        })

        /**
         *  GET CARTON API
         */

        viewModel.getCarton.observe(this, Observer {
            when(it.status){

                Status.LOADING->{ }

                Status.SUCCESS ->
                {
                    it.let {
                        if(isNetworkConnected(this)){
                        val cartonNo = it.data?.get(0)?.cartonNo.toString()
                        val itemCode = it.data?.get(0)?.itemCode.toString()
                        val pilotNo = it.data?.get(0)?.pilotNo.toString()
                        val cartonSNo = it.data?.get(0)?.cartonSNo.toString()
                        val totCarton = it.data?.get(0)?.totCarton.toString()

                            binding.saveBtn.click {

                                viewModel.addCarton(
                                    Utils.getSimpleTextBody("0"),
                                    Utils.getSimpleTextBody("0"),
                                    Utils.getSimpleTextBody("$itemCode"),
                                    Utils.getSimpleTextBody("$pilotNo"),
                                    Utils.getSimpleTextBody("$analyticalNo"),
                                    Utils.getSimpleTextBody("$cartonSNo"),
                                    Utils.getSimpleTextBody("$totCarton"),
                                    Utils.getSimpleTextBody(hierarchyLoc),
                                    Utils.getSimpleTextBody(LocalPreferences.getInt(this,userNo).toString()),
                                    Utils.getSimpleTextBody(deviceId)
                                )
                                Log.i(
                                    "addCarton","1.0\n2.0\n3.$itemCode\n4.$pilotNo\n5.$analyticalNo\n6.$cartonSNo\n7.$totCarton\n8.$hierarchyLoc\n9.$deviceId"
                                )
                                Log.i("itemcode",itemCode)

                            }

                            binding.updateBtn.click {

                                viewModel.addCarton(
                                    Utils.getSimpleTextBody("$cartonNo"),
                                    Utils.getSimpleTextBody("0"),
                                    Utils.getSimpleTextBody("$itemCode"),
                                    Utils.getSimpleTextBody("$selectedPalletNo"),
                                    Utils.getSimpleTextBody("$analyticalNo"),
                                    Utils.getSimpleTextBody("$cartonSNo"),
                                    Utils.getSimpleTextBody("$totCarton"),
                                    Utils.getSimpleTextBody(hierarchyLoc),
                                    Utils.getSimpleTextBody(LocalPreferences.getInt(this,userNo).toString()),
                                    Utils.getSimpleTextBody(deviceId)
                                )

                                viewModel.palletHierarchy(
                                    Utils.getSimpleTextBody("$selectedPalletCode"),
                                    Utils.getSimpleTextBody("0")
                                )

                                binding.updateBtn.gone()
                                binding.selectPalletCont.gone()
                                binding.changeTV.visible()

                            }
                    }
                    }
                    
                    Log.i("cartonDetails",it.data?.get(0)?.cartonNo.toString())
                    Log.i("getCarton","1.$cartonNo\n$cartonCode.0\n3.$itemCode\n4.$pilotNo\n5.$cartonSNo\n6.$cartonSNo\n7.$totCarton\n")
                }

                Status.ERROR ->{

                }
            }
        })

        /**
         *  ADD CARTON API
         */

        viewModel.addCarton.observe(this, Observer {
            when(it.status){

                Status.LOADING ->{ }

                Status.SUCCESS ->
                {
                    it.let {
                        if(isNetworkConnected(this))
                        {
                            Log.i("IntentSave","${it.data?.error}")
                            toast("${it.data?.error}")
                        }
                    }
                }

                Status.ERROR ->{ }

            }
        })

    }

    private fun initListener(){

        binding.backBtn.click {
            onBackPressed()
        }

//        binding.logout.click {
//            clearPreferences(this)
//        }

        binding.closeBtn.click{
            binding.selectPalletCont.gone()
            binding.changeTV.visible()
            binding.selectPalletCont.gone()
            binding.updateBtn.gone()
            binding.palletView.gone()
        }

        binding.changeTV.click {
            binding.selectPalletCont.visible()
            binding.updateBtn.visible()
            binding.changeTV.gone()
            binding.palletView.visible()

        }

    }

    private fun setupUi(){

        supportActionBar?.hide()
        setTransparentStatusBarColor(R.color.transparent)
        Log.i("pilotNo",pilotNo)

        binding.analyticalNumTV.text = analyticalNo
//        binding.materialNumTV.text = materialId
        binding.stockTV.text = stock
        binding.cartonNumTV.text = cartonNo

        binding.userNameTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userName
        )
        binding.userDesignTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.userDesignation
        )
        binding.loginTimeTV.text = LocalPreferences.getString(this,
            LocalPreferences.AppLoginPreferences.loginTime
        )

        deviceId = UUID.randomUUID().toString()
        Log.i("deviceId",deviceId)

        Log.i("isExist",intent.extras?.getInt("isExist").toString())
        when
        {
            intent.extras?.getInt("isExist") == 1 ->{
                binding.changeTV.visible()
            }

            intent.extras?.getInt("isExist") == 0 ->{
                binding.changeTV.gone()
                binding.saveBtn.visible()
                binding.palletName.gone()
                binding.palletNameTV.gone()
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
                    if (isNetworkConnected(this@CartonDetailActivity))
                    {
                        Log.i("PalletNo","This is pallet pos ${adapter?.getItemAtPosition(position)}")
                        selectedPalletNo = data[position].pilotNo.toString()
                        selectedPalletCode = data[position].pilotCode.toString()
                        selectedPalletName = data[position].pilotName.toString()
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