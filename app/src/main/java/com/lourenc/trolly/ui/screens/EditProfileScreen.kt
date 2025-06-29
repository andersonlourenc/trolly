package com.lourenc.trolly.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.lourenc.trolly.utils.ImageUploadManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imageUploadManager = remember { ImageUploadManager() }
    
    val initialName = user?.displayName ?: "Usuário"
    val email = user?.email ?: ""
    val initialPhotoUrl = user?.photoUrl?.toString()

    var name by remember { mutableStateOf(initialName) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            Log.d("EditProfile", "Imagem selecionada: $uri")
            photoUri = uri
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
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
            // Avatar com lápis
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.BottomEnd
            ) {
                if (photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUri),
                        contentDescription = "Nova foto de perfil",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else if (initialPhotoUrl != null) {
                    AsyncImage(
                        model = initialPhotoUrl,
                        contentDescription = "Foto do usuário",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                // Ícone de lápis pequeno e clicável
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar foto",
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Campo de nome com suporte a caracteres especiais
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    Log.d("EditProfile", "Nome alterado: $it")
                    name = it 
                },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Campo de email (apenas leitura)
            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botão salvar
            Button(
                onClick = {
                    Log.d("EditProfile", "Iniciando salvamento...")
                    Log.d("EditProfile", "Nome atual: $name, Nome inicial: $initialName")
                    Log.d("EditProfile", "Foto selecionada: $photoUri")
                    
                    scope.launch {
                        isUploading = true
                        showError = false
                        showSuccess = false
                        
                        try {
                            var hasChanges = false
                            
                            // Se há uma nova foto, fazer upload
                            if (photoUri != null) {
                                Log.d("EditProfile", "Fazendo upload da foto...")
                                val imageUrl = imageUploadManager.uploadProfileImage(photoUri!!)
                                if (imageUrl != null) {
                                    Log.d("EditProfile", "Upload bem-sucedido: $imageUrl")
                                    val success = imageUploadManager.updateUserProfilePhoto(imageUrl)
                                    if (success) {
                                        hasChanges = true
                                        Log.d("EditProfile", "Foto do perfil atualizada com sucesso")
                                    } else {
                                        showError = true
                                        errorMessage = "Erro ao atualizar foto do perfil"
                                        Log.e("EditProfile", "Erro ao atualizar foto do perfil")
                                    }
                                } else {
                                    showError = true
                                    errorMessage = "Erro ao fazer upload da imagem"
                                    Log.e("EditProfile", "Erro no upload da imagem")
                                }
                            }
                            
                            // Atualizar nome se necessário
                            if (name != initialName && name.isNotBlank()) {
                                Log.d("EditProfile", "Atualizando nome para: $name")
                                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()
                                user?.updateProfile(profileUpdates)?.await()
                                hasChanges = true
                                Log.d("EditProfile", "Nome atualizado com sucesso")
                            }
                            
                            if (!showError) {
                                if (hasChanges) {
                                    showSuccess = true
                                    Log.d("EditProfile", "Alterações salvas com sucesso")
                                    // Aguardar um pouco para mostrar sucesso
                                    kotlinx.coroutines.delay(1000)
                                }
                                navController.popBackStack()
                            }
                        } catch (e: Exception) {
                            showError = true
                            errorMessage = "Erro ao salvar alterações: ${e.message}"
                            Log.e("EditProfile", "Erro ao salvar alterações", e)
                        } finally {
                            isUploading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isUploading) "Salvando..." else "Salvar")
            }
            
            // Mensagem de erro
            if (showError) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Mensagem de sucesso
            if (showSuccess) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Alterações salvas com sucesso!",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 