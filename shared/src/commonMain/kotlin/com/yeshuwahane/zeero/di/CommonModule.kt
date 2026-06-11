package com.yeshuwahane.zeero.di

import com.russhwolf.settings.Settings
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
import com.yeshuwahane.zeero.data.db.DatabaseDriverFactory
import com.yeshuwahane.zeero.data.db.ZeeroDb
import com.yeshuwahane.zeero.data.db.ProductDao
import com.yeshuwahane.zeero.data.db.ProductDaoImpl
import com.yeshuwahane.zeero.data.db.RoomDatabase
import org.koin.dsl.module

import com.yeshuwahane.zeero.domain.usecase.RegisterUseCase
import com.yeshuwahane.zeero.domain.usecase.UploadProductImageUseCase
import com.yeshuwahane.zeero.domain.usecase.GetSettingsUserUseCase
import com.yeshuwahane.zeero.domain.usecase.LogoutUseCase
import com.yeshuwahane.zeero.domain.usecase.DeleteUserUseCase
import com.yeshuwahane.zeero.domain.usecase.UpdateUserUseCase
import com.yeshuwahane.zeero.domain.usecase.UpdateProductUseCase

val commonModule = module {
    // Settings & Database
    single { Settings() }
    single { DatabaseDriverFactory() }
    single { ZeeroDb(get<DatabaseDriverFactory>().createDriver()) }
    single { RoomDatabase(get()) }
    single<ProductDao> { ProductDaoImpl(get()) }

    // Repositories
    single<ProductRepository> { ProductRepositoryImpl(get(), get<ProductDao>(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }

    // Use Cases
    single { GetProductsUseCase(get()) }
    single { GetUsersUseCase(get()) }
    single { ApproveProductUseCase(get()) }
    single { RejectProductUseCase(get()) }
    single { LoginUseCase(get()) }
    single { GetProductByIdUseCase(get()) }
    single { PlaceBidUseCase(get()) }
    single { AddProductUseCase(get()) }
    single { RegisterUseCase(get()) }
    single { UploadProductImageUseCase(get()) }
    single { GetSettingsUserUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { DeleteUserUseCase(get()) }
    single { UpdateUserUseCase(get()) }
    single { UpdateProductUseCase(get()) }

    // ViewModels
    factory { LoginViewModel(get(), get()) }
    factory { MarketplaceViewModel(get()) }
    factory { ProductDetailViewModel(get(), get(), get()) }
    factory { SupplierViewModel(get(), get(), get(), get(), get(), get()) }
    factory { AdminViewModel(get(), get(), get(), get(), get(), get(), get()) }
}