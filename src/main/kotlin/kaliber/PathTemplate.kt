package io.github.kaliber.kaliber


// A path that possibly contains path variables (e.g. /users/{id})
data class PathTemplate(val path: String) {
    private data class PathSegment(val value: String, val isPathVariable: Boolean)

    private val routeParts = splitSegments(path).map { segment ->
        val isPathVariable = isPathVariable(segment)
        val name = if (isPathVariable) segment.substring(1, segment.length - 1) else segment
        PathSegment(name, isPathVariable)
    }

    fun matches(requestPath: String, allowPartialMatch: Boolean = false): Boolean {
        val requestParts = splitSegments(requestPath)
        if (routeParts.size != requestParts.size && !allowPartialMatch) return false

        for (i in routeParts.indices) {
            if (routeParts[i].isPathVariable) continue
            if (routeParts[i].value != requestParts[i]) return false
        }
        return true
    }

    fun extractPathVariables(requestPath: String): Map<String, String> {
        val requestSegments = splitSegments(requestPath)

        val result = mutableMapOf<String, String>()
        routeParts.forEachIndexed { i, segment ->
            if (segment.isPathVariable) {
                result[segment.value] = requestSegments[i]
            }
        }

        return result.toMap()
    }

    private fun splitSegments(requestPath: String) =
        requestPath.split("/").filter { it.isNotEmpty() }

    private fun isPathVariable(urlPart: String): Boolean =
        urlPart.startsWith("{") && urlPart.endsWith("}")
}