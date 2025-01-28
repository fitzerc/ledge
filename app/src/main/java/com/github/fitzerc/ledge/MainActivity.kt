package com.github.fitzerc.ledge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.fitzerc.ledge.data.LedgeDatabase
import com.github.fitzerc.ledge.ui.screens.BooksScreen
import com.github.fitzerc.ledge.ui.screens.HomeScreen
import com.github.fitzerc.ledge.ui.screens.SettingsScreen
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
    var searchQuery: TextFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(navController) },) { paddingInner ->
        NavHost(navController, startDestination = "home") {
            composable("home") {
                HomeScreen(navController, ledgeDb, paddingInner)
            }
            composable("books") { BooksScreen(navController, Modifier.padding(paddingInner)) }
            composable("settings") { SettingsScreen(navController, Modifier.padding(paddingInner)) }
        }
    }
}

@Composable fun BottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home),
        BottomNavItem("Books", Icons.AutoMirrored.Default.MenuBook),
        BottomNavItem("Settings", Icons.Default.Menu)
    )

    var selectedItem by remember { mutableIntStateOf(0) }
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
                selectedItem = index
                navController.navigate(items[index].label.lowercase())
            } )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector)