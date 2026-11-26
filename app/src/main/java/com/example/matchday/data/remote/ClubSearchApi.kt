package com.example.matchday.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

const val OPENLIGA_BASE_URL = "https://api.openligadb.de/"
private const val JSON_MEDIA_TYPE = "application/json"

interface ClubSearchApi {
    @GET("getavailableteams/{league}/{season}")
    suspend fun listClubs(
        @Path("league") league: String,
        @Path("season") season: Int
    ): List<ClubDto>
}

fun createClubSearchApi(baseUrl: String = OPENLIGA_BASE_URL): ClubSearchApi {
    val json = Json { ignoreUnknownKeys = true }

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
        .build()
        .create(ClubSearchApi::class.java)
}
