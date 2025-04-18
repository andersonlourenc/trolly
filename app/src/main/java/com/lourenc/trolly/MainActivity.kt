package com.lourenc.trolly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lourenc.trolly.splash.SplashScreen
import com.lourenc.trolly.ui.theme.TrollyTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            setContent {
                TrollyTheme {
                    SplashScreen(
                        onLoginClick = { /* TODO: Navegar para tela de login */ },
                        onRegisterClick = { /* TODO: Navegar para tela de registro */ }
                    )
                }
            }

        }

    }


}