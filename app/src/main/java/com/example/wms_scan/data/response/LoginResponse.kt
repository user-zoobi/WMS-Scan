package com.example.scanmate.data.response

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
class LoginResponse {

    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    @ColumnInfo(name = "UserNo")
    @SerializedName("UserNo")
    @Expose
     val userNo: Int? = null

    @ColumnInfo(name = "UserID")
    @SerializedName("UserID")
    @Expose
     val userID: String? = null

    @ColumnInfo(name = "UserName")
    @SerializedName("UserName")
    @Expose
     val userName: String? = null

    @ColumnInfo(name = "User_CategoryNo")
    @SerializedName("User_CategoryNo")
    @Expose
     val userCategoryNo: Int? = null

    @ColumnInfo(name = "User_CategoryName")
    @SerializedName("User_CategoryName")
    @Expose
     val userCategoryName: String? = null

    @ColumnInfo(name = "MobileNo")
    @SerializedName("MobileNo")
    @Expose
     val mobileNo: String? = null

    @ColumnInfo(name = "ExtensionNo")
    @SerializedName("ExtensionNo")
    @Expose
     val extensionNo: String? = null

    @ColumnInfo(name = "EmailID")
    @SerializedName("EmailID")
    @Expose
     val emailID: String? = null

    @ColumnInfo(name = "Active")
    @SerializedName("Active")
    @Expose
     val active: Boolean? = null

    @ColumnInfo(name = "EmpNo")
    @SerializedName("EmpNo")
    @Expose
     val empNo: Int? = null

    @ColumnInfo(name = "DesigName")
    @SerializedName("DesigName")
    @Expose
     val desigName: String? = null

    @ColumnInfo(name = "DeptNo")
    @SerializedName("DeptNo")
    @Expose
     val deptNo: Int? = null

    @ColumnInfo(name = "DeptName")
    @SerializedName("DeptName")
    @Expose
     val deptName: String? = null

    @ColumnInfo(name = "Super")
    @SerializedName("Super")
    @Expose
     val _super: Boolean? = null

    @ColumnInfo(name = "Administrator")
    @SerializedName("Administrator")
    @Expose
     val administrator: Boolean? = null

    @ColumnInfo(name = "Guest")
    @SerializedName("Guest")
    @Expose
     val guest: Boolean? = null

    @ColumnInfo(name = "PwdExpired")
    @SerializedName("PwdExpired")
    @Expose
     val pwdExpired: Boolean? = null

    @ColumnInfo(name = "LoginDT")
    @SerializedName("LoginDT")
    @Expose
     val loginDT: String? = null

    @ColumnInfo(name = "Status")
    @SerializedName("Status")
    @Expose
     val status: Boolean? = null

    @ColumnInfo(name = "Error")
    @SerializedName("Error")
    @Expose
     val error: String? = null

}