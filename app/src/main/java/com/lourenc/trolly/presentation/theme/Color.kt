package com.lourenc.trolly.presentation.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// Cores principais (comuns aos temas claro e escuro)
val CoralPrimary = Color(0xFFF77564)
val CoralVariant = Color(0xFFFFA091) // Tom mais claro da primária
val PeachSecondary = Color(0xFFF8D082)
val PeachSecondaryVariant = Color(0xFFFFE3AC) // Variante mais clara
val SandBackground = Color(0xFFFAFAFA)
val TextDark = Color(0xFF111111)
val TextLight = Color(0xFFFFFFFF)
val Error = Color(0xFFE74C3C)

val CoralPrimaryDark = CoralPrimary // Usar mesma primária clara no dark
val CoralVariantDark = CoralVariant // Variante clara também
val PeachSecondaryDark = PeachSecondary
val PeachSecondaryVariantDark = PeachSecondaryVariant
val DarkBackground = Color(0xFF1C1C1E)
val DarkSurface = Color(0xFF2C2C2E)
val ErrorDark = Color(0xFFC0392B)

// Mantendo o que não existe no novo padrão
val Success = Color(0xFF2ECC71)
val OnSuccess = Color(0xFFFFFFFF)
val SuccessDark = Color(0xFF27AE60)
val OnSuccessDark = Color(0xFFFFFFFF)
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
