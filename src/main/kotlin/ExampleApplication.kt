package io.github.kaliber

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import io.github.kaliber.kaliber.RouteNotFoundException
import io.github.kaliber.kaliber.openapi.PathVariable
import kaliber.KaliberHttpServer
import kaliber.routing

fun main() {
    data class MyError(val errorText: String)
    class ValidationException : RuntimeException()

    data class UserId(val id: Long) {
        @JsonValue fun toValue(): Long = id
        constructor(idString: String): this(idString.toLong())
    }
    data class User(val id: UserId, val name: String, val age: Int)
    val users = mutableListOf(User(UserId(111), "Hello User", 24))

    val routing = routing {

        middleware("hello") { exchange ->
            exchange.getRequestHeaders()?.forEach { header -> println(header) }
        }

        get("hello") { exchange ->
            exchange.respondWithText("Hello, World!")
        }

        // Simulate error throwing from application logic / request handler
        get("error") { _ ->
            throw ValidationException()
        }

        get(path = "user",
            summary = "Get all users") { exchange ->
            exchange.respondWithJson(users)
        }

        get(path = "user/{id}",
            summary = "Get user by id",
            pathVariables = listOf(PathVariable("id", UserId::class))) { exchange ->

            val userId = exchange.getPathVariable<UserId>("id")

            val user = users.find { it.id == userId }
            if (user == null) {
                exchange.respondWithJson(MyError("User not found"), 404)
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

        middleware("/") { exchange ->
            if (exchange.error != null) {
                when (exchange.error) {
                    is ValidationException -> { exchange.respondWithJson(MyError("Validation failed"), 400) }
                    is RouteNotFoundException -> { exchange.respondWithJson(MyError("Unknown route"), 404) }
                    is InvalidFormatException -> {
                        println(exchange.error)
                        exchange.respondWithJson(MyError("Invalid JSON"), 400)
                    }
                    else -> {
                        println(exchange.error)
                        exchange.respondWithJson(MyError("Something went wrong"), 500)
                    }
                }
            }
        }
    }

    val server = KaliberHttpServer(8080, "/", routing)
    server.start()
}


