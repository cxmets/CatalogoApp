package com.comets.catalogoapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform