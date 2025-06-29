package com.lourenc.trolly.utils

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ImageUploadManager {
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun uploadProfileImage(imageUri: Uri): String? {
        return try {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                Log.e("ImageUploadManager", "Usuário não autenticado")
                return null
            }
            
            Log.d("ImageUploadManager", "Iniciando upload para usuário: $userId")
            val imageFileName = "profile_images/$userId/${UUID.randomUUID()}.jpg"
            val imageRef = storage.reference.child(imageFileName)
            
            Log.d("ImageUploadManager", "Fazendo upload para: $imageFileName")
            // Upload da imagem
            val uploadTask = imageRef.putFile(imageUri).await()
            Log.d("ImageUploadManager", "Upload concluído: ${uploadTask.bytesTransferred} bytes")
            
            // Obter URL de download
            val downloadUrl = imageRef.downloadUrl.await()
            Log.d("ImageUploadManager", "URL obtida: $downloadUrl")
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("ImageUploadManager", "Erro no upload da imagem", e)
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUserProfilePhoto(imageUrl: String): Boolean {
        return try {
            val user = auth.currentUser
            if (user == null) {
                Log.e("ImageUploadManager", "Usuário não autenticado para atualizar foto")
                return false
            }
            
            Log.d("ImageUploadManager", "Atualizando foto do perfil para: $imageUrl")
            // Atualizar a foto do perfil no Firebase Auth
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(imageUrl))
                .build()
            
            user.updateProfile(profileUpdates).await()
            Log.d("ImageUploadManager", "Foto do perfil atualizada com sucesso")
            true
        } catch (e: Exception) {
            Log.e("ImageUploadManager", "Erro ao atualizar foto do perfil", e)
            e.printStackTrace()
            false
        }
    }
} 