package com.comets.catalogo.ui.theme // Certifique-se que o pacote está correto

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp), // Usado frequentemente para Cards
    large = RoundedCornerShape(12.dp)  // Pode ser usado para Dialogs ou elementos maiores
    // extraSmall = RoundedCornerShape(2.dp), // Se precisar de cantos menos arredondados
    // extraLarge = RoundedCornerShape(16.dp) // Se precisar de cantos mais arredondados
)