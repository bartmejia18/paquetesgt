package com.encomiendas.encomiendas.data.repository

import com.encomiendas.encomiendas.data.api.ApiTrackingHelper
import com.encomiendas.encomiendas.data.model.Tracking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class TrackingRepository @Inject constructor(
    private val apiTrackingHelper: ApiTrackingHelper
) {
    suspend fun registration(tracking: Tracking): Response<Tracking> {
        return apiTrackingHelper.registration(tracking)
    }
}