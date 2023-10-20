package com.fcmapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory (
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        try {
            val constructor = modelClass.getDeclaredConstructor(Repository::class.java)
            return constructor.newInstance(repository)
        } catch (e: Exception) {
            Log.e(e.message.toString(),"catch...")
        }
        return super.create(modelClass)
    }
}
