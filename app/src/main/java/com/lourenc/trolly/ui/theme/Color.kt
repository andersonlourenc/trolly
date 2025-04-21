package com.lourenc.trolly.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color


// ====== Light Theme ======
val LightPrimary = Color(0xFF4E6EF2)
val LightSecondary = Color(0xFFFFB836)
val LightBackground = Color(0xFFF9FAFB)
val LightSurface = Color(0xFFFFFFFF)
val LightTextPrimary = Color(0xFF1C1C1E)
val LightTextSecondary = Color(0xFF6C6C70)
val LightSuccess = Color(0xFF22C55E)
val LightError = Color(0xFFEF4444)


// ====== Dark Theme ======
val DarkPrimary = Color(0xFF8E9CFF)
val DarkSecondary = Color(0xFFFFD173)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkTextPrimary = Color(0xFFF5F5F5)
val DarkTextSecondary = Color(0xFFAAAAAA)
val DarkSuccess = Color(0xFF4ADE80)
val DarkError = Color(0xFFF87171)

data class ExtraColors(
    val success: Color
)

// Crie uma CompositionLocal para acesso
val LocalExtraColors = compositionLocalOf {
    ExtraColors(
        success = LightSuccess // fallback
    )
}
