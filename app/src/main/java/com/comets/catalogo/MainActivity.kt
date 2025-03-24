package com.comets.catalogo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.comets.catalogo.ui.theme.CatalogoAppTheme // Importação correta do tema

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent() // Chama a função Composable
        }
    }
}

@Composable
fun AppContent() {
    CatalogoAppTheme { // Nome corrigido aqui
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val produtos = ProdutoRepository.getProdutos()
            var searchText by remember { mutableStateOf("") }
            var selectedCategory by remember { mutableStateOf("") }
            var selectedCategoria2 by remember { mutableStateOf("") }

            ProdutoLista(
                produtos = produtos,
                searchText = searchText,
                selectedCategory = selectedCategory,
                selectedCategoria2 = selectedCategoria2,
                onSearchTextChanged = { searchText = it },
                onCategorySelected = { selectedCategory = it },
                onCategoria2Selected = { selectedCategoria2 = it }
            )
        }
    }
}