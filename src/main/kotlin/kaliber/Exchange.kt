package kaliber

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import io.github.kaliber.kaliber.PathTemplate
import java.io.OutputStream
import kotlin.reflect.KClass

// todo enable configuration
val objectMapper = jacksonObjectMapper()


class Exchange(val exchange: HttpExchange, var error: Throwable? = null)  {
    private val pathVariables = mutableMapOf<String, String>()

    inline fun <reified T> parseRequestJson(): T {
        return objectMapper.readValue(exchange.requestBody.readAllBytes().toString(Charsets.UTF_8), T::class.java)
    }

    fun getRequestHeaders(): Headers? = exchange.requestHeaders

    fun pathVariable(name: String): String? = pathVariables[name]

    inline fun <reified T : Any> getPathVariable(name: String): T? {
        return pathVariable(name, T::class)
    }

    fun <T : Any> pathVariable(name: String, targetType: KClass<T>): T? {
        val value = pathVariables[name]
        return when (targetType) {
            Int::class -> value?.toIntOrNull() as T?
            Long::class -> value?.toLongOrNull() as T?
            Double::class -> value?.toDoubleOrNull() as T?
            Float::class -> value?.toFloatOrNull() as T?
            else -> return try {
                // Find the primary constructor that takes a String parameter
                val constructor = targetType.constructors.firstOrNull {
                    it.parameters.size == 1 && it.parameters[0].type.classifier == String::class
                }
                constructor?.call(value)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun extractPathVariables(pathTemplate: PathTemplate) {
        val pathVariables = pathTemplate.extractPathVariables(requestPath())
        this.pathVariables.putAll(pathVariables)
    }

    fun respondWithJson(data: Any, statusCode: Int = 200) {
        val response = try {
            objectMapper.writeValueAsString(data)
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
        exchange.responseHeaders.add("Content-Type", "application/json")
        exchange.sendResponseHeaders(statusCode, response.toByteArray().size.toLong())
        exchange.responseBody.use { os: OutputStream ->
            os.write(response.toByteArray())
        }
    }

    fun respondWithText(content: String, statusCode: Int = 200) {
        exchange.responseHeaders.add("Content-Type", "text/plain")
        exchange.sendResponseHeaders(statusCode, content.toByteArray().size.toLong())
        exchange.responseBody.use { os: OutputStream ->
            os.write(content.toByteArray())
        }
    }

    fun status(code: Int) {
        exchange.sendResponseHeaders(code, 0)
    }

    fun requestMethod(): Method = Method.valueOf(exchange.requestMethod)
    fun requestPath(): String = exchange.requestURI.path

}