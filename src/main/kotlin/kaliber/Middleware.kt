package io.github.kaliber.kaliber

import kaliber.Exchange
import kaliber.ExchangeHandler


class Middleware(val path: PathTemplate,
                 val handler: (Exchange) -> Unit): ExchangeHandler {
    override fun matches(exchange: Exchange): Boolean {
        return path.matches(exchange.requestPath(), allowPartialMatch = true)
    }
}