package com.idpwf.medicationtracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Define the color scheme for the application.
// We are using a dark color scheme by default.
private val AppColorScheme = darkColorScheme(
    background = Background,
    onBackground = OnBackground,

    secondaryContainer = Secondary,
    onSecondaryContainer = OnSecondary,
)

@Composable
fun MedicationTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
