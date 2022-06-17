package com.example.wms_scan.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaletteHierarchy {
    @SerializedName("WH_Name")
    @Expose
    val wHName: Any? = null

    @SerializedName("RackName")
    @Expose
    val rackName: Any? = null

    @SerializedName("ShelfName")
    @Expose
    val shelfName: Any? = null

    @SerializedName("PilotName")
    @Expose
    val pilotName: Any? = null

    @SerializedName("Status")
    @Expose
    val status: Boolean? = null

    @SerializedName("Error")
    @Expose
    val error: String? = null
}