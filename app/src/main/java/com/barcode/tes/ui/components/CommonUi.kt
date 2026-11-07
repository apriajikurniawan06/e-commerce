package com.barcode.tes.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.barcode.tes.ui.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Transaksi", fontSize = 10.sp) },
            selected = currentRoute == Screen.Dashboard.route,
            onClick = { 
                if (currentRoute != Screen.Dashboard.route) {
                    navController.navigate(Screen.Dashboard.route) 
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Belanja") },
            label = { Text("Belanja", fontSize = 10.sp) },
            selected = currentRoute == Screen.ProductList.route,
            onClick = { 
                if (currentRoute != Screen.ProductList.route) {
                    navController.navigate(Screen.ProductList.route) 
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.History, contentDescription = "Riwayat") },
            label = { Text("Riwayat", fontSize = 10.sp) },
            selected = currentRoute == "riwayat", // This could be improved
            onClick = { 
                navController.navigate(Screen.Transaction.createRoute("History"))
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 10.sp) },
            selected = currentRoute == Screen.Profile.route,
            onClick = { 
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route) 
                }
            }
        )
    }
}
