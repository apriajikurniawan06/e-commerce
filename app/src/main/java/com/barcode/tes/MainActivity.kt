package com.barcode.tes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.barcode.tes.data.remote.SupabaseClient
import com.barcode.tes.ui.navigation.Screen
import com.barcode.tes.ui.screens.*
import com.barcode.tes.ui.theme.TesTheme
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle Deep Link saat app baru dibuka
        intent?.let {
            SupabaseClient.client.handleDeeplinks(it)
        }

        setContent {
            var isDarkTheme by remember { mutableStateOf(value = false) }
            TesTheme(darkTheme = isDarkTheme) {
                MainAppContent(
                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle Deep Link saat app sudah terbuka (singleTop)
        SupabaseClient.client.handleDeeplinks(intent)
    }
}

@Composable
fun MainAppContent(onThemeToggle: () -> Unit, isDarkTheme: Boolean) {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                onThemeToggle = onThemeToggle,
                isDarkTheme = isDarkTheme
            )
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                onThemeToggle = onThemeToggle,
                isDarkTheme = isDarkTheme
            )
        }
        composable(route = Screen.ProductList.route) {
            ProductScreen(
                navController = navController,
                onThemeToggle = onThemeToggle,
                isDarkTheme = isDarkTheme
            )
        }
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument(name = "productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailScreen(
                productId = productId,
                navController = navController
            )
        }
        composable(route = Screen.AddProduct.route) {
            AddProductScreen(navController = navController)
        }
        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            EditProductScreen(productId, navController)
        }
        composable(route = Screen.MyProducts.route) {
            MyProductsScreen(navController = navController)
        }
        composable(route = Screen.MyBanners.route) {
            MyBannersScreen(navController = navController)
        }
        composable(route = Screen.Pulsa.route) {
            PulsaScreen(navController = navController)
        }
        composable(route = Screen.Pdam.route) {
            PdamScreen(navController = navController)
        }
        composable(route = Screen.Pln.route) {
            PlnScreen(navController = navController)
        }
        composable(route = Screen.Flight.route) {
            FlightScreen(navController = navController)
        }
        composable(route = Screen.Train.route) {
            TrainScreen(navController = navController)
        }
        composable(route = Screen.Ship.route) {
            ShipScreen(navController = navController)
        }
        composable(route = Screen.Bus.route) {
            BusScreen(navController = navController)
        }
        composable(route = Screen.SalesHistory.route) {
            SalesHistoryScreen(navController = navController)
        }
        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("amount") { type = NavType.FloatType },
                navArgument("capital") { type = NavType.FloatType },
                navArgument("desc") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val amount = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
            val capital = backStackEntry.arguments?.getFloat("capital")?.toDouble() ?: 0.0
            val desc = backStackEntry.arguments?.getString("desc") ?: ""
            PaymentScreen(amount, capital, desc, navController)
        }
        composable(
            route = Screen.Receipt.route,
            arguments = listOf(
                navArgument("transactionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
            ReceiptScreen(transactionId, navController)
        }
        composable(
            route = Screen.Transaction.route,
            arguments = listOf(
                navArgument(name = "type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: ""
            TransactionScreen(type = type, navController = navController)
        }
    }
}
