package com.tlatoltech.gym.data.source.remote.dto

data class LaravelResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)
