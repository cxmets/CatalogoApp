package com.comets.catalogokmp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight // Certifique-se que este import está presente
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun Color.lighten(factor: Float, forceOpaque: Boolean = true): Color {
    val newRed = (this.red + (1 - this.red) * factor).coerceIn(0f, 1f)
    val newGreen = (this.green + (1 - this.green) * factor).coerceIn(0f, 1f)
    val newBlue = (this.blue + (1 - this.blue) * factor).coerceIn(0f, 1f)
    return Color(newRed, newGreen, newBlue, alpha = if (forceOpaque) 1f else this.alpha)
}

@Composable
fun GradientBorderLightFillButton(
    text: String,
    vibrantGradient: Brush,
    lightGradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    borderWidth: Dp = 2.dp,
    cornerRadiusDp: Dp = 25.dp,
    textStyle: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center
    )
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadiusDp))
            .background(vibrantGradient)
            .clickable(onClick = onClick)
            .padding(borderWidth)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(cornerRadiusDp - borderWidth))
                .background(lightGradient)
                .padding(
                    horizontal = (16.dp - borderWidth).coerceAtLeast(0.dp),
                    vertical = (12.dp - borderWidth).coerceAtLeast(0.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                style = textStyle,
                textAlign = textStyle.textAlign
            )
        }
    }
}