package com.example.wms_scan.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetPalletResponse {
    
    @SerializedName("PilotNo")
    @Expose
     val pilotNo: Int? = null

    @SerializedName("PilotName")
    @Expose
     val pilotName: String? = null

    @SerializedName("ShelfNo")
    @Expose
     val shelfNo: Int? = null

    @SerializedName("ShelfName")
    @Expose
     val shelfName: String? = null

    @SerializedName("Capacity")
    @Expose
     val capacity: Int? = null

    @SerializedName("PilotCode")
    @Expose
     val pilotCode: String? = null

    @SerializedName("Status")
    @Expose
     val status: Boolean? = null

    @SerializedName("Error")
    @Expose
     val error: String? = null
    
}