package com.example.matchday.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

internal interface ClubSearchApi {
    @GET("getavailableteams/{league}/{season}")
    suspend fun listClubs(
        @Path("league") league: String,
        @Path("season") season: Int
    ): List<ClubDto>
}

internal fun createClubSearchApi(baseUrl: String = "https://api.openligadb.de/"): ClubSearchApi {
    val json = Json { ignoreUnknownKeys = true }

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ClubSearchApi::class.java)
}
