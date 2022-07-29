package com.example.scanmate.extensions

import android.R
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.boschscan.extensions.putExtra

fun <T : AppCompatActivity> AppCompatActivity.gotoActivity(targetActivityClass: Class<T>) {
    val intent = Intent(this, targetActivityClass)
    startActivity(intent)
}

fun AppCompatActivity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun View.click(it: (View) -> Unit) {
    this.setOnClickListener(it)
}

fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProvider.NewInstanceFactory().create(viewModelClass)


fun <T : AppCompatActivity> Fragment.gotoActivityFromFragment(targetActivityClass: Class<T>) {
    val intent = Intent(requireActivity(), targetActivityClass)
    startActivity(intent)
}

fun Fragment.gotoActivityFromFragment(
    targetActivityClass: Class<*>,
    intentKey: String,
    intentValue: Any? = null
) {
    val i = Intent(requireActivity(), targetActivityClass)
    i.putExtra(intentKey, intentValue)
    startActivity(i)
}

fun Fragment.gotoActivityFromFragment(
    targetActivityClass: Class<*>,
    intentKey: String,
    intentValue: Any? = null,
    intentKey1: String,
    intentValue1: Any? = null

) {
    val i = Intent(requireActivity(), targetActivityClass)
    i.putExtra(intentKey, intentValue)
    i.putExtra(intentKey1, intentValue1)
    startActivity(i)
}

fun AppCompatActivity.gotoActivity(
    targetActivityClass: Class<*>,
    intentKey: String,
    intentValue: Any? = null
) {
    val i = Intent(this, targetActivityClass)
    i.putExtra(intentKey, intentValue)
    startActivity(i)
}

fun AppCompatActivity.gotoActivity(
    targetActivityClass: Class<*>,
    intentKey1: String,
    intentValue1: Any? = null,
    intentKey2: String,
    intentValue2: Any? = null,
    intentKey3: String,
    intentValue3: Any? = null
) {
    val i = Intent(this, targetActivityClass)
    i.putExtra(intentKey1, intentValue1)
    i.putExtra(intentKey2, intentValue2)
    i.putExtra(intentKey3, intentValue3)
    startActivity(i)
}

fun AppCompatActivity.setTransparentStatusBarColor(color: Int) {
    val window: Window = window
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(false)
    } else {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(this, color)
}

fun Context.returnNoAdapter() : SpinnerAdapter
{
    val noItem: List<String> = arrayListOf("No Record")
    return ArrayAdapter(this, R.layout.simple_list_item_1, noItem)
}