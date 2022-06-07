package com.example.scanmate.data.api

import com.example.scanmate.data.response.*
import com.example.wms_scan.data.routes.Routes.EndPoint.addRack
import com.example.wms_scan.data.routes.Routes.EndPoint.addShelf
import com.example.wms_scan.data.routes.Routes.EndPoint.addUpdateWarehouse
import com.example.wms_scan.data.routes.Routes.EndPoint.getPallet
import com.example.wms_scan.data.routes.Routes.EndPoint.getRack
import com.example.wms_scan.data.routes.Routes.EndPoint.getShelf
import com.example.wms_scan.data.routes.Routes.EndPoint.getWarehouse
import com.example.wms_scan.data.routes.Routes.EndPoint.userAuth
import com.example.wms_scan.data.routes.Routes.EndPoint.userLoc
import com.example.wms_scan.data.routes.Routes.EndPoint.userMenu
import com.example.wms_scan.data.response.AddPalletResponse
import com.example.wms_scan.data.response.GetPalletResponse
import com.example.wms_scan.data.routes.Routes.EndPoint.addPallet
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

//
    @Multipart
    @POST(userAuth)
    suspend fun userAuthLogin(
        @Part("UserID") UserID: RequestBody,
        @Part("Pwd") Pwd: RequestBody
    ): List<LoginResponse>


    @Multipart
    @POST(userLoc)
    suspend fun userLocation(
        @Part("UserNo") UserNo:RequestBody
    ):List<UserLocationResponse>


    @Multipart
    @POST(userMenu)
    suspend fun userMenu(
        @Part("UserNo") UserNo:RequestBody,
        @Part("LocationNo") LocationNo:RequestBody
    ):List<UserMenuResponse>


    @FormUrlEncoded
    @POST(getWarehouse)
    suspend fun getWarehouse(
        @Field("WH_Name") WH_Name:String,
        @Field("LocationNo") LocationNo:String
    ):List<GetWarehouseResponse>

    @FormUrlEncoded
    @POST(addUpdateWarehouse)
    suspend fun addUpdateWarehouse(
        @Field("WH_No") WH_No:String,
        @Field("WH_Name") WH_Name:String,
        @Field("WH_Code") WH_Code:String,
        @Field("LocationNo") LocationNo:String,
        @Field("DMLUserNo") DMLUserNo:String,
        @Field("DMLPCName") DMLPCName:String,
    ):AddUpdateWarehouseResponse

    @Multipart
    @POST(addRack)
    suspend fun addRack(
        @Part("RackNo") RackNo:RequestBody,
        @Part("RackName") RackName:RequestBody,
        @Part("RackCode") RackCode:RequestBody,
        @Part("WH_No") WH_No:RequestBody,
        @Part("Capacity") Capacity:RequestBody,
        @Part("LocationNo") LocationNo:RequestBody,
        @Part("DMLUserNo") DMLUserNo:RequestBody,
        @Part("DMLPCName") DMLPCName:RequestBody
    ):AddRackResponse

    @Multipart
    @POST(addShelf)
    suspend fun addShelf(
        @Part("ShelfNo") ShelfNo:RequestBody,
        @Part("RackNo") RackNo:RequestBody,
        @Part("ShelfName") ShelfName:RequestBody,
        @Part("ShelfCode") ShelfCode:RequestBody,
        @Part("Capacity") Capacity:RequestBody,
        @Part("LocationNo") LocationNo:RequestBody,
        @Part("DMLUserNo") DMLUserNo:RequestBody,
        @Part("DMLPCName") DMLPCName:RequestBody
    ):AddShelfResponse


    @Multipart
    @POST(getRack)
    suspend fun getRack(
        @Part("RackName") RackName:RequestBody,
        @Part("WH_No") WH_No:RequestBody,
        @Part("LocationNo") LocationNo:RequestBody
    ):List<GetRackResponse>


    @Multipart
    @POST(getShelf)
    suspend fun getShelf(
        @Part("ShelfName") ShelfName:RequestBody,
        @Part("RackNo") RackNo:RequestBody,
        @Part("LocationNo") LocationNo:RequestBody
    ):List<GetShelfResponse>

    @Multipart
    @POST(getPallet)
    suspend fun getPallet(
        @Part("PilotName") PilotName:RequestBody,
        @Part("ShelfNo") ShelfNo:RequestBody,
        @Part("LocationNo") LocationNo:RequestBody,
    ): List<GetPalletResponse>

    @Multipart
    @POST(addPallet)
    suspend fun addPallet(
        @Part("PilotNo") PilotNo:RequestBody,
        @Part("PilotName") PilotName:RequestBody,
        @Part("PilotCode") PilotCode:RequestBody,
        @Part("ShelfNo") ShelfNo:RequestBody,
        @Part("Capacity") Capacity:RequestBody,
        @Part("LocationNo") LocationNo:RequestBody,
        @Part("DMLUserNo") DMLUserNo:RequestBody,
        @Part("DMLPCName") DMLPCName:RequestBody,
    ): AddPalletResponse

}