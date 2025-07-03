package com.lourenc.trolly.auth

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

fun createUserWithEmail(
    name: String,
    email: String,
    password: String,
    context: Context,
    navController: NavController
) {
    Firebase.auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = Firebase.auth.currentUser
                user?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                )
                Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
            } else {
                val exception = task.exception
                val errorMessage = when (exception) {
                    is FirebaseAuthUserCollisionException -> "That email is already in use."
                    is FirebaseAuthWeakPasswordException -> "The password is too weak. Use 6 or more characters."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email. Please check and try again."
                    else -> "Error: ${exception?.localizedMessage}"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
}

fun loginWithEmail(
    email: String,
    password: String,
    context: Context,
    navController: NavController
) {
    Firebase.auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
            } else {
                val errorMessage = when (val exception = task.exception) {
                    is FirebaseAuthInvalidUserException -> "User not found."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
                    else -> "Error: ${exception?.localizedMessage}"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
}
