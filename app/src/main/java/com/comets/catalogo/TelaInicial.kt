package com.comets.catalogo

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource // Necessário para R.string
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun TelaInicial(navController: NavController) {
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

        val logoResourceId = if (systemIsDarkTheme) {
            R.drawable.logo_dark
        } else {
            R.drawable.logo_light
        }

        Image(
            painter = painterResource(id = logoResourceId),
            contentDescription = stringResource(id = R.string.desc_logo_nexpart_tela_inicial),
            modifier = Modifier
                .fillMaxWidth(0.70f)
                .aspectRatio(1f)
                .padding(bottom = 5.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(0.75f)
        ) {
            GradientBorderLightFillButton(
                text = stringResource(id = R.string.tela_inicial_botao_catalogo),
                vibrantGradient = nexpartVibrantGradient,
                lightGradient = nexpartLightGradient,
                textColor = textoCorBotoes,
                onClick = { navController.navigate(Routes.LISTA) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                cornerRadiusDp = 25.dp,
                borderWidth = 2.dp
            )

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(0.8f),
                thickness = 1.dp,
                color = outlineColor.copy(alpha = 0.3f)
            )

            GradientBorderLightFillButton(
                text = stringResource(id = R.string.tela_inicial_botao_fale_conosco),
                vibrantGradient = nexpartVibrantGradient,
                lightGradient = nexpartLightGradient,
                textColor = textoCorBotoes,
                onClick = {
                    navController.navigate(Routes.FALE_CONOSCO)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                cornerRadiusDp = 25.dp,
                borderWidth = 2.dp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}