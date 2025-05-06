package com.comets.catalogo

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

@Composable
fun TelaInicial(navController: NavController) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    // Calcular a cor customizada para o container dos botões manualmente
    val customButtonContainerColor = if (isDarkTheme) {
        // No tema escuro, tornar a cor ligeiramente mais clara
        Color(
            red = (backgroundColor.red + 0.15f).coerceIn(0f, 1f), // Aumenta Vermelho (clamp entre 0f e 1f)
            green = (backgroundColor.green + 0.15f).coerceIn(0f, 1f), // Aumenta Verde
            blue = (backgroundColor.blue + 0.15f).coerceIn(0f, 1f), // Aumenta Azul
            alpha = backgroundColor.alpha // Mantém o alpha original
        )
    } else {
        // No tema claro, tornar a cor ligeiramente mais escura
        Color(
            red = (backgroundColor.red - 0.15f).coerceIn(0f, 1f), // Diminui Vermelho
            green = (backgroundColor.green - 0.15f).coerceIn(0f, 1f), // Diminui Verde
            blue = (backgroundColor.blue - 0.15f).coerceIn(0f, 1f), // Diminui Azul
            alpha = backgroundColor.alpha // Mantém o alpha original
        )
    }

    val customButtonContentColor = onBackgroundColor

    val customButtonColors = ButtonDefaults.buttonColors(
        containerColor = customButtonContainerColor,
        contentColor = customButtonContentColor
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.weight(0.3f))

        Spacer(modifier = Modifier.size(150.dp))

        Text(
            text = "Logo da Empresa",
            style = MaterialTheme.typography.headlineMedium,
            color = onBackgroundColor
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate(Routes.LISTA) },
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = customButtonColors
        ) {
            Text("Acessar Catálogo")
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(0.6f),
            thickness = 1.dp,
            color = customButtonContentColor
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Toast.makeText(context, "Fale Conosco clicado!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(0.6f),
            colors = customButtonColors
        ) {
            Text("Fale Conosco")
        }

        Spacer(modifier = Modifier.weight(0.7f))
    }
}