package com.example.matchday.domain.model

import java.io.IOException

sealed class ClubSearchException(message: String, cause: Exception) : IOException(message, cause) {
    class Network(cause: IOException) :
        ClubSearchException("Could not reach the club catalog", cause)

    class Timeout(cause: IOException) :
        ClubSearchException("The club catalog request timed out", cause)

    class Http(val statusCode: Int, cause: Exception) :
        ClubSearchException("The club catalog returned HTTP $statusCode", cause)

    class InvalidResponse(cause: Exception) :
        ClubSearchException("Could not decode the club catalog", cause)
}
