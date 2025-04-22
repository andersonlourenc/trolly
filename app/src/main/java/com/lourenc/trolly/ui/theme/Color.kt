package com.lourenc.trolly.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color


// 🎨 MODO CLARO

val BluePrimary = Color(0xFF246BFD)          // Azul médio elegante
val BluePrimaryVariant = Color(0xFF1A4FCC)   // Azul mais escuro
val BlueSecondary = Color(0xFFDDE9FF)        // Azul claro (destaque)
val BlueBackground = Color(0xFFF7F9FC)       // Fundo claro suave
val BlueSurface = Color(0xFFFFFFFF)          // Branco puro

val OnPrimary = Color(0xFFFFFFFF)            // Texto sobre azul


val OnSecondary = Color(0xFF1A4FCC)          // Texto sobre azul claro
val OnBackground = Color(0xFF1C1C1E)         // Azul grafiteado
val OnSurface = Color(0xFF1C1C1E)            // Azul escuro

val Success = Color(0xFF2ECC71)
val OnSuccess = Color(0xFFFFFFFF)


val Error = Color(0xFFE74C3C)
val OnError = Color(0xFFFFFFFF)

// 🌙 MODO ESCURO

val BluePrimaryDark = Color(0xFF246BFD)           // Mesmo azul
val BluePrimaryVariantDark = Color(0xFF123D9B)    // Azul ainda mais escuro
val BlueSecondaryDark = Color(0xFF2C3E50)         // Azul escuro suave
val BlueBackgroundDark = Color(0xFF0E1116)        // Fundo quase preto
val BlueSurfaceDark = Color(0xFF1A1D23)           // Cinza-azulado escuro

val OnPrimaryDark = Color(0xFFFFFFFF)
val OnSecondaryDark = Color(0xFFDDE9FF)
val OnBackgroundDark = Color(0xFFE2EAFB)
val OnSurfaceDark = Color(0xFFCBD6EA)

val SuccessDark = Color(0xFF27AE60)
val OnSuccessDark = Color(0xFFFFFFFF)
val ErrorDark = Color(0xFFC0392B)
val OnErrorDark = Color(0xFFFFFFFF)





data class ExtraColors(
    val success: Color
)

// Crie uma CompositionLocal para acesso
val LocalExtraColors = compositionLocalOf {
    ExtraColors(
        success = Success // fallback
    )
}
