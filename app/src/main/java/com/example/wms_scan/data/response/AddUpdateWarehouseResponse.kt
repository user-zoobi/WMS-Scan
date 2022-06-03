package com.example.scanmate.data.response
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AddUpdateWarehouseResponse {

    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    @SerializedName("Error")
    @Expose
    val error: String? = null

    @SerializedName("Status")
    @Expose
    val status: Boolean? = null
    
}
