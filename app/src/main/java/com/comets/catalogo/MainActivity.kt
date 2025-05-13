package com.comets.catalogo

import android.app.Activity // Importar Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme // Importar isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat // Importar WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comets.catalogo.ui.theme.CatalogoAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    // Acessa a window somente se o contexto for uma Activity
    val window = (context as? Activity)?.window // Usar 'as?' para cast seguro

    // isDarkTheme será usado no SideEffect
    val isDarkTheme = isSystemInDarkTheme()

    CatalogoAppTheme {
        val colorScheme = MaterialTheme.colorScheme // colorScheme é usado no Surface

        // Configurar a barra de status para ser transparente
        // Este SideEffect é crucial para o modo edge-to-edge
        if (window != null) { // Só executa se window não for nula
            SideEffect {
                // Permite que o conteúdo seja desenhado por trás da barra de status
                WindowCompat.setDecorFitsSystemWindows(window, false)

                // A cor da barra de status é definida como transparente no Theme XML (themes.xml)
                // ou pode ser definida programaticamente se necessário (mas transparente é comum para edge-to-edge)
                // window.statusBarColor = android.graphics.Color.TRANSPARENT // Já deve estar no tema

                // Ajusta a cor dos ícones da barra de status (claro ou escuro)
                WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !isDarkTheme
            }
        }


        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.background // Usar cor do tema
        ) {
            val navController = rememberNavController()

            // Estados para os filtros e busca
            var searchText by remember { mutableStateOf("") }
            var selectedTipo by remember { mutableStateOf("") }
            var selectedLente by remember { mutableStateOf("") }
            var selectedHaste by remember { mutableStateOf("") }
            var selectedRosca by remember { mutableStateOf("") }


            NavHost(
                navController = navController,
                startDestination = Routes.INICIAL
            ) {
                composable(Routes.INICIAL) {
                    TelaInicial(navController = navController)
                }
                composable(Routes.LISTA) {
                    // Obter produtos passando o context
                    // O 'remember' aqui com 'context' como chave garante que os produtos
                    // são recarregados se o contexto (ou sua instância) mudar, o que é raro,
                    // mas é uma boa prática para dependências externas.
                    // Para dados que mudam com mais frequência, um ViewModel seria melhor.
                    val produtos = remember(context) { ProdutoRepository.getProdutos(context) }
                    ProdutoLista(
                        produtos = produtos,
                        searchText = searchText,
                        selectedTipo = selectedTipo,
                        selectedLente = selectedLente,
                        selectedHaste = selectedHaste,
                        selectedRosca = selectedRosca,
                        onSearchTextChanged = { searchText = it },
                        onTipoSelected = { selectedTipo = it },
                        onLenteSelected = { selectedLente = it },
                        onHasteSelected = { selectedHaste = it },
                        onRoscaSelected = { selectedRosca = it },
                        navController = navController
                    )
                }
                composable(Routes.DETALHES) { backStackEntry ->
                    val codigo = backStackEntry.arguments?.getString("codigo")
                    val produto = remember(context, codigo) { // Re-busca se o código ou contexto mudar
                        codigo?.let { ProdutoRepository.getProdutoPorCodigo(context, it) }
                    }
                    if (produto != null) {
                        DetalhesProduto(produto, navController)
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Produto não encontrado")
                        }
                    }
                }
            }
        }
    }
}