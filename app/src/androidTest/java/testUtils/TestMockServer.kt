package testUtils

import okhttp3.mockwebserver.MockWebServer

/**
 * Обёртка для MockWebServer, позволяющая явно управлять его жизненным циклом.
 */
class TestMockServer {
    val server = MockWebServer()

    fun start() {
        server.start()
    }

    fun shutdown() {
        server.shutdown()
    }
}
