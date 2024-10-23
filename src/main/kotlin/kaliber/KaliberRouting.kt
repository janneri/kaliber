package kaliber

import io.github.kaliber.kaliber.PathTemplate
import kaliber.Method.GET
import kaliber.Method.POST

class KaliberRouting {
    private val routes = mutableListOf<Route>()

    fun get(path: String, handler: (Exchange) -> Unit) {
        routes.add(Route(GET, PathTemplate(path), handler))
    }

    fun post(path: String, handler: (Exchange) -> Unit) {
        routes.add(Route(POST, PathTemplate(path), handler))
    }

    // Handle incoming requests and route them
    fun handle(exchange: Exchange) {
        val matchingRoute = routes.find { it.matches(exchange) }

        if (matchingRoute != null) {
            exchange.extractPathVariables(matchingRoute.path)
            matchingRoute.handler.invoke(exchange)
        } else {
            // If no route matches, return 404
            exchange.respondWithText("Route not found", 404)
        }
    }
}

fun routing(init: KaliberRouting.() -> Unit): KaliberRouting {
    val routes = KaliberRouting()
    routes.init()
    return routes
}