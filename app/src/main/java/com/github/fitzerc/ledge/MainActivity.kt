package com.github.fitzerc.ledge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.ui.models.navparams.BookNavParam
import com.github.fitzerc.ledge.ui.models.navparams.SearchNavParam
import com.github.fitzerc.ledge.ui.screens.BookViewScreen
import com.github.fitzerc.ledge.ui.screens.BooksScreen
import com.github.fitzerc.ledge.ui.screens.HomeScreen
import com.github.fitzerc.ledge.ui.screens.settings.ManageSeriesScreen
import com.github.fitzerc.ledge.ui.screens.settings.SettingsScreen
import com.github.fitzerc.ledge.ui.theme.LedgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val db = LedgeDatabase.getDatabase(applicationContext)
            
            LedgeTheme(dynamicColor = false) {
                MainScreen(ledgeDb = db)
            }
        }
    }
}

@Composable
fun MainScreen(ledgeDb: LedgeDatabase) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        BottomNavItem("Home", Icons.Default.Home),
        BottomNavItem("Books", Icons.AutoMirrored.Default.MenuBook),
        BottomNavItem("Settings", Icons.Default.Menu)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(navItems, selectedItem, navController) { newIndex ->
            selectedItem = newIndex
        }
        },) { paddingInner ->
        NavHost(navController, startDestination = "home") {
            composable("home") {
                selectedItem = navItems.indexOfFirst { it.label == "Home" }
                HomeScreen(navController, ledgeDb, paddingInner)
            }
            composable("settings") {
                selectedItem = navItems.indexOfFirst { it.label == "Settings" }
                SettingsScreen(navController, ledgeDb)
            }
            composable<SearchNavParam> { backStackEntry ->
                selectedItem = navItems.indexOfFirst { it.label == "Books" }
                val searchNavParam: SearchNavParam = backStackEntry.toRoute()
                BooksScreen(
                    innerPadding = paddingInner,
                    navController = navController,
                    searchNavParam = searchNavParam,
                    ledgeDb = ledgeDb)
            }
            composable<BookNavParam> { navBackStackEntry ->
                val bookNavParam: BookNavParam = navBackStackEntry.toRoute()
                selectedItem = -1
                BookViewScreen(bookNavParam = bookNavParam, ledgeDb, navController)
            }
            composable("manageseries") {
                selectedItem = -1
                ManageSeriesScreen(
                    innerPadding = paddingInner,
                    ledgeDb = ledgeDb
                )
            }
        }
    }
}

@Composable fun BottomNavBar(items: List<BottomNavItem>, selectedItem: Int, navController: NavController, onSelectedItemChange: (Int) -> Unit) {

    NavigationBar {
        items.forEachIndexed { index, item -> NavigationBarItem (
            icon = { Icon(item.icon, contentDescription = item.label) },
            label = { Text(item.label) },
            selected = selectedItem == index,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            onClick = {
                onSelectedItemChange(index)
                if (items[index].label.lowercase() == "books") {
                    navController.navigate(SearchNavParam(""))
                } else {
                    navController.navigate(items[index].label.lowercase())
                }
            } )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector)