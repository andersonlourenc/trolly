package com.lourenc.trolly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lourenc.trolly.splash.SplashScreen
import com.lourenc.trolly.ui.login.LoginScreen

import com.lourenc.trolly.ui.theme.TrollyTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setContent {
                val navController: NavHostController = rememberNavController()

                TrollyTheme {
                     NavHost(
                         navController = navController,
                         startDestination = "splash"
                     ) {
                        composable("splash") {
                            SplashScreen(
                                onLoginClick = { navController.navigate("login") },
                                onRegisterClick = { navController.navigate("register")}
                                    )
                                }
                         composable("login") {
                             LoginScreen(
                                 println("Login feito")
                             )
                         }
                     }

                }
            }

        }

    }

}
