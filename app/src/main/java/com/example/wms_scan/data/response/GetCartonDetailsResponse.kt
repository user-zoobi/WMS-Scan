package com.example.wms_scan.data.response
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetCartonDetailsResponse {

    @SerializedName("Analytical_No")
    @Expose
    val analyticalNo: String? = null

    @SerializedName("material_id")
    @Expose
    val materialId: String? = null

    @SerializedName("Material_name")
    @Expose
    val materialName: String? = null

    @SerializedName("Mat_Stock")
    @Expose
    val matStock: Double? = null

    @SerializedName("IsExist")
    @Expose
    val isExist: Int? = null

    @SerializedName("Status")
    @Expose
    val status: Boolean? = null

    @SerializedName("Error")
    @Expose
    val error: String? = null

}