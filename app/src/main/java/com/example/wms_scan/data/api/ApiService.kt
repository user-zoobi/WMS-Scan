package com.example.scanmate.data.api

import com.example.scanmate.data.response.*
import com.example.scanmate.data.routes.Routes.EndPoint.addRack
import com.example.scanmate.data.routes.Routes.EndPoint.addShelf
import com.example.scanmate.data.routes.Routes.EndPoint.addUpdateWarehouse
import com.example.scanmate.data.routes.Routes.EndPoint.getRack
import com.example.scanmate.data.routes.Routes.EndPoint.getShelf
import com.example.scanmate.data.routes.Routes.EndPoint.getWarehouse
import com.example.scanmate.data.routes.Routes.EndPoint.userAuth
import com.example.scanmate.data.routes.Routes.EndPoint.userLoc
import com.example.scanmate.data.routes.Routes.EndPoint.userMenu
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
        @Part("ShelfNo") RackNo:RequestBody,
        @Part("RackNo") RackName:RequestBody,
        @Part("ShelfName") RackCode:RequestBody,
        @Part("ShelfCode") WH_No:RequestBody,
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

}