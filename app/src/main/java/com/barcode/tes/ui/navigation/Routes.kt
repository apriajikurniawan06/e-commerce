package com.barcode.tes.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object ProductList : Screen("product_list")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object AddProduct : Screen("add_product")
    object EditProduct : Screen("edit_product/{productId}") {
        fun createRoute(productId: String) = "edit_product/$productId"
    }
    object Profile : Screen("profile")
    object MyProducts : Screen("my_products")
    object MyBanners : Screen("my_banners")
    object SalesHistory : Screen("sales_history")
    object Transaction : Screen("transaction/{type}") {
        fun createRoute(type: String) = "transaction/$type"
    }
    object Pulsa : Screen("pulsa")
    object Pdam : Screen("pdam")
    object Pln : Screen("pln")
    object Flight : Screen("flight")
    object Train : Screen("train")
    object Ship : Screen("ship")
    object Bus : Screen("bus")
    object Payment : Screen("payment/{amount}/{capital}/{desc}") {
        fun createRoute(amount: Double, capital: Double, desc: String) = "payment/$amount/$capital/$desc"
    }
    object Receipt : Screen("receipt/{transactionId}") {
        fun createRoute(transactionId: String) = "receipt/$transactionId"
    }
    object konten : Screen("konten")
    object ShortVideo : Screen("short_video")
}
