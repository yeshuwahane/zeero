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

// Data layer Repositories
import com.yeshuwahane.zeero.data.repository.ProductRepositoryImpl
import com.yeshuwahane.zeero.data.repository.UserRepositoryImpl
import com.yeshuwahane.zeero.domain.repository.ProductRepository
import com.yeshuwahane.zeero.domain.repository.UserRepository

// Domain layer Use Cases
import com.yeshuwahane.zeero.domain.usecase.AddProductUseCase
import com.yeshuwahane.zeero.domain.usecase.ApproveProductUseCase
import com.yeshuwahane.zeero.domain.usecase.GetProductByIdUseCase
import com.yeshuwahane.zeero.domain.usecase.GetProductsUseCase
import com.yeshuwahane.zeero.domain.usecase.GetUsersUseCase
import com.yeshuwahane.zeero.domain.usecase.LoginUseCase
import com.yeshuwahane.zeero.domain.usecase.PlaceBidUseCase
import com.yeshuwahane.zeero.domain.usecase.RejectProductUseCase

// Presentation layer ViewModels
import com.yeshuwahane.zeero.presentation.login.LoginViewModel
import com.yeshuwahane.zeero.presentation.marketplace.MarketplaceViewModel
import com.yeshuwahane.zeero.presentation.detail.ProductDetailViewModel
import com.yeshuwahane.zeero.presentation.supplier.SupplierViewModel
import com.yeshuwahane.zeero.presentation.admin.AdminViewModel


fun appModule() = listOf(networkModule, commonModule)
