package com.rcdnc.cafezinho.utils

expect class Platform() {
    val name: String
}

fun getPlatform(): Platform = Platform()