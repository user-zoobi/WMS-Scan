package com.example.scanmate.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddShelfResponse {

    @SerializedName("Error")
    @Expose
     val error: String? = null

    @SerializedName("Status")
    @Expose
     val status: Boolean? = null

}