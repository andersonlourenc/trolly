package com.lourenc.trolly.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// Light
val BluePrimary = Color(0xFF246BFD)
val BluePrimaryVariant = Color(0xFF1A4FCC)
val BlueSecondary = Color(0xFFDDE9FF)
val BlueBackground = Color(0xFFF7F9FC)
val BlueSurface = Color(0xFFFFFFFF)
val BlueOnPrimary = Color(0xFFFFFFFF)
val BlueOnSecondary = Color(0xFF1A4FCC)
val BlueOnBackground = Color(0xFF1C1C1E)
val BlueOnSurface = Color(0xFF1C1C1E)

// Dark
val DarkBluePrimaryVariant = Color(0xFF123D9B)
val DarkBlueSecondary = Color(0xFF2C3E50)
val DarkBlueBackground = Color(0xFF0E1116)
val DarkBlueSurface = Color(0xFF1A1D23)
val DarkBlueOnBackground = Color(0xFFE2EAFB)
val DarkBlueOnSurface = Color(0xFFCBD6EA)

// ✅ Feedback
val SuccessGreen = Color(0xFF2ECC71)
val SuccessGreenVariant = Color(0xFF27AE60) // Dark
val OnSuccess = Color(0xFFFFFFFF)

val ErrorRed = Color(0xFFE74C3C)
val ErrorRedVariant = Color(0xFFC0392B) // Dark
val OnError = Color(0xFFFFFFFF)


data class ExtraColors(
    val success: Color
)

// Crie uma CompositionLocal para acesso
val LocalExtraColors = compositionLocalOf {
    ExtraColors(
        success = SuccessGreen // fallback
    )
}
