package com.lourenc.trolly.auth

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

fun createUserWithEmail(
    nome: String,
    sobrenome: String,
    email: String,
    senha: String,
    context: Context,
    navController: NavController
) {
    Firebase.auth.createUserWithEmailAndPassword(email, senha)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = Firebase.auth.currentUser
                user?.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName("$nome $sobrenome")
                        .build()
                )
                Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
            } else {
                val exception = task.exception
                val errorMessage = when (exception) {
                    is FirebaseAuthUserCollisionException -> "Esse email já está em uso."
                    is FirebaseAuthWeakPasswordException -> "A senha é muito fraca. Use 6 ou mais caracteres."
                    is FirebaseAuthInvalidCredentialsException -> "Email inválido. Verifique e tente novamente."
                    else -> "Erro: ${exception?.localizedMessage}"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
}

fun loginWithEmail(
    email: String,
    senha: String,
    context: Context,
    navController: NavController
) {
    Firebase.auth.signInWithEmailAndPassword(email, senha)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login feito com sucesso!", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
            } else {
                val errorMessage = when (val exception = task.exception) {
                    is FirebaseAuthInvalidUserException -> "Usuário não encontrado."
                    is FirebaseAuthInvalidCredentialsException -> "Email ou senha inválidos."
                    else -> "Erro: ${exception?.localizedMessage}"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
}
