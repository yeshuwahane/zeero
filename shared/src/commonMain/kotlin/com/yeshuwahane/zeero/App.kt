package com.yeshuwahane.zeero

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.yeshuwahane.zeero.di.appModule
import com.yeshuwahane.zeero.domain.model.UserRole
import com.yeshuwahane.zeero.domain.usecase.GetSettingsUserUseCase
import com.yeshuwahane.zeero.presentation.admin.AdminOverviewScreen
import com.yeshuwahane.zeero.presentation.login.LoginScreen
import com.yeshuwahane.zeero.presentation.marketplace.CustomerMarketplaceScreen
import com.yeshuwahane.zeero.presentation.supplier.SupplierDashboardScreen
import com.yeshuwahane.zeero.theme.ZeeroTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    KoinApplication(
        application = {
            modules(appModule())
        }
    ) {
        var startScreen by remember { mutableStateOf<Screen?>(null) }
        val getSettingsUserUseCase = koinInject<GetSettingsUserUseCase>()

        LaunchedEffect(Unit) {
            val user = getSettingsUserUseCase()
            if (user != null) {
                startScreen = when (user.role) {
                    UserRole.CUSTOMER -> CustomerMarketplaceScreen()
                    UserRole.SUPPLIER -> SupplierDashboardScreen()
                    UserRole.ADMIN -> AdminOverviewScreen()
                }
            } else {
                startScreen = LoginScreen()
            }
        }

        ZeeroTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                val screen = startScreen
                if (screen != null) {
                    Navigator(screen)
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}