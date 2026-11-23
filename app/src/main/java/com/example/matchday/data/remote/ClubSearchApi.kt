package com.example.matchday.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

internal interface ClubSearchApi {
    @GET("getavailableteams/{league}/{season}")
    suspend fun listClubs(
        @Path("league") league: String,
        @Path("season") season: Int
    ): List<ClubDto>
}
