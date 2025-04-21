package com.lourenc.trolly.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp



private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightTextPrimary,
    onSecondary = LightTextSecondary,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    error = LightError,
    onError = Color.White,

)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkTextPrimary,
    onSecondary = DarkTextSecondary,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    error = DarkError,
    onError = Color.Black,


)



@Composable
fun TrollyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    val extraColors = if (darkTheme) {
        ExtraColors(success = DarkSuccess)
    } else {
        ExtraColors(success = LightSuccess)
    }

    CompositionLocalProvider(LocalExtraColors provides extraColors) {
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

}