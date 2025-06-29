package com.lourenc.trolly

import TermsAndPrivacyPolicyScreen

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
import com.lourenc.trolly.ui.screens.ForgotPasswordScreen
import com.lourenc.trolly.ui.screens.HomeScreen
import com.lourenc.trolly.ui.screens.InsightsScreen
import com.lourenc.trolly.ui.screens.LaunchScreen
import com.lourenc.trolly.ui.screens.ListaCompraDetailScreen
import com.lourenc.trolly.ui.screens.ListasScreen
import com.lourenc.trolly.ui.screens.LoginScreen
import com.lourenc.trolly.ui.screens.ProfileScreen
import com.lourenc.trolly.ui.screens.RegisterScreen
import com.lourenc.trolly.ui.screens.AddListsScreen
import com.lourenc.trolly.viewmodel.ListaCompraViewModel
import com.lourenc.trolly.viewmodel.ListaCompraViewModelFactory
import com.lourenc.trolly.ui.theme.TrollyTheme
import com.lourenc.trolly.ui.screens.EditItemScreen
import com.lourenc.trolly.ui.screens.AddProductScreen
import com.lourenc.trolly.ui.screens.EditProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    val factory = ListaCompraViewModelFactory(trollyApp.listaUseCase, trollyApp.itemUseCase)
    
    TrollyTheme(darkTheme = darkTheme) {
        NavHost(navController = navController, startDestination = "launch") {
            composable("launch") { LaunchScreen(navController) }
            composable("register") { RegisterScreen(navController, context) }
            composable("login") { LoginScreen(navController, context) }
            composable("home") { 
                val viewModel = viewModel<ListaCompraViewModel>(factory = factory)
                HomeScreen(navController, viewModel)
            }
            composable("forgot_password") { ForgotPasswordScreen(navController) }
            composable("terms_privacy") { TermsAndPrivacyPolicyScreen(navController = navController) }
            composable("listas") { 
                val viewModel = viewModel<ListaCompraViewModel>(factory = factory)
                ListasScreen(navController, viewModel)
            }
            composable("insights") {
                InsightsScreen(navController)
            }
            composable("listaDetail/{listaId}") { backStackEntry ->
                val viewModel = viewModel<ListaCompraViewModel>(factory = factory)
                val listaId = backStackEntry.arguments?.getString("listaId")?.toIntOrNull() ?: 0
                ListaCompraDetailScreen(navController, viewModel, listaId)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
            composable("addList") { 
                val viewModel = viewModel<ListaCompraViewModel>(factory = factory)
                AddListsScreen(navController, viewModel)
            }
            composable("editItem/{itemId}") { backStackEntry ->
                val viewModel = viewModel<ListaCompraViewModel>(factory = factory)
                val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull() ?: 0
                EditItemScreen(navController, viewModel, itemId)
            }
            composable("addProduct/{listaId}") { backStackEntry ->
                val viewModel = viewModel<ListaCompraViewModel>(factory = factory)
                val listaId = backStackEntry.arguments?.getString("listaId")?.toIntOrNull() ?: 0
                AddProductScreen(navController, viewModel, listaId)
            }
            composable("editProfile") {
                EditProfileScreen(navController)
            }
        }
    }
}
