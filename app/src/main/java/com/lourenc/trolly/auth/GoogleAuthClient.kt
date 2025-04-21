package com.lourenc.trolly.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser

fun getGoogleSignInClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("741353373161-9bh6d8me3nq3knrmo1couq94ui2ioeid.apps.googleusercontent.com")
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

fun firebaseAuthWithGoogle(idToken: String, context: Context, navController: NavController) {
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    val auth = FirebaseAuth.getInstance()

    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                Toast.makeText(context, "Bem-vindo, ${user?.displayName}", Toast.LENGTH_SHORT).show()
                // Você pode navegar para a próxima tela aqui, se quiser
                navController.navigate("home")
            } else {
                Toast.makeText(context, "Falha na autenticação", Toast.LENGTH_SHORT).show()
            }
        }
}
