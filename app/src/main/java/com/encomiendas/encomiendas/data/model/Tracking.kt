package com.encomiendas.encomiendas.data.model

import com.google.gson.annotations.SerializedName

data class Tracking(
    @SerializedName("_id")
    val id: String? = null,
    val file: String? = null,
    @SerializedName("tracking_id")
    val trackingId: String,
    val description: String,
    @SerializedName("group_id")
    val groupId: String,
    @SerializedName("client_id")
    val clientId: String,
    val photo: String? = null,
    val delivered: Boolean,
)
