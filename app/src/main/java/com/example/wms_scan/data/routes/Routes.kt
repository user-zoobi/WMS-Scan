package com.example.wms_scan.data.routes

object Routes {

    const val BASE_URL = "https://call.i-konnect.net/"


    object EndPoint{
        const val userAuth = "GAuthAPI/Get_SM_User_Auth"
        const val userLoc = "WHUserAPI/Get_SM_User_Location"
        const val userMenu = "WHUserAPI/Get_SM_UserMenu"
        const val addUpdateWarehouse = "WHAPI/DMl_WareHouse"
        const val getWarehouse = "WHAPI/Get_WareHouse"
        const val addRack = "RackAPI/DML_Rack"
        const val addShelf = "ShelfAPI/DMl_Shelf"
        const val getRack = "RackAPI/Get_Rack"
        const val getShelf = "ShelfAPI/Get_Shelf"
        const val getPallet = "PilotAPI/Get_Pilot"
        const val addPallet = "PilotAPI/DML_Pilot"
        const val getCarton = "CartonAPI/Get_Carton"
    }

}