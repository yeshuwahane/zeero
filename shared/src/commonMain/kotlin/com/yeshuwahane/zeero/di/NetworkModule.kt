package com.yeshuwahane.zeero.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module


import io.ktor.client.plugins.defaultRequest
import com.yeshuwahane.zeero.getPlatform

val networkModule = module {
    single {
        HttpClient {
            defaultRequest {
                url("https://zeeroapi-production.up.railway.app")
            }
            install(ContentNegotiation) {
                json(Json {
                    explicitNulls = false
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.d("Ktor: $message")
                    }
                }
                level = LogLevel.ALL
            }.also { Napier.base(DebugAntilog()) }

            install(HttpTimeout) {
                socketTimeoutMillis = 10_000
                requestTimeoutMillis = 100_000
            }
        }
    }
}
