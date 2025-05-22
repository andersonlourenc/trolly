package com.lourenc.trolly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lourenc.trolly.R
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import com.lourenc.trolly.auth.firebaseAuthWithGoogle
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LaunchScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    // Verifica se já existe um usuário logado
    LaunchedEffect(Unit) {
        if (auth.currentUser != null) {
            navController.navigate("home") {
                popUpTo("launch") { inclusive = true }
            }
            return@LaunchedEffect
        }
    }

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

    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier

                        .size(180.dp)
                )
            }

            Column(
                modifier = Modifier

                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = {
                        navController.navigate("login")
                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                        .height(48.dp)
                        .shadow(elevation = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "E-mail",
                            modifier = Modifier
                                .padding(start = 0.dp)
                                .size(24.dp)
                        )
                        Text(
                            "Entrar com o E-mail",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )

                    }
                }
                Button(
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    },

                    modifier = Modifier.fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),

                    ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Google logo",
                            modifier = Modifier
                                .padding(start = 0.dp)
                                .size(24.dp)
                        )

                        Text(
                            "Entrar com o Google",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )

                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.facebook),
                            contentDescription = "Facebook logo",
                            modifier = Modifier
                                .padding(start = 0.dp)
                                .size(24.dp)
                        )

                        Text(
                            "Entrar com o Facebook",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }



            }
        }
    }
}
