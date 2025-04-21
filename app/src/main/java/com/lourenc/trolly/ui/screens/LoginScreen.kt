package com.lourenc.trolly.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lourenc.trolly.R
import com.lourenc.trolly.auth.loginWithEmail
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.lourenc.trolly.auth.firebaseAuthWithGoogle



@Composable

fun LoginScreen(navController: NavController, context: Context) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }

    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("741353373161-9bh6d8me3nq3knrmo1couq94ui2ioeid.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.result
            val idToken = account.idToken
            if (idToken != null) {
                firebaseAuthWithGoogle(idToken, context, navController)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_color_textblack), // substitua por seu logo real
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )




        Text("Login", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))


        // Email
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(8.dp))

        // Senha
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Senha") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible.value)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
        )

        // Esqueceu senha
        TextButton(onClick = { navController.navigate("forgot_password") },
            modifier = Modifier.align(Alignment.Start),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )) {
            Text("Esqueceu a senha?")

        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botão Entrar
        Button(
            onClick = {
                loginWithEmail(emailState.value, passwordState.value, context, navController)
            },
            modifier = Modifier.fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Text("Entrar")
        }



        DividerWithText()


        // Botão Google
        Button(
            onClick = {
                val signInIntent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            },

            modifier = Modifier.fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
        ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        )  {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google logo",
                    modifier = Modifier
                        .size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Entrar com Google")
            }

    }

}


@Composable
fun DividerWithText(text: String = "Ou") {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.secondaryContainer
        )
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Divider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.secondaryContainer
        )
    }
}
