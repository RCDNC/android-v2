package com.rcdnc.cafezinho

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform