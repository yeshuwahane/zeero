package com.yeshuwahane.zeero.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val context = appContext ?: throw IllegalStateException("Android Context not initialized for DatabaseDriverFactory")
        return AndroidSqliteDriver(ZeeroDb.Schema, context, "zeero.db")
    }

    companion object {
        var appContext: Context? = null
    }
}
