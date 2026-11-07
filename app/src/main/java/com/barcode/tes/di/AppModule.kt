package com.barcode.tes.di

import com.barcode.tes.data.repository.*
import com.barcode.tes.domain.repository.*
import com.barcode.tes.domain.usecase.auth.*
import com.barcode.tes.domain.usecase.product.*
import com.barcode.tes.domain.usecase.banner.*
import com.barcode.tes.domain.usecase.order.*
import com.barcode.tes.domain.usecase.address.*

import com.barcode.tes.TesApplication
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object AppModule {
    // Database & DAOs
    private val database get() = TesApplication.database
    private val profileDao get() = database.profileDao()
    private val productDao get() = database.productDao()
    private val categoryDao get() = database.categoryDao()
    private val orderDao get() = database.orderDao()
    private val bannerDao get() = database.bannerDao()
    private val addressDao get() = database.addressDao()

    private val client: HttpClient by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }

    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(profileDao) }
    val productRepository: ProductRepository by lazy { ProductRepositoryImpl(productDao, categoryDao) }
    val orderRepository: OrderRepository by lazy { OrderRepositoryImpl(orderDao) }
    val bannerRepository: BannerRepository by lazy { BannerRepositoryImpl(bannerDao) }
    val addressRepository: AddressRepository by lazy { AddressRepositoryImpl(addressDao) }
    val ppobRepository: PpobRepository by lazy { PpobRepositoryImpl(client) }

    // Auth Use Cases
    val authUseCases: AuthUseCases by lazy {
        AuthUseCases(
            login = LoginUseCase(authRepository),
            signUp = SignUpUseCase(authRepository),
            logout = LogoutUseCase(authRepository),
            getProfile = GetProfileUseCase(authRepository),
            updateProfile = UpdateProfileUseCase(authRepository),
            uploadAvatar = UploadAvatarUseCase(authRepository),
            deleteAccount = DeleteAccountUseCase(authRepository)
        )
    }

    // Product Use Cases
    val productUseCases: ProductUseCases by lazy {
        ProductUseCases(
            getProducts = GetProductsUseCase(productRepository),
            getCategories = GetCategoriesUseCase(productRepository),
            addProduct = AddProductUseCase(productRepository),
            updateProduct = UpdateProductUseCase(productRepository),
            deleteProduct = DeleteProductUseCase(productRepository),
            uploadImage = UploadImageUseCase(productRepository)
        )
    }

    // Banner Use Cases
    val bannerUseCases: BannerUseCases by lazy {
        BannerUseCases(
            getBanners = GetBannersUseCase(bannerRepository),
            addBanner = AddBannerUseCase(bannerRepository),
            uploadBannerImage = UploadBannerImageUseCase(bannerRepository)
        )
    }

    // Order Use Cases
    val orderUseCases: OrderUseCases by lazy {
        OrderUseCases(
            getOrders = GetOrdersUseCase(orderRepository),
            createOrder = CreateOrderUseCase(orderRepository)
        )
    }

    // Address Use Cases
    val addressUseCases: AddressUseCases by lazy {
        AddressUseCases(
            getAddresses = GetAddressesUseCase(addressRepository),
            getDefaultAddress = GetDefaultAddressUseCase(addressRepository),
            addAddress = AddAddressUseCase(addressRepository),
            updateAddress = UpdateAddressUseCase(addressRepository),
            deleteAddress = DeleteAddressUseCase(addressRepository)
        )
    }
}
