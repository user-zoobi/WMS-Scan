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
    private var hierarchyPilotNo = ""
    private var cartonSNo = ""
    private var cartonCode = ""
    private var cartonNo = ""
    private var itemCode = ""
    private var stock = ""
    private var selectedPalletNo = ""
    private var selectedPalletCode = ""
    private var selectedPalletName = ""

    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartonDetailBinding.inflate(layoutInflater)
        viewModel = obtainViewModel(MainViewModel::class.java)
        setContentView(binding.root)
        permissionDialog = PermissionDialog(this)
        initListener()
        setupUi()

        val palletCode =  intent.extras?.getString("palletCode")
        val analyticalNum =  intent.extras?.getString("scanAnalyticalNum")
        Log.i("analyticalNum", analyticalNum.toString())
        Log.i("palletCode", palletCode.toString())


        /**
         *  PALLET HIERARCHY API
         */

        viewModel.palletHierarchy(
            Utils.getSimpleTextBody("$palletCode"),
            Utils.getSimpleTextBody("0")
        )
        viewModel.palletHierarchy.observe(this, Observer {

            when(it.status){

                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    Log.i("palletCode","${it.data?.get(0)?.pilotCode}")

                    val warehouse = it.data?.get(0)?.wHName.toString()
                    val  racks = it.data?.get(0)?.rackName.toString()
                    val shelf = it.data?.get(0)?.shelfName.toString()
                    val pallet = it.data?.get(0)?.pilotName.toString()

                    binding.WHTV.text = warehouse
                    binding.rackTV.text = racks
                    binding.shelfTV.text = shelf
                    binding.palletTV.text = pallet

                    binding.palletCode.text = "Pallet Code : ${it.data?.get(0)?.pilotCode}"
                    binding.palletName.text = "Pallet Name : ${it.data?.get(0)?.pilotName}"
                    binding.palletNo.text = "Pallet no : ${it.data?.get(0)?.pilotNo}"

                    hierarchyPilotNo = it.data?.get(0)?.pilotNo.toString()
                    Log.i("hierarchyPilotNo",hierarchyPilotNo)

                }

                Status.ERROR ->{

                }
            }

        })

        /**
         *  CARTON DETAIL API
         */

        viewModel.getCartonDetails("$analyticalNum")

        viewModel.getCartonDetails.observe(this, Observer{

            when(it.status){

                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    binding.materialNumTV.text = it.data?.get(0)?.materialName
                    binding.analyticalNumTV.text = it.data?.get(0)?.analyticalNo
                    binding.cartonNumTV.text = it.data?.get(0)?.cartonNo.toString()
                    binding.stockTV.text = it.data?.get(0)?.matStock.toString()

                    analyticalNo = it.data?.get(0)?.analyticalNo.toString()

                }
                Status.ERROR ->{

                }

            }

        })

        /**
         *  GET CARTON API
         */

        viewModel.getCarton(
            Utils.getSimpleTextBody("$hierarchyPilotNo"),
            Utils.getSimpleTextBody("1"),
        )
        viewModel.getCarton.observe(this, Observer {
            when(it.status){
                Status.LOADING->{

                }
                Status.SUCCESS ->{
                    it.let {
                        cartonNo = it.data?.get(0)?.cartonNo.toString()
                        cartonCode = it.data?.get(0)?.cartonCode.toString()
                        itemCode = it.data?.get(0)?.itemCode.toString()
                        pilotNo = it.data?.get(0)?.pilotNo.toString()
                        cartonSNo = it.data?.get(0)?.cartonSNo.toString()
                        totCarton = it.data?.get(0)?.totCarton.toString()
                    }
                    Log.i("cartonDetails",it.data?.get(0)?.cartonNo.toString())
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

                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    Log.i("IntentSave","${it.data?.error}")
                    toast("${it.data?.error}")

                }
                Status.ERROR ->{

                }

            }
        })

        /**
         *  ADD CARTON API
         */

    }

    private fun initListener(){

        binding.backBtn.click {
            onBackPressed()
        }

        binding.saveBtn.click {

            viewModel.addCarton(
                Utils.getSimpleTextBody("0"),
                Utils.getSimpleTextBody("$cartonCode"),
                Utils.getSimpleTextBody("$itemCode"),
                Utils.getSimpleTextBody("$pilotNo"),
                Utils.getSimpleTextBody("$analyticalNo"),
                Utils.getSimpleTextBody("$cartonSNo"),
                Utils.getSimpleTextBody("$totCarton"),
                Utils.getSimpleTextBody("1"),
                Utils.getSimpleTextBody("2"),
                Utils.getSimpleTextBody("test")
            )
            Log.i("saveCarton","$cartonCode $itemCode $pilotNo $analyticalNo $cartonSNo $totCarton")

        }

        binding.updateBtn.click {

            viewModel.addCarton(
                Utils.getSimpleTextBody("$cartonNo"),
                Utils.getSimpleTextBody("$cartonCode"),
                Utils.getSimpleTextBody("$itemCode"),
                Utils.getSimpleTextBody("$pilotNo"),
                Utils.getSimpleTextBody("$analyticalNo"),
                Utils.getSimpleTextBody("$cartonSNo"),
                Utils.getSimpleTextBody("$totCarton"),
                Utils.getSimpleTextBody("1"),
                Utils.getSimpleTextBody("2"),
                Utils.getSimpleTextBody("test")
            )
        }

//        binding.logout.click {
//            clearPreferences(this)
//        }

        binding.closeBtn.click {
            binding.selectPalletCont.gone()
            binding.palletValuesCont.visible()
            binding.updateBtn.gone()
            binding.saveBtn.visible()
        }

        binding.changeTV.click {
            binding.palletValuesCont.gone()
            binding.selectPalletCont.visible()
            binding.updateBtn.visible()
            binding.saveBtn.gone()
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