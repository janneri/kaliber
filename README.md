# Kaliber

Kaliber is an experimental toy web framework written with Kotlin. 
It demonstrates language features of Kotlin, that may be useful for implementing such a framework.
It also demonstrates what is somewhat easy to implement and what is not.


## Key Features

1. Route Declaration: A simple and concise way to define routes (GET, POST, PUT, etc.).
2. Path Parameters: Support for dynamic path parameters (like /users/{id}).
3. Data binding (JSON): Convert http request bodies to Kotlin data classes with ease  

## Missing (key) features

1. Exception handling
2. Ability to configure serializers: use any json-lib, use xml, ...
3. Ready-made middleware for authentication, rate limiting, content negotiation, or response formatting
4. Validation: support for validation
5. OPEN API generation
6. Middleware: The ability to define middleware (e.g., authentication, logging, etc.) that applies to certain routes or groups of routes.

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

## Notes

### About http request and response

In Java, these are immutable. If we want to implement a chain of middleware, we probably need to 
wrap the request and response to mutable class(es) that is passed though the chain.

### Parameters in URL

There is roughly two ways to handle URL params:
Option 1. The URL params are annotated function parameters given to you handler function, which supports any amount of params
Option 2. The HTTP request (or HTTP exchange) is the only param

Spring Boot supports Option 1. The Node Express, Clojure Reitit, KTor way are closer to Option 2.

The framework must allow devs to read params as Strings. It may also assist you in reading the params to 
numeric types (e.g. Int, Long) or simple value types (e.g. UserId) that wrap primitive types like String, Int, Long, ...
Not supporting the string conversion introduces a lot of boilerplate to a typical web application.
We can argue, that the conversion support is magic, which is also something many developers hate.
The framework implementation needs to choose between two evils.

## OPEN API support

One aspect is supporting (or not supporting) OPEN API documentation generation. How do you list and describe the
URL parameters for the generator? In dynamic languages, it is typically done with schemas-definitions that come in many forms.
In statically typed languages, we typically want to use types. 

If and when we start customizing (json) serialization, how do we capture the customizations to a generated OPEN API spec

