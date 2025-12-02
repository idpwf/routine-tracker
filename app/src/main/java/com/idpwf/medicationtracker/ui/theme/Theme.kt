package com.idpwf.medicationtracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Define the color scheme for the application.
// We are using a dark color scheme by default.
private val AppColorScheme = darkColorScheme(
    primary = SkyBlue,       // A pleasant accent for primary actions
    background = SlateGray,    // The main background color
    surface = SteelBlue,       // Color for surfaces like Cards and Buttons
    onPrimary = SlateGray,
    onBackground = OffWhite,
    onSurface = OffWhite,
    secondary = MutedGray,
    onSecondary = OffWhite
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
