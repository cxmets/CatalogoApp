package com.comets.catalogo

import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures // Para gestos de transformação (pinch, pan)
import androidx.compose.foundation.gestures.detectTapGestures // Para gestos de toque (single, double tap)
import androidx.compose.foundation.interaction.MutableInteractionSource // Para clickable sem ripple
import androidx.compose.foundation.layout.* // Layouts: Column, Row, Box, Modifier, fillMaxSize, padding, etc.
import androidx.compose.foundation.rememberScrollState // Para estado de scroll
import androidx.compose.foundation.verticalScroll // Para habilitar scroll vertical
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Ícone de voltar
import androidx.compose.material3.* // Componentes Material3: Icon, IconButton, Text, Card, etc.
import androidx.compose.runtime.* // Estados: remember, mutableStateOf, etc.
import androidx.compose.runtime.mutableFloatStateOf // Estado para Float (scale)
import androidx.compose.ui.Alignment // Alinhamento
import androidx.compose.ui.Modifier // Modificador de UI
import androidx.compose.ui.geometry.Offset // Para deslocamento (pan)
import androidx.compose.ui.geometry.Size // Para tamanhos
import androidx.compose.ui.graphics.Color // Cores
import androidx.compose.ui.graphics.TransformOrigin // Origem da transformação (zoom)
import androidx.compose.ui.graphics.asImageBitmap // Converter Bitmap para ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer // Aplicar transformações gráficas
import androidx.compose.ui.input.pointer.pointerInput // Detecção de ponteiro/gestos
import androidx.compose.ui.layout.ContentScale // Escala de imagem
import androidx.compose.ui.platform.LocalContext // Acessar contexto Android
import androidx.compose.ui.text.style.TextAlign // Alinhamento de texto
import androidx.compose.ui.unit.dp // Unidade DP
import androidx.compose.ui.unit.sp // Unidade SP
import androidx.navigation.NavController // Navegação
import androidx.compose.ui.text.font.FontWeight


// =================================================================
// Composable principal da tela de Detalhes do Produto
// =================================================================

@Composable
fun DetalhesProduto(produto: Produto, navController: NavController) {
    val context = LocalContext.current
    val assetManager = context.assets // Assumindo que a imagem está em Assets

    // --- Estado para controlar a visibilidade do overlay de zoom ---
    // Quando 'true', o overlay ZoomableImageOverlay é exibido sobre o resto da tela.
    var isImageOverlayVisible by remember { mutableStateOf(false) }

    // --- Layout principal da tela (manual, sem Scaffold/TopAppBar) ---
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa a tela inteira
            // Aplicar o padding superior para alinhar com o cabeçalho manual
            .padding(top = 32.dp) // Ajuste este valor conforme o padding do topo da sua ProdutoLista
    ) {
        // --- Cabeçalho manual: Botão Voltar + Código do Produto ---
        Row(
            modifier = Modifier
                .fillMaxWidth() // Ocupa a largura total
                .padding(horizontal = 4.dp), // Padding nas laterais
            verticalAlignment = Alignment.CenterVertically, // Alinha itens verticalmente
            horizontalArrangement = Arrangement.SpaceBetween // Distribui espaço entre itens
        ) {
            // Botão de voltar para a tela anterior
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
            }

            // Texto do Código do Produto (Centralizado na Row)
            Text(
                text = produto.codigo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f), // Permite que o texto ocupe o máximo de espaço disponível
                textAlign = TextAlign.Center // Centraliza o texto dentro desse espaço
            )

            // Placeholder invisível para ajudar a centralizar o texto do código,
            // compensando visualmente o IconButton à esquerda.
            Spacer(modifier = Modifier.width(48.dp)) // Largura similar à de um IconButton
        }

        // --- Conteúdo Rolável: Imagem e Detalhes ---
        // Este Column contém a imagem e os detalhes e permite rolagem
        Column(modifier = Modifier
            .fillMaxSize() // Ocupa o espaço restante verticalmente após o cabeçalho
            .verticalScroll(rememberScrollState()) // Habilita rolagem vertical
        ) {
            // --- Container da Imagem Principal ---
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Preenche a largura disponível
                    .aspectRatio(1f) // Mantém uma proporção 1:1 (quadrada). Ajuste se necessário.
                    .background(Color.LightGray) // Fundo leve, visível se a imagem não preencher a Box
                    // --- Torna a Box clicável para mostrar o overlay de zoom ---
                    .clickable(
                        // interactionSource e indication = null removem o "efeito de onda" (ripple) ao clicar
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // Ao clicar, muda o estado para true, o que fará o overlay ser exibido
                        isImageOverlayVisible = true
                    }
            ) {
                // --- Carregamento da Imagem Principal a partir de Assets ---
                // Seu código original para carregar Bitmap de Assets
                val inputStream = assetManager.open(produto.imagemUrl) // Abre o asset usando o nome do arquivo
                val bitmap = BitmapFactory.decodeStream(inputStream) // Decodifica o stream para Bitmap
                inputStream.close() // Fecha o stream
                val imageBitmap = bitmap.asImageBitmap() // Converte para ImageBitmap do Compose Image

                // Componente Image para exibir o Bitmap
                Image(
                    bitmap = imageBitmap, // A imagem Bitmap carregada
                    contentDescription = produto.nome, // Descrição para acessibilidade (nome do produto)
                    modifier = Modifier
                        .fillMaxSize() // Faz a imagem preencher a Box Container
                        .align(Alignment.Center), // Centraliza a imagem na Box
                    contentScale = ContentScale.Fit // Ajusta a imagem para caber sem cortar
                )
            }

            // --- Coluna para os Textos de Detalhes do Produto ---
            Column(
                modifier = Modifier
                    .fillMaxWidth() // Preenche a largura disponível
                    .padding(16.dp) // Adiciona padding interno
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Espaço vertical

                Text(
                    text = produto.nome,
                    fontSize = 24.sp, // Tamanho da fonte
                    modifier = Modifier.fillMaxWidth(), // Preenche a largura
                    textAlign = TextAlign.Center // Centraliza o texto
                )
                Spacer(modifier = Modifier.height(8.dp)) // Espaço

                Text(
                    text = produto.descricao,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp)) // Espaço

                Text(text = produto.detalhes, fontSize = 16.sp)
                // Adicione aqui outros campos de texto do produto se necessário
            }
        }
    }

    if (isImageOverlayVisible) {
        // Chama a função Composable do overlay de zoom
        ZoomableImageOverlay(
            // Passa a informação da imagem que o overlay precisa (o nome do asset)
            imageUrl = produto.imagemUrl,
            // Passa uma função que o overlay pode chamar para indicar que deve ser fechado
            onDismiss = {
                // Esta lambda é executada quando o overlay chama onDismiss().
                // Ela muda o estado de volta para false, ocultando o overlay.
                isImageOverlayVisible = false
            }
        )
    }
    // -------------------------------------------------------
}


// =================================================================
// Composable do Overlay de Imagem Ampliável (Zoomable)
// =================================================================
// Esta função é definida no MESMO arquivo abaixo da DetalhesProduto.
// Ela é chamada APENAS pela DetalhesProduto quando isImageOverlayVisible é true.
// =================================================================

@Composable
fun ZoomableImageOverlay(
    imageUrl: String, // Nome do asset da imagem a ser exibida (ex: "biz.png")
    onDismiss: () -> Unit // Função a ser chamada para fechar este overlay
) {
    val context = LocalContext.current
    // Assumindo que a imagem é carregada dos Assets locais,
    // como no código original que você forneceu.
    val assetManager = context.assets

    // --- Estados para controle de zoom (scale) e pan (offset/deslocamento) ---
    var scale by remember { mutableFloatStateOf(1f) } // Escala de zoom, inicia em 1f (tamanho normal)
    var offset by remember { mutableStateOf(Offset.Zero) } // Deslocamento da imagem, inicia no centro (sem deslocamento)

    // --- Limites para a escala de zoom ---
    val maxScale = 5f // Escala máxima permitida (5 vezes o tamanho original)
    val minScale = 1f // Escala mínima (o tamanho original)

    // --- Tratamento do botão Voltar do sistema Android ---
    // Intercepta o botão Voltar quando este overlay está ativo.
    // Se a imagem estiver zoom in, o primeiro Back pressionado reseta o zoom/pan.
    // Se a imagem estiver em zoom out (1x), o Back pressionado chama onDismiss para fechar o overlay.
    BackHandler {
        if (scale > minScale) { // Se a imagem estiver com zoom (escala > 1x)
            scale = minScale // Reseta a escala para o mínimo
            offset = Offset.Zero // Reseta o deslocamento para o centro
        } else { // Se a imagem estiver em zoom out (escala = 1x)
            onDismiss() // Chama a função para fechar o overlay
        }
    }

    // --- Container do Overlay: ocupa a tela inteira com fundo escuro ---
    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa a tela inteira disponível
            .background(Color.Black.copy(alpha = 0.8f)) // Fundo preto semi-transparente
            // Torna a Box clicável. Um clique simples no fundo escuro fecha o overlay.
            .clickable(
                // interactionSource e indication = null: Remove o efeito visual de "onda" ao clicar no fundo.
                interactionSource = remember { MutableInteractionSource() },
                indication = null // <-- Removendo a indicação visual (onda/ripple) AQUI
            ) {
                onDismiss() // Lógica de fechar ao clicar no fundo
            }
            // pointerInput(Unit) {} vazio: Adicionado para garantir que eventos de toque na Box
            // (que não sejam tratados pelos gestos na Image) sejam consumidos aqui e não vazem para baixo.
            .pointerInput(Unit) {}
    ) {
        // --- Carregamento e Exibição da Imagem a ser Ampliada ---
        // Carrega a imagem do asset local usando o nome do arquivo (imageUrl)
        val inputStream = assetManager.open(imageUrl)
        val bitmap = BitmapFactory.decodeStream(inputStream) // Decodifica para Bitmap
        inputStream.close() // Fecha o stream
        val imageBitmap = bitmap.asImageBitmap() // Converte para ImageBitmap do Compose

        // Componente Image para exibir o bitmap carregado
        Image(
            bitmap = imageBitmap, // A imagem Bitmap a ser exibida
            contentDescription = "Imagem Ampliada", // Descrição para acessibilidade
            modifier = Modifier
                .fillMaxSize() // Faz a imagem preencher o tamanho da Box container
                .align(Alignment.Center) // Centraliza a imagem na Box
                // --- Detecção de Gestos de Transformação (Pinch-to-Zoom e Pan/Drag) ---
                .pointerInput(Unit) { // Adiciona um detector de ponteiro para gestos
                    detectTransformGestures { gestureCentroid, gesturePan, gestureZoom, _ ->
                        // Atualiza a escala de zoom: multiplica a escala atual pelo fator de zoom do gesto.
                        // coerceIn limita a escala resultante entre minScale e maxScale.
                        scale = (scale * gestureZoom).coerceIn(minScale, maxScale)

                        // Calcula os limites para o pan (deslocamento) para que a imagem não saia completamente da tela
                        val imageDisplaySize = Size(size.width.toFloat(), size.height.toFloat()) // Tamanho da área onde a imagem é exibida
                        val scaledWidth = imageDisplaySize.width * scale // Largura da imagem após o zoom
                        val scaledHeight = imageDisplaySize.height * scale // Altura da imagem após o zoom

                        // maxX e maxY: o deslocamento máximo permitido do centro para a borda antes que a imagem "grude" na borda da Box.
                        // É 0 se a imagem escalada é menor ou igual à área de exibição.
                        val maxX = if (scaledWidth > imageDisplaySize.width) (scaledWidth - imageDisplaySize.width) / 2f else 0f
                        val maxY = if (scaledHeight > imageDisplaySize.height) (scaledHeight - imageDisplaySize.height) / 2f else 0f

                        // newOffset: o novo deslocamento calculado adicionando o deslocamento atual ao pan do gesto.
                        val newOffset = offset + gesturePan

                        // Aplica o newOffset, restringindo seus valores dentro dos limites calculados (-maxX a maxX, -maxY a maxY).
                        offset = Offset(
                            x = newOffset.x.coerceIn(-maxX, maxX), // Restringe o deslocamento horizontal
                            y = newOffset.y.coerceIn(-maxY, maxY)  // Restringe o deslocamento vertical
                        )
                    }
                }
                // --- Detecção de Gestos de Toque (Toque Duplo para Alternar Zoom In/Reset) ---
                .pointerInput(Unit) { // Adiciona outro detector de ponteiro para toques
                    detectTapGestures(
                        onDoubleTap = { tapOffset -> // Lida especificamente com o evento de toque duplo. tapOffset é a posição do toque.
                            // --- Lógica para alternar Zoom In / Reset ---
                            // Se a escala atual for próxima do tamanho normal (1x), dá zoom in.
                            // Caso contrário (já está zoom in), reseta para o tamanho normal (1x).
                            scale = if (scale < 1.5f) { // Verifica se a escala é menor que 1.5f (considerando "zoom out")
                                3f // Nível de zoom in desejado (ajuste este valor se quiser um zoom maior ou menor)
                            } else {
                                minScale // Reseta para o zoom mínimo (1x) (Este é o "reset")
                            }

                            // --- Lógica de Deslocamento (Offset) ao Dar Toque Duplo ---
                            // Para simplificar, reseta o deslocamento para o centro (Offset.Zero) após qualquer toque duplo.
                            // Isso faz com que a imagem sempre centralize na tela ao dar toque duplo.
                            offset = Offset.Zero

                            // OBS: Para centralizar o zoom no ponto EXATO do toque duplo, a lógica de offset seria mais complexa,
                            // precisando calcular o deslocamento necessário com base no 'tapOffset' e no tamanho da imagem.
                        }
                    )
                }
                // --- Aplica as Transformações Gráficas (Zoom e Pan) ---
                // Este modifier é crucial; ele aplica as mudanças de scale e offset calculadas pelos gestos.
                .graphicsLayer {
                    translationX = offset.x // Aplica o deslocamento horizontal (pan)
                    translationY = offset.y // Aplica o deslocamento vertical (pan)
                    scaleX = scale // Aplica a escala horizontal (zoom)
                    scaleY = scale // Aplica a escala vertical (zoom)
                    transformOrigin = TransformOrigin.Center // O ponto a partir do qual as transformações (zoom) são aplicadas
                },
            contentScale = ContentScale.Fit // Escala a imagem para caber na Box sem cortar, mantendo a proporção
        )
    }
}