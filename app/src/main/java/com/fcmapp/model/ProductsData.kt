package com.fcmapp

import java.io.Serializable

data class FileResponse(val code:String,val status:String,val message:String ):Serializable

data class MyRequest(val userid:String,val filename:String,val base64:String ):Serializable
data class MyLocationRequest(val latitude:String,val longitude:String ):Serializable
