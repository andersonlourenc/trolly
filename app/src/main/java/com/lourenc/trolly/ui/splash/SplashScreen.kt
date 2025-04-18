package com.lourenc.trolly.splash


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.lourenc.trolly.R
import androidx.compose.ui.unit.dp


@Composable
fun SplashScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Fundo opcional
        Image(
            painter = painterResource(id = R.drawable.image_background),
            contentDescription = "image background",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Crop

        )
        Box(
            modifier = Modifier.fillMaxSize().
            background(Color.Black.copy(alpha = 0.6f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "Logo Trolly",
                modifier = Modifier.size(160.dp)
            )


            Spacer(modifier = Modifier.height(48.dp))

            // Botões
            Button(

                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth().
                height(40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )

            ) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White
                )

            ) {
                Text("Log in")
            }
        }
    }
}