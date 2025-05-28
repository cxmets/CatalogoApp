package com.comets.catalogokmp.ui.theme

import androidx.compose.ui.graphics.Color

// Cores da Marca Nexpart (inspiradas no logo)
val NexpartOrangePrincipal = Color(0xFFF27304) // Laranja vibrante principal
val NexpartRedAcento = Color(0xFFC13504)     // Vermelho/Laranja escuro para acentos
val NexpartOrangeClaro = Color(0xFFF8A54A)   // Um tom mais claro para variantes ou hover/pressed states
val NexpartAmareloDetalhe = Color(0xFFFFC107) // Amarelo do início do degradê do logo, para detalhes sutis

// Cores base para tema claro
val LightBackground = Color(0xFFFDFBFF) // Um branco levemente off-white
val LightSurface = Color(0xFFFFFFFF)    // Para cards, dialogs, menus
val LightOnPrimary = Color.White
val LightOnSecondary = Color.White
val LightOnBackground = Color(0xFF1C1B1F)
val LightOnSurface = Color(0xFF1C1B1F)

// Cores base para tema escuro
val DarkBackground = Color(0xFF1C1B1F)  // Um cinza bem escuro
val DarkSurface = Color(0xFF2A292E)     // Um pouco mais claro que o fundo para cards, etc.
val DarkOnPrimary = Color.Black         // Para contraste sobre o laranja principal no tema escuro
val DarkOnSecondary = Color.White
val DarkOnBackground = Color(0xFFE6E1E5)
val DarkOnSurface = Color(0xFFE6E1E5)

// Definições completas para Material Theme Builder (ajustadas com as cores da Nexpart)
val md_theme_light_primary = NexpartOrangePrincipal
val md_theme_light_onPrimary = LightOnPrimary
val md_theme_light_primaryContainer = NexpartOrangeClaro
val md_theme_light_onPrimaryContainer = Color(0xFF2F1400) // Texto escuro sobre container laranja claro
val md_theme_light_secondary = NexpartRedAcento
val md_theme_light_onSecondary = LightOnSecondary
val md_theme_light_secondaryContainer = Color(0xFFFFDBD0) // Um pêssego claro derivado do vermelho
val md_theme_light_onSecondaryContainer = Color(0xFF4B0A00)
val md_theme_light_tertiary = NexpartAmareloDetalhe // Amarelo como terciário
val md_theme_light_onTertiary = Color.Black
val md_theme_light_tertiaryContainer = Color(0xFFFFE089)
val md_theme_light_onTertiaryContainer = Color(0xFF251A00)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color.White
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = LightBackground
val md_theme_light_onBackground = LightOnBackground
val md_theme_light_surface = LightSurface
val md_theme_light_onSurface = LightOnSurface
val md_theme_light_surfaceVariant = Color(0xFFF6DDCF) // Variante de superfície mais quente
val md_theme_light_onSurfaceVariant = Color(0xFF54433B)
val md_theme_light_outline = Color(0xFF877369)
val md_theme_light_inverseOnSurface = Color(0xFFFDEEE8)
val md_theme_light_inverseSurface = Color(0xFF372F2B)
val md_theme_light_inversePrimary = Color(0xFFFFB57D) // Laranja claro para primário inverso
val md_theme_light_surfaceTint = md_theme_light_primary
val md_theme_light_outlineVariant = Color(0xFFD9C1B5)
val md_theme_light_scrim = Color.Black

val md_theme_dark_primary = NexpartOrangePrincipal // Laranja se destaca bem no escuro
val md_theme_dark_onPrimary = DarkOnPrimary
val md_theme_dark_primaryContainer = Color(0xFF7A3A00) // Laranja escuro para container
val md_theme_dark_onPrimaryContainer = NexpartOrangeClaro
val md_theme_dark_secondary = NexpartRedAcento
val md_theme_dark_onSecondary = DarkOnSecondary
val md_theme_dark_secondaryContainer = Color(0xFF942B00)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFDBD0)
val md_theme_dark_tertiary = NexpartAmareloDetalhe // Amarelo se destaca
val md_theme_dark_onTertiary = Color.Black
val md_theme_dark_tertiaryContainer = Color(0xFF594400)
val md_theme_dark_onTertiaryContainer = Color(0xFFFFE089)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6) // Ajustado para melhor contraste
val md_theme_dark_background = DarkBackground
val md_theme_dark_onBackground = DarkOnBackground
val md_theme_dark_surface = DarkSurface
val md_theme_dark_onSurface = DarkOnSurface
val md_theme_dark_surfaceVariant = Color(0xFF54433B) // Variante de superfície escura
val md_theme_dark_onSurfaceVariant = Color(0xFFD9C1B5)
val md_theme_dark_outline = Color(0xFF9F8C82)
val md_theme_dark_inverseOnSurface = Color(0xFF201A17)
val md_theme_dark_inverseSurface = Color(0xFFEBE0DB)
val md_theme_dark_inversePrimary = NexpartOrangeClaro // Laranja claro para primário inverso no escuro
val md_theme_dark_surfaceTint = md_theme_dark_primary
val md_theme_dark_outlineVariant = Color(0xFF54433B)
val md_theme_dark_scrim = Color.Black