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
    primary = BluePrimary,
    onPrimary = BlueOnPrimary,
    primaryContainer = BluePrimaryVariant,

    secondary = BlueSecondary,
    onSecondary = BlueOnSecondary,

    background = BlueBackground,
    onBackground = BlueOnBackground,

    surface = BlueSurface,
    onSurface = BlueOnSurface,

    error = ErrorRed,
    onError = OnError
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    onPrimary = BlueOnPrimary,
    primaryContainer = DarkBluePrimaryVariant,

    secondary = DarkBlueSecondary,
    onSecondary = BlueSecondary,

    background = DarkBlueBackground,
    onBackground = DarkBlueOnBackground,

    surface = DarkBlueSurface,
    onSurface = DarkBlueOnSurface,

    error = ErrorRedVariant,
    onError = OnError
)



@Composable
fun TrollyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    val extraColors = if (darkTheme) {
        ExtraColors(success = SuccessGreenVariant)
    } else {
        ExtraColors(success = SuccessGreen)
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