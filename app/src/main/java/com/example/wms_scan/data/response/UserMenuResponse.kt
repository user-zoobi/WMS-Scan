package com.example.scanmate.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserMenuResponse {
    
    @SerializedName("MenuNo")
    @Expose
     val menuNo: Int? = null

    @SerializedName("Menu")
    @Expose
     val menu: String? = null

    @SerializedName("Status")
    @Expose
     val status: Boolean? = null

    @SerializedName("Error")
    @Expose
     val error: String? = null
    
}