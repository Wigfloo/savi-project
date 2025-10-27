package com.savi.shared.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    fun create(baseUrl: String): SaviApi =
        Retrofit.Builder()
            .baseUrl(baseUrl) // ejemplo: http://10.0.2.2:8000 para backend local
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SaviApi::class.java)
}
