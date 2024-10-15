package com.encomiendas.encomiendas.data.services

import com.encomiendas.encomiendas.data.model.Tracking
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface TrackingService {
    @POST("registration")
    suspend fun registration(
        @Body tracking: Tracking
    ): Response<Tracking>
}