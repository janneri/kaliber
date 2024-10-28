package io.github.kaliber.kaliber.openapi

import kotlin.reflect.KClass

data class PathVariable<T: Any> (val name: String, val type: KClass<T>)