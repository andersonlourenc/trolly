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
                Log.e("ImageUploadManager", "User not authenticated")
                return null
            }
            
            Log.d("ImageUploadManager", "Starting upload for user: $userId")
            Log.d("ImageUploadManager", "Image URI: $imageUri")
            
            val imageFileName = "profile_images/$userId/${UUID.randomUUID()}.jpg"
            val imageRef = storage.reference.child(imageFileName)
            
            Log.d("ImageUploadManager", "Uploading to: $imageFileName")
            
            // Upload the image
            val uploadTask = imageRef.putFile(imageUri).await()
            Log.d("ImageUploadManager", "Upload completed: ${uploadTask.bytesTransferred} bytes")
            
            // Get download URL
            val downloadUrl = imageRef.downloadUrl.await()
            Log.d("ImageUploadManager", "URL obtained: $downloadUrl")
            
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("ImageUploadManager", "Error in image upload", e)
            Log.e("ImageUploadManager", "Error message: ${e.message}")
            Log.e("ImageUploadManager", "Cause: ${e.cause}")
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUserProfilePhoto(imageUrl: String): Boolean {
        return try {
            val user = auth.currentUser
            if (user == null) {
                Log.e("ImageUploadManager", "User not authenticated to update profile photo")
                return false
            }
            
            Log.d("ImageUploadManager", "Updating profile photo for: $imageUrl")
            Log.d("ImageUploadManager", "Current user: ${user.uid}")
            
            // Update profile photo in Firebase Auth
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(imageUrl))
                .build()
            
            Log.d("ImageUploadManager", "Executing updateProfile...")
            user.updateProfile(profileUpdates).await()
            
            // Verify if the update was successful
            val updatedUser = auth.currentUser
            Log.d("ImageUploadManager", "Profile photo updated successfully")
            Log.d("ImageUploadManager", "New photo URL: ${updatedUser?.photoUrl}")
            
            true
        } catch (e: Exception) {
            Log.e("ImageUploadManager", "Error updating profile photo", e)
            Log.e("ImageUploadManager", "Error message: ${e.message}")
            Log.e("ImageUploadManager", "Cause: ${e.cause}")
            e.printStackTrace()
            false
        }
    }

    suspend fun updateUserProfileName(name: String): Boolean {
        return try {
            val user = auth.currentUser
            if (user == null) {
                Log.e("ImageUploadManager", "User not authenticated to update profile name")
                return false
            }
            
            Log.d("ImageUploadManager", "Updating profile name for: $name")
            Log.d("ImageUploadManager", "Current user: ${user.uid}")
            
            // Update profile name in Firebase Auth
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            
            Log.d("ImageUploadManager", "Executing updateProfile...")
            user.updateProfile(profileUpdates).await()
            
            // Verify if the update was successful
            val updatedUser = auth.currentUser
            Log.d("ImageUploadManager", "Profile name updated successfully")
            Log.d("ImageUploadManager", "New name: ${updatedUser?.displayName}")
            
            true
        } catch (e: Exception) {
            Log.e("ImageUploadManager", "Error updating profile name", e)
            Log.e("ImageUploadManager", "Error message: ${e.message}")
            Log.e("ImageUploadManager", "Cause: ${e.cause}")
            e.printStackTrace()
            false
        }
    }
} 