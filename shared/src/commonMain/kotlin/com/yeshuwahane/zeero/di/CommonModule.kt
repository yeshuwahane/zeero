package com.yeshuwahane.zeero.di

import com.yeshuwahane.zeero.data.repository.ProductRepositoryImpl
import com.yeshuwahane.zeero.data.repository.UserRepositoryImpl
import com.yeshuwahane.zeero.domain.repository.ProductRepository
import com.yeshuwahane.zeero.domain.repository.UserRepository
import com.yeshuwahane.zeero.domain.usecase.AddProductUseCase
import com.yeshuwahane.zeero.domain.usecase.ApproveProductUseCase
import com.yeshuwahane.zeero.domain.usecase.GetProductByIdUseCase
import com.yeshuwahane.zeero.domain.usecase.GetProductsUseCase
import com.yeshuwahane.zeero.domain.usecase.GetUsersUseCase
import com.yeshuwahane.zeero.domain.usecase.LoginUseCase
import com.yeshuwahane.zeero.domain.usecase.PlaceBidUseCase
import com.yeshuwahane.zeero.domain.usecase.RejectProductUseCase
import com.yeshuwahane.zeero.presentation.admin.AdminViewModel
import com.yeshuwahane.zeero.presentation.detail.ProductDetailViewModel
import com.yeshuwahane.zeero.presentation.login.LoginViewModel
import com.yeshuwahane.zeero.presentation.marketplace.MarketplaceViewModel
import com.yeshuwahane.zeero.presentation.supplier.SupplierViewModel
import org.koin.dsl.module


val commonModule = module {
    // Repositories
    single<ProductRepository> { ProductRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl() }

    // Use Cases
    single { GetProductsUseCase(get()) }
    single { GetUsersUseCase(get()) }
    single { ApproveProductUseCase(get()) }
    single { RejectProductUseCase(get()) }
    single { LoginUseCase(get()) }
    single { GetProductByIdUseCase(get()) }
    single { PlaceBidUseCase(get()) }
    single { AddProductUseCase(get()) }

    // ViewModels
    factory { LoginViewModel(get()) }
    factory { MarketplaceViewModel(get()) }
    factory { ProductDetailViewModel(get(), get()) }
    factory { SupplierViewModel(get()) }
    factory { AdminViewModel(get(), get(), get(), get()) }
}