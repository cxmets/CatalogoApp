package com.comets.catalogokmp.ui.screens.inicial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import catalogokmp.shared.generated.resources.Res
import catalogokmp.shared.generated.resources.desc_logo_nexpart_tela_inicial
import catalogokmp.shared.generated.resources.logo_dark
import catalogokmp.shared.generated.resources.logo_light
import catalogokmp.shared.generated.resources.tela_inicial_botao_catalogo
import catalogokmp.shared.generated.resources.tela_inicial_botao_fale_conosco
import com.comets.catalogokmp.model.UserThemePreference
import com.comets.catalogokmp.navigation.FaleConoscoVoyagerScreen
import com.comets.catalogokmp.navigation.ProdutoListaVoyagerScreen
import com.comets.catalogokmp.presentation.AppViewModel
import com.comets.catalogokmp.ui.common.GradientBorderLightFillButton
import com.comets.catalogokmp.ui.common.lighten
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun TelaInicial() {
    val navigator = LocalNavigator.currentOrThrow
    val appViewModel: AppViewModel = koinInject()
    val userThemePreference by appViewModel.userThemePreference.collectAsState()
    val systemIsDark = isSystemInDarkTheme()

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

    val currentEffectiveDarkTheme = when (userThemePreference) {
        UserThemePreference.LIGHT -> false
        UserThemePreference.DARK -> true
        UserThemePreference.SYSTEM -> systemIsDark
    }

    Box(modifier = Modifier.fillMaxSize().background(backgroundColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            val logoResourceId = if (currentEffectiveDarkTheme) {
                Res.drawable.logo_dark
            } else {
                Res.drawable.logo_light
            }

            Image(
                painter = painterResource(logoResourceId),
                contentDescription = stringResource(Res.string.desc_logo_nexpart_tela_inicial),
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
                    text = stringResource(Res.string.tela_inicial_botao_catalogo),
                    vibrantGradient = nexpartVibrantGradient,
                    lightGradient = nexpartLightGradient,
                    textColor = textoCorBotoes,
                    onClick = { navigator.push(ProdutoListaVoyagerScreen) },
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
                    text = stringResource(Res.string.tela_inicial_botao_fale_conosco),
                    vibrantGradient = nexpartVibrantGradient,
                    lightGradient = nexpartLightGradient,
                    textColor = textoCorBotoes,
                    onClick = { navigator.push(FaleConoscoVoyagerScreen) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    cornerRadiusDp = 25.dp,
                    borderWidth = 2.dp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        val nextThemePreference: UserThemePreference
        val themeIcon: androidx.compose.ui.graphics.vector.ImageVector
        val iconContentDescription: String

        when (userThemePreference) {
            UserThemePreference.SYSTEM -> {
                nextThemePreference = UserThemePreference.LIGHT
                themeIcon = Icons.Filled.BrightnessAuto
                iconContentDescription = "Mudar para tema claro (Atualmente: Sistema)"
            }
            UserThemePreference.LIGHT -> {
                nextThemePreference = UserThemePreference.DARK
                themeIcon = Icons.Filled.LightMode
                iconContentDescription = "Mudar para tema escuro (Atualmente: Claro)"
            }
            UserThemePreference.DARK -> {
                nextThemePreference = UserThemePreference.SYSTEM
                themeIcon = Icons.Filled.DarkMode
                iconContentDescription = "Mudar para tema do sistema (Atualmente: Escuro)"
            }
        }

        IconButton(
            onClick = { appViewModel.setUserThemePreference(nextThemePreference) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 75.dp)
        ) {
            Icon(
                imageVector = themeIcon,
                contentDescription = iconContentDescription,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}