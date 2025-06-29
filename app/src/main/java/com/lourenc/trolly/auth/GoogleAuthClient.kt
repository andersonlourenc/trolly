 package com.lourenc.trolly.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.lourenc.trolly.R

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val webClientId = "697988202593-ekjcc2g1j617njtlrbuuk4p4t258bmhj.apps.googleusercontent.com"
    Log.d("GoogleAuth", "Usando web client ID: $webClientId")
    
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(webClientId)
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

fun firebaseAuthWithGoogle(
    idToken: String,
    context: Context,
    navController: NavController
) {
    Log.d("GoogleAuth", "Começando autenticação com token ID: ${idToken.take(10)}...")
    
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    val auth = FirebaseAuth.getInstance()

    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                Log.d("GoogleAuth", "Login bem-sucedido para usuário: ${user?.displayName}")
                Toast.makeText(context, "Bem-vindo, ${user?.displayName}", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
            } else {
                val exception = task.exception
                Log.e("GoogleAuth", "Falha na autenticação", exception)
                Toast.makeText(context, "Falha na autenticação: ${exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
