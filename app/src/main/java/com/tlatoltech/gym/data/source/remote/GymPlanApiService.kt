package com.tlatoltech.gym.data.source.remote

import com.tlatoltech.gym.data.source.remote.dto.LaravelResponse
import com.tlatoltech.gym.data.source.remote.dto.GymPlanDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GymPlanApiService {
    @GET("api/planes") // Ajusta esto si en Laravel le pusiste el prefijo api/ (ej. "api/planes")
    suspend fun getActivePlans(): LaravelResponse<List<GymPlanDto>>

    @POST("api/planes")
    suspend fun createPlan(@Body plan: GymPlanDto): LaravelResponse<GymPlanDto>

    @PATCH("api/planes/{id}/archivar")
    suspend fun archivePlan(@Path("id") id: Int): LaravelResponse<Any>
}