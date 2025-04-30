package com.comets.catalogo

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController // Importar NavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ProdutoItem(produto: Produto, navController: NavController) { // Recebe NavController
    val context = LocalContext.current
    val assetManager = context.assets

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Card(
                modifier = Modifier
                    .padding(7.dp)
                    .clickable {
                        val encodedCodigo = URLEncoder.encode(produto.codigo, StandardCharsets.UTF_8.toString())
                        // Usar a constante de rota
                        navController.navigate("${Routes.DETALHES_BASE}/$encodedCodigo")
                    },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                // O carregamento da imagem pode ser otimizado aqui no futuro
                val inputStream = assetManager.open(produto.imagemUrl)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                val imageBitmap = bitmap.asImageBitmap()

                Image(
                    bitmap = imageBitmap,
                    contentDescription = produto.nome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                text = produto.nome,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 5.dp),
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                .padding(3.dp)
        ) {
            Text(
                text = produto.codigo,
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}