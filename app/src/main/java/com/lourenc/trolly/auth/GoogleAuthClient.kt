package com.lourenc.trolly.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.lourenc.trolly.R

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val webClientId = "697988202593-ekjcc2g1j617njtlrbuuk4p4t258bmhj.apps.googleusercontent.com"
    Log.d("GoogleAuth", "Usando web client ID: $webClientId")
    
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(webClientId)
        .requestEmail()
        .requestProfile()
        .build()
    
    Log.d("GoogleAuth", "GoogleSignInOptions configurado com sucesso")
    return GoogleSignIn.getClient(context, gso)
}

fun firebaseAuthWithGoogle(
    idToken: String,
    context: Context,
    navController: NavController
) {
    Log.d("GoogleAuth", "Começando autenticação com token ID: ${idToken.take(10)}...")
    
    if (idToken.isEmpty()) {
        Log.e("GoogleAuth", "Token ID está vazio")
        Toast.makeText(context, "Erro: Token de autenticação inválido", Toast.LENGTH_LONG).show()
        return
    }
    
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    val auth = FirebaseAuth.getInstance()

    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                Log.d("GoogleAuth", "Login bem-sucedido para usuário: ${user?.displayName} (${user?.email})")
                Toast.makeText(context, "Bem-vindo, ${user?.displayName ?: user?.email}", Toast.LENGTH_SHORT).show()
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                val exception = task.exception
                Log.e("GoogleAuth", "Falha na autenticação", exception)
                
                val errorMessage = when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> "Credenciais inválidas. Tente novamente."
                    is FirebaseAuthUserCollisionException -> "Esta conta já está sendo usada com outro método de login."
                    is FirebaseAuthInvalidUserException -> "Usuário não encontrado."
                    else -> "Falha na autenticação: ${exception?.localizedMessage ?: "Erro desconhecido"}"
                }
                
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
        .addOnFailureListener { exception ->
            Log.e("GoogleAuth", "Falha na autenticação (OnFailure)", exception)
            Toast.makeText(context, "Erro na autenticação: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
        }
}
