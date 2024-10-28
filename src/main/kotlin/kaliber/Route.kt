package kaliber

import io.github.kaliber.kaliber.PathTemplate
import io.github.kaliber.kaliber.openapi.PathVariable

enum class Method {
    GET, POST, PUT
}

interface ExchangeHandler {
    fun matches(exchange: Exchange): Boolean
}

class Route(val method: Method,
            var summary: String? = null,
            pathVariables: List<PathVariable<out Any>>? = null,
            val path: PathTemplate,
            val handler: (Exchange) -> Unit): ExchangeHandler {

    override fun matches(exchange: Exchange): Boolean {
        return exchange.requestMethod() == method && path.matches(exchange.requestPath())
    }

    fun extractPathVariables(exchange: Exchange): Map<String, String> =
        path.extractPathVariables(exchange.requestPath())
}