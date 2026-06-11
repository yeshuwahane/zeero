package com.yeshuwahane.zeero.data.utils

import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.plugins.ResponseException

suspend inline fun <reified T : Any> apiCall(apiCall: () -> HttpResponse): DataResource<T> {
    return try {
        val response: HttpResponse = apiCall.invoke()

        if (response.status.value in 200..299) {
            val responseBody = response.body<T>()
            DataResource.success(responseBody)
        } else {
            val errorText = try {
                response.bodyAsText()
            } catch (e: Exception) {
                response.status.description
            }
            DataResource.error(error = Throwable(errorText), data = null)
        }
    } catch (exception: Exception) {
        Napier.d("exception: ${exception.message}")
        val errorText = try {
            if (exception is ResponseException) {
                exception.response.bodyAsText()
            } else {
                exception.message ?: "Unknown error occurred"
            }
        } catch (e: Exception) {
            exception.message ?: "Unknown error occurred"
        }
        DataResource.error(error = Throwable(errorText), data = null)
    }
}

