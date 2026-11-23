package com.example.matchday.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

internal interface ClubSearchApi {
    @GET("teams")
    suspend fun searchClubs(
        @Query("search") query: String,
        @Header("x-apisports-key") apiKey: String
    ): ClubSearchResponseDto
}
