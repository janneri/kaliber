package kaliber

import io.github.kaliber.kaliber.PathTemplate

enum class Method {
    GET, POST, PUT
}

class Route(val method: Method, val path: PathTemplate, val handler: (Exchange) -> Unit) {
    fun matches(exchange: Exchange): Boolean {
        return exchange.requestMethod() == method && path.matches(exchange.requestPath())
    }

    fun extractPathVariables(exchange: Exchange): Map<String, String> =
        path.extractPathVariables(exchange.requestPath())
}