package io.github.kaliber

import kaliber.KaliberHttpServer
import kaliber.routing

fun main() {
    data class User(val id: Long, val name: String, val age: Int)
    val users = mutableListOf(User(111, "Hello User", 24))

    val routing = routing {
        get("hello") { exchange ->
            exchange.respondWithText("Hello, World!")
        }

        get("user") { exchange ->
            exchange.respondWithJson(users)
        }

        get("user/{id}") { exchange ->
            val userId = exchange.pathVariable("id", Long::class)

            val user = users.find { it.id == userId }
            if (user == null) {
                exchange.respondWithText("User not found", 404)
            }
            else {
                exchange.respondWithJson(user)
            }
        }

        post("user") { exchange ->
            val user = exchange.parseRequestJson<User>()
            users.add(user)
            println("Received $user")
            exchange.respondWithJson(user)
        }
    }

    val server = KaliberHttpServer(8080, "/", routing)
    server.start()
}