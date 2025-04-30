package com.comets.catalogo

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults // Importar ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Importar Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme


@Composable
fun TelaInicial(navController: NavController) {
    val context = LocalContext.current
    val assetManager = context.assets
    val isDarkTheme = isSystemInDarkTheme() // Verifica o tema atual

    // Define o nome do arquivo de imagem com base no tema
    val backgroundImageName = if (isDarkTheme) "Bd-dark.png" else "Bd-light.png"

    // Carrega a imagem do asset
    val inputStream = try {
        assetManager.open(backgroundImageName)
    } catch (e: Exception) {
        e.printStackTrace()
        null // Lidar com erro de carregamento, talvez mostrar um placeholder
    }

    val bitmap = inputStream?.let {
        BitmapFactory.decodeStream(it).asImageBitmap()
    }

    // Definir as cores customizadas para os botões
    val customButtonColors = ButtonDefaults.buttonColors(
        containerColor = if (isDarkTheme) Color.White else Color(0xFF1A1A1A), // Branco no escuro, Quase Preto no claro
        contentColor = if (isDarkTheme) Color(0xFF1A1A1A) else Color.White // Quase Preto no escuro, Branco no claro (Cor do texto)
        // Você pode adicionar disabledContainerColor e disabledContentColor se necessário
    )

    // Usar um Box para empilhar o fundo e o conteúdo
    Box(modifier = Modifier.fillMaxSize()) {

        // Exibe a imagem de fundo
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Opcional: Mostrar uma cor de fundo sólida ou outro indicador se a imagem não carregar
            Spacer(modifier = Modifier.fillMaxSize().background(if (isDarkTheme) Color.Black else Color.LightGray)) // Exemplo de cor de fundo alternativa
        }


        // Conteúdo principal (logo, botões) dentro de um Column, alinhado ao centro
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Placeholder para o Logo
            Spacer(modifier = Modifier.size(150.dp))

            // Exemplo com Text (temporário para visualizar):
            Text(
                text = "Logo da Empresa",
                style = MaterialTheme.typography.headlineMedium,
                // Ajustar a cor do texto do logo para ser visível sobre o fundo
                color = if (isDarkTheme) Color.White else Color.Black // Exemplo: Branco no escuro, Preto no claro
            )
            Spacer(modifier = Modifier.height(32.dp))


            // Botão "Acessar Catálogo" com cores customizadas
            Button(
                onClick = { navController.navigate(Routes.LISTA) },
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = customButtonColors // Aplicar cores customizadas
            ) {
                Text("Acessar Catálogo")
            }

            // Linha fina entre os botões
            Spacer(modifier = Modifier.height(16.dp)) // Espaço antes da linha
            HorizontalDivider( // Usar HorizontalDivider em vez de Divider
                modifier = Modifier.fillMaxWidth(0.8f), // Mesma largura dos botões
                thickness = 1.dp, // Espessura da linha
                color = if (isDarkTheme) Color(0xFF1A1A1A) else Color.White // Mesma cor do texto do botão para contraste
            )
            Spacer(modifier = Modifier.height(16.dp)) // Espaço depois da linha


            // Botão "Fale Conosco" com cores customizadas
            Button(
                onClick = {
                    Toast.makeText(context, "Fale Conosco clicado!", Toast.LENGTH_SHORT).show()
                    // TODO: Implementar a ação real de Fale Conosco
                },
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = customButtonColors // Aplicar cores customizadas
            ) {
                Text("Fale Conosco")
            }
        }
    }
}