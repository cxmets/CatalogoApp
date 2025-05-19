package com.comets.catalogo

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


const val EMAIL_EMPRESA_VAL = "contato@nexpart.com.br"
const val TELEFONE_EMPRESA_VAL = "+551122071624"
const val NUMERO_TELEFONE_DISCADOR_VAL = "1122072006"
const val INSTAGRAM_PROFILE_NAME = "@nexpartoficial"
const val INSTAGRAM_URL_VAL = "https://www.instagram.com/nexpartoficial/"
const val WEBSITE_NAME = "nexpart.com.br"
const val WEBSITE_URL_VAL = "https://nexpart.com.br/"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFaleConosco(navController: NavController) {
    val context = LocalContext.current
    val systemIsDarkTheme = isSystemInDarkTheme()
    var isProcessingPopBack by remember { mutableStateOf(false) }

    val mensagemPadraoWhatsApp = stringResource(id = R.string.fale_conosco_mensagem_padrao_whatsapp)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.tela_fale_conosco_titulo_topbar)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isProcessingPopBack) { return@IconButton }
                        isProcessingPopBack = true
                        val popped = navController.popBackStack()
                        if (!popped) { isProcessingPopBack = false }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.voltar))
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = stringResource(id = R.string.desc_logo_nexpart_topbar),
                        modifier = Modifier.padding(end = 16.dp).size(36.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val textImageResourceId = if (systemIsDarkTheme) {
                R.drawable.text_dark
            } else {
                R.drawable.text_light
            }

            Image(
                painter = painterResource(id = textImageResourceId),
                contentDescription = stringResource(id = R.string.desc_imagem_texto_nexpart_contato),
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .aspectRatio(1200f / 379f)
                    .padding(bottom = 28.dp, top = 22.dp),
                contentScale = ContentScale.Fit
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ContactItem(
                    iconPainter = painterResource(id = R.drawable.i_mail),
                    text = stringResource(id = R.string.fale_conosco_label_email, EMAIL_EMPRESA_VAL),
                    contentDescription = stringResource(id = R.string.fale_conosco_desc_acao_email, EMAIL_EMPRESA_VAL),
                    enabled = !isProcessingPopBack,
                    onClick = { abrirEmail(context, EMAIL_EMPRESA_VAL) }
                )

                ContactItem(
                    iconPainter = painterResource(id = R.drawable.i_phone),
                    text = stringResource(id = R.string.fale_conosco_label_telefone, NUMERO_TELEFONE_DISCADOR_VAL),
                    contentDescription = stringResource(id = R.string.fale_conosco_desc_acao_telefone, NUMERO_TELEFONE_DISCADOR_VAL),
                    enabled = !isProcessingPopBack,
                    onClick = { abrirDiscador(context, NUMERO_TELEFONE_DISCADOR_VAL) }
                )

                ContactItem(
                    iconPainter = painterResource(id = R.drawable.i_whatsapp),
                    text = stringResource(id = R.string.fale_conosco_label_whatsapp, TELEFONE_EMPRESA_VAL),
                    contentDescription = stringResource(id = R.string.fale_conosco_desc_acao_whatsapp, TELEFONE_EMPRESA_VAL),
                    enabled = !isProcessingPopBack,
                    onClick = { abrirWhatsApp(context, TELEFONE_EMPRESA_VAL, mensagemPadraoWhatsApp) }
                )

                ContactItem(
                    iconPainter = painterResource(id = R.drawable.i_instagram),
                    text = stringResource(id = R.string.fale_conosco_label_instagram, INSTAGRAM_PROFILE_NAME),
                    contentDescription = stringResource(id = R.string.fale_conosco_desc_acao_instagram_oficial),
                    enabled = !isProcessingPopBack,
                    onClick = { abrirInstagram(context, INSTAGRAM_URL_VAL) }
                )

                ContactItem(
                    iconPainter = painterResource(id = R.drawable.i_web),
                    text = stringResource(id = R.string.fale_conosco_label_site, WEBSITE_NAME),
                    contentDescription = stringResource(id = R.string.fale_conosco_desc_acao_site_nexpart),
                    enabled = !isProcessingPopBack,
                    onClick = { abrirUrlGenerica(context, WEBSITE_URL_VAL) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(id = R.string.fale_conosco_mensagem_final),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }
}

@Composable
fun ContactItem(
    iconPainter: Painter,
    text: String,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = contentDescription,
                modifier = Modifier.size(60.dp),
                tint = Color.Unspecified
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 19.sp)
            )
        }
    }
}