package com.yeshuwahane.zeero

import kotlinx.datetime.Clock

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getCurrentTimeMillis(): Long