package com.fcmapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fcmapp.interfaces.OperationalCallBack
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel   : ViewModel() {
    fun uploadLocation(latitude: String, longitude: String, callBack: OperationalCallBack) {
        viewModelScope.launch {
            var request= MyLocationRequest(latitude,longitude)
            RetrofitInstance.api.uploadLocation("asdf","location",request).enqueue(object :
                Callback<FileResponse> {
                override fun onResponse(call: Call<FileResponse>, response: Response<FileResponse>)
                {
                    Log.d("Location upload..", response.body().toString())
                    callBack.onSuccess(response.body().toString())
                }
                override fun onFailure(call: Call<FileResponse>, t: Throwable) {
                    Log.d("Location fail..", t.message.toString())
                    callBack.onFailure( t.message.toString())
                }
            })

        }
    }

    fun uploadBase64File(sImage: String, userId: String?,callBack: OperationalCallBack) {
        viewModelScope.launch {
            var request=MyRequest(""+userId,"shivanshiii.jpg",""+sImage)
            RetrofitInstance.api.uploadBase64File("asdf","fileupbase64",request).enqueue(object :
                Callback<FileResponse> {
                override fun onResponse(
                    call: Call<FileResponse>,
                    response: Response<FileResponse>
                ) {
                    Log.d("response..base64 success..", response.body().toString())
                    callBack.onSuccess(response.body().toString())

                    if (response.body() != null) {
                       // movieLiveData.value = response.body()?.Data
                    } else {
                        return
                    }
                }

                override fun onFailure(call: Call<FileResponse>, t: Throwable) {
                    Log.d("response..base64 fail..",  t.message.toString())
                    callBack.onFailure( t.message.toString())

                }
            })

        }
    }
    fun uploadMultipartFile(body: MultipartBody.Part, userId: String?,fileType:String,callBack: OperationalCallBack) {
        viewModelScope.launch {
            val userid: RequestBody = RequestBody.create(MediaType.parse("text/plain"), ""+userId)
            val filename: RequestBody = RequestBody.create(MediaType.parse("text/plain"), ""+fileType)

            RetrofitInstance.api.uploadUserFile("asdf","multipartfileupload",userid,filename,body)?.enqueue(object :
                Callback<FileResponse> {
                override fun onResponse(
                    call: Call<FileResponse>,
                    response: Response<FileResponse>
                ) {
                    Log.d("response..part success..", response.body().toString())
                    callBack.onSuccess(response.body().toString())

                    if (response.body() != null) {
                        // movieLiveData.value = response.body()?.Data
                    } else {
                        return
                    }
                }

                override fun onFailure(call: Call<FileResponse>, t: Throwable) {
                    Log.d("response..part fail..", t.message.toString())
                    callBack.onFailure( t.message.toString())

                }
            })
        }
    }

    }