package com.fcmapp.volley

import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.fcmapp.utility.Constant
import com.fcmapp.MyRequest
import com.fcmapp.interfaces.OperationalCallBack
import org.json.JSONException
import org.json.JSONObject


class VolleyHandler(context: Context) {

    private var requestQueue: RequestQueue = Volley.newRequestQueue(context)

    fun base64Upload(mRequest: MyRequest, callBack: OperationalCallBack): String? {
        val url = "${Constant.BASE_URL}${Constant.END_POINT}"
        val data = JSONObject()
        var message: String? = null

        data.put("userid", mRequest.userid)
        data.put("filename", mRequest.filename)
        data.put("base64", mRequest.base64)


        val request :JsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            url,
            data,
            Response.Listener<JSONObject> { response ->
                message = response.getString("message")
                Log.i("response on Success", message.toString())
                callBack.onSuccess(message.toString())
            },
            Response.ErrorListener{ error ->
                error.printStackTrace()
                message = error.message.toString()
                Log.i("response on fail", error.toString())
                callBack.onFailure(message.toString())
            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authkey", "asdf")
                return headers
            }

        }
        requestQueue.add(request)
            return message

    }

    companion object {
        const val TAG = "TAG"
    }

     fun saveMultipartImage(userId:String,filename:String,byteArray:ByteArray,callBack: OperationalCallBack) {
        // loading or check internet connection or something...
        // ... then
         val url = "https://6247-2401-4900-1c19-f760-1991-eaf8-af80-b250.ngrok-free.app/wappgo/index.php/?p=multipartfileupload"
        val multipartRequest: VolleyMultipartRequest = object : VolleyMultipartRequest(
            Method.POST, url,
            Response.Listener<NetworkResponse> { response ->
                val resultResponse =String(response.data)
                try {
                    Log.d("response..on success", "success.."+resultResponse)
                    callBack.onSuccess(resultResponse)
                    val result = JSONObject(resultResponse)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                val networkResponse = error.networkResponse
                var errorMessage = "Unknown error"
                if (networkResponse == null) {
                    if (error.javaClass == TimeoutError::class.java) {
                        errorMessage = "Request timeout"
                    } else if (error.javaClass == NoConnectionError::class.java) {
                        errorMessage = "Failed to connect server"
                    }
                } else {
                    val result = String(networkResponse.data)
                    try {

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found"
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = " Please login again"
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = " Check your inputs"
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = " Something is getting wrong"
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                Log.d("response..on Error", "Error.."+errorMessage)
                callBack.onFailure(errorMessage)
                error.printStackTrace()
            }) {
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = HashMap()
              //  params["api_token"] = "gh659gjhvdyudo973823tt9gvjf7i6ric75r76"
                params["userid"] = ""+userId
                params["filename"] = ""+filename
                return params
            }

            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                params["file"] = DataPart("file",byteArray)
/*
                params["cover"] = DataPart(
                    "file_cover.jpg",
                   " AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable())",
                    "image/jpeg"
                )
*/
                return params
            }
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authkey", "asdf")
                return headers
            }

        }
        requestQueue.add(multipartRequest)
    }

}
