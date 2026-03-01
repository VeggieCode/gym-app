package com.tlatoltech.gym.data.source.remote

import com.tlatoltech.gym.data.source.remote.dto.LaravelResponse
import com.tlatoltech.gym.data.source.remote.dto.RoutineDto
import retrofit2.http.Body
import retrofit2.http.POST

interface GymRoutineApiService {

    @POST("api/rutinas")
    suspend fun createRoutine(@Body routine: RoutineDto): LaravelResponse<RoutineDto>
}