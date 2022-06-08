package com.example.wms_scan.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.scanmate.extensions.gone
import com.example.scanmate.extensions.setTransparentStatusBarColor
import com.example.scanmate.extensions.visible
import com.example.scanmate.util.CustomProgressDialog
import com.example.scanmate.util.LocalPreferences
import com.example.scanmate.viewModel.MainViewModel
import com.example.wms_scan.R
import com.example.wms_scan.databinding.ActivityAddUpdateCartonBinding
import com.example.wms_scan.databinding.ActivityMenuBinding

class AddUpdateCarton : AppCompatActivity() {
    private lateinit var binding: ActivityAddUpdateCartonBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var dialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateCartonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUi()
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

                binding.addCartonBtn.gone()
                binding.updateCartonBtn.visible()
                binding.editDetailTV.text = "Update to"

            }
        }

    }

    private fun initListener(){
        binding.toolbar.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            clearPreferences(this)
            true
        }
    }

    private fun clearPreferences(context: Context){
        val settings: SharedPreferences =
            context.getSharedPreferences(LocalPreferences.AppLoginPreferences.PREF, Context.MODE_PRIVATE)
        settings.edit().clear().apply()
        finish()
    }

}