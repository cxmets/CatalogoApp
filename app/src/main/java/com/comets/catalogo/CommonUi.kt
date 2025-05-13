package com.comets.catalogo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Função auxiliar para clarear cores
fun Color.lighten(factor: Float, forceOpaque: Boolean = true): Color {
    val newRed = (this.red + (1 - this.red) * factor).coerceIn(0f, 1f)
    val newGreen = (this.green + (1 - this.green) * factor).coerceIn(0f, 1f)
    val newBlue = (this.blue + (1 - this.blue) * factor).coerceIn(0f, 1f)
    return Color(newRed, newGreen, newBlue, alpha = if (forceOpaque) 1f else this.alpha)
}

// Composable para o Botão com Borda em Degradê e Fundo Clarinho
@Composable
fun GradientBorderLightFillButton(
    text: String,
    vibrantGradient: Brush,
    lightGradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.primary,
    borderWidth: Dp = 2.dp,
    cornerRadiusDp: Dp = 25.dp
) {
    Box( // Usaremos Box em vez de Button base para ter mais controle sobre a borda em degradê
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadiusDp)) // Arredonda os cantos do conjunto
            .background(vibrantGradient) // Fundo com o degradê vibrante (será a borda)
            .clickable(onClick = onClick) // Torna toda a área clicável (com ripple padrão)
            .padding(borderWidth) // Este padding "cria" a borda visualmente
    ) {
        Box( // Box interno para o fundo com degradê claro e o texto
            modifier = Modifier
                .fillMaxSize() // Ocupa o espaço interno deixado pelo padding do Box externo
                .clip(RoundedCornerShape(cornerRadiusDp - borderWidth)) // Arredonda para encaixar na borda
                .background(lightGradient) // Fundo com o degradê clarinho
                .padding( // Padding interno para o texto, ajustado pela largura da borda
                    horizontal = (16.dp - borderWidth).coerceAtLeast(0.dp),
                    vertical = (12.dp - borderWidth).coerceAtLeast(0.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}