package com.example.matchday.data.remote

import com.example.matchday.domain.model.Club
import com.example.matchday.domain.model.ClubSearchException
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress
import java.util.concurrent.ConcurrentLinkedQueue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ClubSearchHttpTest {
    @Test
    fun sendsCatalogRequestsAndDecodesActualJsonThroughRetrofit() = runBlocking {
        val paths = ConcurrentLinkedQueue<String>()
        withCatalogServer(
            handle = { request ->
                val path = request.requestURI.path
                paths.add(path)
                val body = if (path.endsWith("/ucl/2025")) {
                    """[{"teamId":7082,"teamName":"FK Kairat","shortName":"Kairat Almaty","tla":"KAI"}]"""
                } else {
                    "[]"
                }
                request.respond(200, body)
            }
        ) { repository ->
            assertEquals(
                listOf(Club("7082", "FK Kairat", null)),
                repository.search("almaty")
            )
        }

        assertEquals(
            setOf(
                "/getavailableteams/pl/2026",
                "/getavailableteams/bl1/2026",
                "/getavailableteams/ucl/2025",
                "/getavailableteams/uel2026/2026"
            ),
            paths.toSet()
        )
    }

    @Test
    fun passesThroughServerFailureFromAnHttpResponse() = runBlocking {
        withCatalogServer(handle = { request -> request.respond(503, "{}") }) { repository ->
            val failure = runCatching { repository.search("kairat") }.exceptionOrNull()
            assertEquals(503, (failure as? ClubSearchException.Http)?.statusCode)
        }
    }

    private suspend fun withCatalogServer(
        handle: (HttpExchange) -> Unit,
        test: suspend (RemoteClubSearchRepository) -> Unit
    ) {
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/getavailableteams/") { exchange ->
            exchange.use { handle(it) }
        }
        server.start()
        try {
            val api = createClubSearchApi("http://127.0.0.1:${server.address.port}/")
            test(RemoteClubSearchRepository(api))
        } finally {
            server.stop(0)
        }
    }

    private fun HttpExchange.respond(status: Int, json: String) {
        val body = json.toByteArray(Charsets.UTF_8)
        responseHeaders.set("Content-Type", "application/json")
        sendResponseHeaders(status, body.size.toLong())
        responseBody.use { it.write(body) }
    }
}
