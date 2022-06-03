package com.example.boschscan.extensions

import android.content.Intent
import android.os.Parcelable

fun Intent.putExtra(intentKey: String, intentValue: Any?) {
    when (intentValue) {
        is Boolean -> putExtra(intentKey, intentValue)
        is Int -> putExtra(intentKey, intentValue)
        is Float -> putExtra(intentKey, intentValue)
        is Double -> putExtra(intentKey, intentValue)
        is Long -> putExtra(intentKey, intentValue)
        is String -> putExtra(intentKey, intentValue)
        is Parcelable -> putExtra(intentKey, intentValue)
    }
}