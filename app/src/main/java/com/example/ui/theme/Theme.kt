package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val YapeLightColorScheme = lightColorScheme(
  primary = YapePurple,
  onPrimary = YapeWhite,
  primaryContainer = YapePurpleLight,
  onPrimaryContainer = YapePurpleDark,
  secondary = YapeTurquoise,
  onSecondary = YapeWhite,
  secondaryContainer = YapeTurquoiseLight,
  onSecondaryContainer = YapeTurquoiseDark,
  tertiary = YapeMagenta,
  onTertiary = YapeWhite,
  tertiaryContainer = YapeMagentaLight,
  onTertiaryContainer = YapeMagenta,
  background = YapeTrayBackground,
  onBackground = YapeTextPrimary,
  surface = YapeWhite,
  onSurface = YapeTextPrimary,
  surfaceVariant = YapeTrayBackground,
  onSurfaceVariant = YapeTextSecondary,
  outline = YapeDivider,
  error = YapeErrorRed,
  onError = YapeWhite,
  errorContainer = YapeErrorRedLight,
  onErrorContainer = YapeErrorRed,
)

@Composable
fun YapeMirrorTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = YapeLightColorScheme,
    typography = Typography,
    content = content,
  )
}
