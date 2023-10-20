package com.fcmapp

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ProductInterface {
  /*  @GET("Fresh_Fetch_Partner_ProductList_By_SubCatId?")
    fun getProducts(@Query ("PartnerId") pid:String, @Query ("CatId") catid:String, @Query ("SubCatId") subcatid:String ) : Call<ProductsData>
*/
    @POST("wappgo/index.php/")
    fun uploadBase64File(@Header("Authkey") header:String, @Query("p") param:String, @Body request: MyRequest) : Call<FileResponse>

    @POST("location/location.php")
    fun uploadLocation(@Header("Authkey") header:String, @Query("p") param:String , @Body request: MyLocationRequest) :
            Call<FileResponse>

    @Multipart
    @POST("wappgo/index.php/")
    fun uploadUserFile(
        @Header("Authkey") header:String, @Query("p") param:String,
        @Part("userid") userid: RequestBody?,
        @Part("filename") filename: RequestBody?,
        @Part  file: MultipartBody.Part?
    ): Call<FileResponse>

}

