package kaliber

import io.github.kaliber.kaliber.Middleware
import io.github.kaliber.kaliber.PathTemplate
import io.github.kaliber.kaliber.RouteNotFoundException
import io.github.kaliber.kaliber.openapi.PathVariable
import kaliber.Method.GET
import kaliber.Method.POST


class KaliberRouting {
    private val handlers = mutableListOf<ExchangeHandler>()

    fun get(
        path: String,
        summary: String? = null,
        pathVariables: List<PathVariable<out Any>>? = null,
        handler: (Exchange) -> Unit
    ) {
        handlers.add(Route(GET, summary, pathVariables, PathTemplate(path), handler))
    }

    fun post(
        path: String,
        summary: String? = null,
        pathVariables: List<PathVariable<Any>>? = null,
        handler: (Exchange) -> Unit
    ) {
        handlers.add(Route(POST, summary, pathVariables, PathTemplate(path), handler))
    }

    fun middleware(
        path: String,
        handler: (Exchange) -> Unit
    ) {
        handlers.add(Middleware(PathTemplate(path), handler))
    }

    // Handle incoming requests and route them
    fun handle(exchange: Exchange) {
        val matchingRoutes = handlers.filter { it.matches(exchange) }

        if (matchingRoutes.filterIsInstance<Route>().isEmpty()) {
            exchange.error = RouteNotFoundException()
        }

        for (route in matchingRoutes) {
            if (route is Middleware) {
                route.handler.invoke(exchange)
            }
            if (route is Route && exchange.error == null) {
                exchange.extractPathVariables(route.path)
                try {
                    route.handler.invoke(exchange)
                } catch (e: Throwable) {
                    exchange.error = e
                }
            }
        }
    }
}

fun routing(init: KaliberRouting.() -> Unit): KaliberRouting {
    val routes = KaliberRouting()
    routes.init()
    return routes
}