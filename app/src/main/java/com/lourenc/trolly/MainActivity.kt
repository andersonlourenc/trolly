package com.lourenc.trolly

import TermsAndPrivacyPolicyScreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lourenc.trolly.ui.screens.ForgotPasswordScreen
import com.lourenc.trolly.ui.screens.HomeScreen
import com.lourenc.trolly.ui.screens.LaunchScreen
import com.lourenc.trolly.ui.screens.LoginScreen
import com.lourenc.trolly.ui.screens.RegisterScreen

import com.lourenc.trolly.ui.theme.TrollyTheme

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
    TrollyTheme (darkTheme = darkTheme){
        NavHost(navController = navController, startDestination = "launch") {
            composable("launch") { LaunchScreen(navController) }
            composable("register") { RegisterScreen(navController, context) }
            composable("login") { LoginScreen(navController, context) }
            composable("home") { HomeScreen() }
            composable("forgot_password") { ForgotPasswordScreen(navController) }
            composable("terms_privacy") { TermsAndPrivacyPolicyScreen(navController = navController)
            }




        }
    }
}
