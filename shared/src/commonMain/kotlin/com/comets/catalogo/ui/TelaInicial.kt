package com.comets.catalogo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.comets.catalogo.GradientBorderLightFillButton
import com.comets.catalogo.lighten
import dev.icerock.moko.resources.compose.painterResource // <<< IMPORT DO MOKO
import dev.icerock.moko.resources.compose.stringResource // <<< IMPORT DO MOKO

@Composable
fun TelaInicial(
    onNavigateToLista: () -> Unit,
    onNavigateToFaleConosco: () -> Unit
) {
    val systemIsDarkTheme = isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background
    val outlineColor = MaterialTheme.colorScheme.outline

    val amareloOriginal = MaterialTheme.colorScheme.tertiary
    val laranjaOriginal = MaterialTheme.colorScheme.primary
    val vermelhoOriginal = MaterialTheme.colorScheme.secondary

    val nexpartVibrantGradient = Brush.horizontalGradient(
        colors = listOf(amareloOriginal, laranjaOriginal, vermelhoOriginal)
    )

    val lightenFactor = 0.75f
    val amareloClarinho = amareloOriginal.lighten(lightenFactor, forceOpaque = true)
    val laranjaClarinho = laranjaOriginal.lighten(lightenFactor, forceOpaque = true)
    val vermelhoClarinho = vermelhoOriginal.lighten(lightenFactor, forceOpaque = true)

    val nexpartLightGradient = Brush.horizontalGradient(
        colors = listOf(amareloClarinho, laranjaClarinho, vermelhoClarinho)
    )
    val textoCorBotoes = Color.Black

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.3f))


        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.8f),
            thickness = 1.dp,
            color = outlineColor.copy(alpha = 0.3f)
        )
    }
}