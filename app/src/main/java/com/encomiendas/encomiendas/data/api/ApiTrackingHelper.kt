package com.encomiendas.encomiendas.data.api

import com.encomiendas.encomiendas.data.model.Tracking
import retrofit2.Response

interface ApiTrackingHelper {
    suspend fun registration(tracking: Tracking): Response<Tracking>
}