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
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CameraAlt
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
import com.lourenc.trolly.ui.theme.*
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
            TrollyTopBar(
                title = "Editar Perfil",
                subtitle = "Atualize suas informações",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = TrollySpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(TrollySpacing.xl))
            
            // Avatar com overlay e ícone de câmera centralizado
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUri),
                        contentDescription = "Nova foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else if (initialPhotoUrl != null) {
                    AsyncImage(
                        model = initialPhotoUrl,
                        contentDescription = "Foto do usuário",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                // Overlay escuro mais transparente
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "Editar foto",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(TrollySpacing.lg))
            
            // Campo de nome com suporte a caracteres especiais
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    Log.d("EditProfile", "Nome alterado: $it")
                    name = it 
                },
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                shape = TrollyShapes.medium,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            )
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            
            // Campo de email (apenas leitura)
            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                shape = TrollyShapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            )
            
            Spacer(modifier = Modifier.height(TrollySpacing.xl))
            
            // Botão salvar
            TrollyPrimaryButton(
                text = if (isUploading) "Salvando..." else "Salvar Alterações",
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
                            var uploadError = false
                            
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
                                        uploadError = true
                                        showError = true
                                        errorMessage = "Erro ao atualizar foto do perfil"
                                        Log.e("EditProfile", "Erro ao atualizar foto do perfil")
                                    }
                                } else {
                                    uploadError = true
                                    showError = true
                                    errorMessage = "Erro ao fazer upload da imagem"
                                    Log.e("EditProfile", "Erro no upload da imagem")
                                }
                            }
                            
                            // Atualizar nome se necessário (apenas se não houve erro no upload)
                            if (!uploadError && name != initialName && name.isNotBlank()) {
                                Log.d("EditProfile", "Atualizando nome...")
                                val success = imageUploadManager.updateUserProfileName(name)
                                if (success) {
                                    hasChanges = true
                                    Log.d("EditProfile", "Nome atualizado com sucesso")
                                } else {
                                    showError = true
                                    errorMessage = "Erro ao atualizar nome"
                                    Log.e("EditProfile", "Erro ao atualizar nome")
                                }
                            }
                            
                            if (hasChanges && !showError) {
                                showSuccess = true
                                Log.d("EditProfile", "Perfil atualizado com sucesso")
                            } else if (!hasChanges && !showError) {
                                showError = true
                                errorMessage = "Nenhuma alteração foi feita"
                                Log.d("EditProfile", "Nenhuma alteração foi feita")
                            }
                            
                        } catch (e: Exception) {
                            showError = true
                            errorMessage = "Erro inesperado: ${e.message}"
                            Log.e("EditProfile", "Erro inesperado", e)
                            e.printStackTrace()
                        } finally {
                            isUploading = false
                        }
                    }
                },
                enabled = !isUploading
            )
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            
            // Botão cancelar
            TrollySecondaryButton(
                text = "Cancelar",
                onClick = { navController.popBackStack() },
                enabled = !isUploading
            )
            
            Spacer(modifier = Modifier.height(TrollySpacing.xl))
        }
    }
    
    // Alertas
    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Erro") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showError = false }) {
                    Text("OK")
                }
            }
        )
    }
    
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { 
                showSuccess = false
                navController.popBackStack()
            },
            title = { Text("Sucesso") },
            text = { Text("Perfil atualizado com sucesso!") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showSuccess = false
                        navController.popBackStack()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
} 