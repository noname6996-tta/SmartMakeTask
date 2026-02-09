package com.tta.smartmaketask

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform