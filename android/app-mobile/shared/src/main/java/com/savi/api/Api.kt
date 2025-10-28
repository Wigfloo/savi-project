package com.savi.shared.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class Measurement(val ts: Long, val steps: Int?, val hr: Int?)
data class IngestPayload(val user_id: String, val items: List<Measurement>)
data class IngestResponse(val received: Int)

interface SaviApi {
    @GET("health")
    suspend fun health(): Map<String, Any>

    @POST("v1/ingest")
    suspend fun ingest(@Body payload: IngestPayload): IngestResponse
}
