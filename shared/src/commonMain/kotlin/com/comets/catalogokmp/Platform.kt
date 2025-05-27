package com.comets.catalogokmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform