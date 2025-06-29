package com.lourenc.trolly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("profile") { inclusive = true }
            }
        }
        return
    }
    val nomeCompleto = user.displayName ?: "Usuário"
    val email = user.email ?: ""
    val photoUrl = user.photoUrl?.toString()

    Scaffold(
        // Remova o topBar se quiser igual à imagem, ou mantenha se preferir
        bottomBar = {
            // Se quiser manter a navegação inferior, descomente abaixo
            // NavigationBar { ... }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            // Avatar
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Foto do usuário",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Nome
            Text(nomeCompleto, style = MaterialTheme.typography.titleLarge)
            // Email
            Text(email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            // Botão editar perfil
            Button(
                onClick = { navController.navigate("editProfile") },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Editar perfil", color = Color.White)
            }
            Spacer(modifier = Modifier.height(32.dp))
            // Card de preferências (apenas logout)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                ListItem(
                    headlineContent = { Text("Logout", color = Color.Red) },
                    leadingContent = {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
                    },
                    modifier = Modifier.clickable {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("launch") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
} 