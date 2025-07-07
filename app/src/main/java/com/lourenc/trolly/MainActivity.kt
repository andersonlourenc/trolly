package com.lourenc.trolly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lourenc.trolly.presentation.screens.*
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModel
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModelFactory
import com.lourenc.trolly.domain.usecase.BulkListUseCase
import com.lourenc.trolly.presentation.theme.TrollyTheme
import androidx.core.view.WindowCompat
import android.os.Build
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.lourenc.trolly.presentation.screens.TermsAndPrivacyPolicyScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        setContent {
            AppNavigator()
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val darkTheme = isSystemInDarkTheme()
    val trollyApp = context.applicationContext as TrollyApp
    
    // Log para debug
    android.util.Log.d("MainActivity", "AppNavigator - Iniciando")
    
    val factory = ShoppingListViewModelFactory(trollyApp.shoppingListUseCase, trollyApp.listItemUseCase)
    
    android.util.Log.d("MainActivity", "AppNavigator - Factory criada com sucesso")
    
    TrollyTheme(darkTheme = darkTheme) {
        NavHost(navController = navController, startDestination = "launch") {
            composable("launch") { LaunchScreen(navController) }
            composable("register") { RegisterScreen(navController, context) }
            composable("login") { LoginScreen(navController, context) }
            composable("home") { 
                val viewModel = viewModel<ShoppingListViewModel>(factory = factory)
                HomeScreen(navController, viewModel)
            }
            composable("forgot_password") { ForgotPasswordScreen(navController) }
            composable("terms_privacy") { TermsAndPrivacyPolicyScreen(navController = navController) }
            composable("shoppingLists") { 
                val viewModel = viewModel<ShoppingListViewModel>(factory = factory)
                ShoppingListsScreen(navController, viewModel)
            }
            composable("insights") {
                val viewModel = viewModel<ShoppingListViewModel>(factory = factory)
                InsightsScreen(navController, viewModel)
            }
            composable("shoppingListDetail/{listId}") { backStackEntry ->
                val viewModel = viewModel<ShoppingListViewModel>(factory = factory)
                val listId = backStackEntry.arguments?.getString("listId")?.toIntOrNull() ?: 0
                ShoppingListDetailScreen(navController, viewModel, listId)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
            composable("addList") { 
                val viewModel = viewModel<ShoppingListViewModel>(factory = factory)
                AddShoppingListScreen(navController, viewModel)
            }
            composable("editItem/{itemId}") { backStackEntry ->
                val viewModel = viewModel<ShoppingListViewModel>(factory = factory)
                val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
                EditItemScreen(navController, viewModel, itemId)
            }
            composable("addProduct/{listId}") { backStackEntry ->
                val viewModel = viewModel<ShoppingListViewModel>(factory = factory)
                val listId = backStackEntry.arguments?.getString("listId")?.toIntOrNull() ?: 0
                AddProductScreen(navController, viewModel, listId)
            }
            composable("editProfile") {
                EditProfileScreen(navController)
            }
            composable("bulkList") {
                val trollyApp = context.applicationContext as TrollyApp
                BulkListScreen(navController, trollyApp.bulkListUseCase)
            }
        }
    }
}
