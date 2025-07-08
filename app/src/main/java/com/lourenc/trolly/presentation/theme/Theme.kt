package com.lourenc.trolly.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Shapes

import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = CoralPrimary,
    onPrimary = TextLight,
    primaryContainer = CoralVariant,
    secondary = PeachSecondary,
    secondaryContainer = PeachSecondaryVariant,
    onSecondaryContainer = WhiteCards,
    onSecondary = TextDark,
    background = SandBackground,
    onBackground = TextDark,
    surface = Color.White,
    onSurface = TextDark,
    error = Error,
    onError = TextLight
)

private val DarkColors = darkColorScheme(
    primary = CoralPrimaryDark,
    onPrimary = TextLight,
    primaryContainer = CoralVariantDark,
    secondary = PeachSecondaryDark,
    onSecondary = TextDark,
    background = DarkBackground,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    error = ErrorDark,
    onError = TextLight
)

@Composable
fun TrollyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            shapes = Shapes(
                small = RoundedCornerShape(4.dp),
                medium = RoundedCornerShape(8.dp),
                large = RoundedCornerShape(12.dp)
            ),
            content = content
        )
}