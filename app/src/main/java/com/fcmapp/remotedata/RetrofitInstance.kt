package com.fcmapp


import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    var gson = GsonBuilder()
        .setLenient()
        .create()

   /* var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()*/

    val api : ProductInterface by lazy {
        Retrofit.Builder()
           .baseUrl("https://6247-2401-4900-1c19-f760-1991-eaf8-af80-b250.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ProductInterface::class.java)
    }

}