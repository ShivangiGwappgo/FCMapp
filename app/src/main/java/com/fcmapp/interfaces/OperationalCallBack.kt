package com.fcmapp.interfaces

interface OperationalCallBack {
    fun onSuccess(message: String)
    fun onFailure(message: String)
}