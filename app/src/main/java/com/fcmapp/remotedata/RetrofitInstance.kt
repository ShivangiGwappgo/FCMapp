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
           .baseUrl("https://9980-2401-4900-1c19-f760-c9b7-12a0-34f3-899b.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ProductInterface::class.java)
    }
    val api1 : ProductInterface by lazy {
        Retrofit.Builder()
           .baseUrl("https://7601-2401-4900-1c19-f760-2ca9-d5be-fd08-5799.ngrok-free.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductInterface::class.java)
    }

}