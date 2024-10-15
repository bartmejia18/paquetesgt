package com.encomiendas.encomiendas.data.api

import com.encomiendas.encomiendas.data.model.Tracking
import com.encomiendas.encomiendas.data.services.TrackingService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class ApiTrackingHelperImpl @Inject constructor(
    private val trackingService: TrackingService
) : ApiTrackingHelper {

    override suspend fun registration(
        tracking: Tracking
    ): Response<Tracking> = trackingService.registration(tracking)
}