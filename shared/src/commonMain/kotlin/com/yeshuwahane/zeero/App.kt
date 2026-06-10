package com.yeshuwahane.zeero

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import com.yeshuwahane.zeero.di.appModule
import com.yeshuwahane.zeero.presentation.login.LoginScreen
import com.yeshuwahane.zeero.theme.ZeeroTheme
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(appModule())
        }
    ) {
        ZeeroTheme {
            Navigator(LoginScreen())
        }
    }
}