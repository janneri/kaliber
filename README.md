# Kaliber

Kaliber is an experimental toy web framework written with Kotlin. 
It demonstrates language features of Kotlin, that may be useful for implementing such a framework.
It also demonstrates what is somewhat easy to implement and what is not.


## Key Features

1. Route Declaration: A simple and concise way to define routes (GET, POST, PUT, etc.).
2. Path Parameters: Support for dynamic path parameters (like /users/{id}).
3. Middleware: The ability to define middleware (e.g., authentication, logging, etc.) that applies to certain routes or groups of routes.
4. Data binding (JSON): Convert http request bodies to Kotlin data classes with ease  

## Missing (key) features

1. Exception handling
2. Ability to configure serializers: use any json-lib, use xml, ...
3. Ready-made middleware for authentication, rate limiting, content negotiation, or response formatting
4. Validation: support for validation

## Example application

A Kaliber-application could look like this. 
The `exchange`  encapsulates a HTTP request received and a response to be generated in one exchange. 

```
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
            val userId = exchange.getPathVariable("id", Long::class)

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
```
