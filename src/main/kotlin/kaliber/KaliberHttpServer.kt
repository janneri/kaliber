package kaliber

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class KaliberHttpServer(val port: Int, val rootContext: String, val routing: KaliberRouting) {
    fun start() {
        val server = HttpServer.create(InetSocketAddress(port), 0)

        server.createContext(rootContext) { exchange: HttpExchange ->
            routing.handle(Exchange(exchange))
        }

        server.start()
    }
}