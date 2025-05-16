package com.comets.catalogo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
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
import androidx.compose.ui.res.stringResource // Import para stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Placeholders (os valores reais vêm daqui para formatação nas strings)
const val EMAIL_EMPRESA_VAL = "contato@nexpart.com.br"
const val TELEFONE_EMPRESA_VAL = "+551122071624"
const val NUMERO_TELEFONE_DISCADOR_VAL = "1122072006"
const val INSTAGRAM_PROFILE_NAME = "@nexpartoficial"
const val INSTAGRAM_URL_VAL = "https://www.instagram.com/nexpartoficial/"
const val WEBSITE_NAME = "nexpart.com.br"
const val WEBSITE_URL_VAL = "https://nexpart.com.br/"


fun abrirEmail(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.fale_conosco_assunto_email_padrao)) // Exemplo
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        try { context.startActivity(intent) } catch (e: Exception) {
            Log.e("TelaFaleConosco", "Erro ao abrir e-mail: ${e.message}")
            Toast.makeText(context, context.getString(R.string.erro_tentar_abrir_email), Toast.LENGTH_LONG).show()
        }
    } else {
        Log.w("TelaFaleConosco", "Nenhum app de e-mail encontrado.")
        Toast.makeText(context, context.getString(R.string.nenhum_app_email_encontrado), Toast.LENGTH_SHORT).show()
    }
}

fun abrirDiscador(context: Context, numero: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply { data = "tel:$numero".toUri() }
    if (intent.resolveActivity(context.packageManager) != null) {
        try { context.startActivity(intent) } catch (e: Exception) {
            Log.e("TelaFaleConosco", "Erro ao abrir discador: ${e.message}")
            Toast.makeText(context, context.getString(R.string.erro_tentar_abrir_discador), Toast.LENGTH_LONG).show()
        }
    } else {
        Log.w("TelaFaleConosco", "Nenhum app de discagem encontrado.")
        Toast.makeText(context, context.getString(R.string.nenhum_app_discagem_encontrado), Toast.LENGTH_SHORT).show()
    }
}

private fun tryAbrirWhatsAppWeb(context: Context, numeroFiltrado: String, mensagem: String) {
    val tag = "WhatsAppLauncher"
    val webUrl = "https://api.whatsapp.com/send?phone=$numeroFiltrado&text=${Uri.encode(mensagem)}"
    Log.d(tag, "URL api.whatsapp.com (fallback): $webUrl")
    val webIntent = Intent(Intent.ACTION_VIEW, webUrl.toUri())
    if (webIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para api.whatsapp.com, iniciando...")
        try { context.startActivity(webIntent) } catch (e: Exception) {
            Log.e(tag, "Erro ao iniciar activity para api.whatsapp.com: ${e.message}")
            Toast.makeText(context, context.getString(R.string.erro_tentar_abrir_whatsapp), Toast.LENGTH_LONG).show()
        }
    } else {
        Log.w(tag, "Nenhuma activity encontrada para api.whatsapp.com (fallback).")
        Toast.makeText(context, context.getString(R.string.whatsapp_nao_encontrado_ou_navegador), Toast.LENGTH_LONG).show()
    }
}

fun abrirWhatsApp(context: Context, numeroCompletoComCodigoPais: String, mensagemPadrao: String) {
    val tag = "WhatsAppLauncher"
    val numeroFiltrado = numeroCompletoComCodigoPais.filter { it.isDigit() }
    Log.d(tag, "Tentando abrir WhatsApp com número (filtrado): $numeroFiltrado")
    val urlWaMe = "https://wa.me/$numeroFiltrado?text=${Uri.encode(mensagemPadrao)}"
    var whatsAppIntent = Intent(Intent.ACTION_VIEW, urlWaMe.toUri())
    whatsAppIntent.setPackage("com.whatsapp")
    Log.d(tag, "Tentando intent direta para com.whatsapp com URL: ${whatsAppIntent.data}")
    if (whatsAppIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para com.whatsapp (direto), iniciando...")
        try { context.startActivity(whatsAppIntent); return } catch (e: Exception) {
            Log.e(tag, "Erro ao iniciar activity directa para com.whatsapp: ${e.message}")
        }
    } else {
        Log.d(tag, "Nenhuma activity encontrada para com.whatsapp (direto) com wa.me.")
    }
    Log.d(tag, "Tentando URL wa.me genérica: $urlWaMe")
    whatsAppIntent = Intent(Intent.ACTION_VIEW, urlWaMe.toUri())
    if (whatsAppIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para wa.me genérico, iniciando...")
        try { context.startActivity(whatsAppIntent); return } catch (e: Exception) {
            Log.e(tag, "Erro ao iniciar activity para wa.me genérico: ${e.message}")
        }
    } else {
        Log.d(tag, "Nenhuma activity encontrada para wa.me genérico.")
    }
    Log.d(tag, "Nenhuma das tentativas com wa.me funcionou. Tentando fallback web com tryAbrirWhatsAppWeb.")
    tryAbrirWhatsAppWeb(context, numeroFiltrado, mensagemPadrao)
}

fun abrirUrlGenerica(context: Context, url: String) {
    val tag = "GenericUrlLauncher"
    Log.d(tag, "Tentando abrir URL genérica: $url")
    val intent = Intent(Intent.ACTION_VIEW).apply { data = url.toUri() }
    if (intent.resolveActivity(context.packageManager) != null) {
        try {
            context.startActivity(intent)
            Log.d(tag, "Abriu URL com sucesso: $url")
        } catch (e: Exception) {
            Log.e(tag, "Erro ao abrir URL $url: ${e.message}")
            Toast.makeText(context, context.getString(R.string.erro_abrir_link), Toast.LENGTH_SHORT).show()
        }
    } else {
        Log.w(tag, "Nenhuma activity encontrada para URL: $url")
        Toast.makeText(context, context.getString(R.string.link_nao_encontrado_ou_navegador), Toast.LENGTH_SHORT).show()
    }
}

fun abrirInstagram(context: Context, profileUrl: String) {
    val tag = "InstagramLauncher"
    var instagramIntent = Intent(Intent.ACTION_VIEW, profileUrl.toUri())
    instagramIntent.setPackage("com.instagram.android")
    Log.d(tag, "Tentando abrir Instagram com setPackage: ${instagramIntent.data}")
    if (instagramIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para com.instagram.android, iniciando...")
        try { context.startActivity(instagramIntent); return } catch (e: Exception) {
            Log.e(tag, "Erro ao iniciar activity com setPackage para com.instagram.android: ${e.message}")
        }
    } else {
        Log.d(tag, "Nenhuma activity encontrada para com.instagram.android com setPackage.")
    }
    Log.d(tag, "Tentando abrir Instagram com intent HTTPS genérica: $profileUrl")
    instagramIntent = Intent(Intent.ACTION_VIEW, profileUrl.toUri())
    if (instagramIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para URL HTTPS genérica do Instagram, iniciando...")
        try { context.startActivity(instagramIntent) } catch (e: Exception) {
            Log.e(tag, "Erro ao abrir URL HTTPS do Instagram (genérico): ${e.message}")
            Toast.makeText(context, context.getString(R.string.erro_tentar_abrir_instagram), Toast.LENGTH_SHORT).show()
        }
    } else {
        Log.w(tag, "Nenhuma activity encontrada para URL HTTPS do Instagram (genérico): $profileUrl")
        Toast.makeText(context, context.getString(R.string.instagram_nao_encontrado_ou_navegador), Toast.LENGTH_SHORT).show()
    }
}


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
                    enabled = !isProcessingPopBack
                ) {
                    abrirEmail(context, EMAIL_EMPRESA_VAL)
                }

                ContactItem(
                    iconPainter = painterResource(id = R.drawable.i_phone),
                    text = stringResource(id = R.string.fale_conosco_label_telefone, NUMERO_TELEFONE_DISCADOR_VAL),
                    contentDescription = stringResource(id = R.string.fale_conosco_desc_acao_telefone, NUMERO_TELEFONE_DISCADOR_VAL),
                    enabled = !isProcessingPopBack
                ) {
                    abrirDiscador(context, NUMERO_TELEFONE_DISCADOR_VAL)
                }

                ContactItem(
                    iconPainter = painterResource(id = R.drawable.i_whatsapp),
                    text = stringResource(id = R.string.fale_conosco_label_whatsapp, TELEFONE_EMPRESA_VAL),
                    contentDescription = stringResource(id = R.string.fale_conosco_desc_acao_whatsapp, TELEFONE_EMPRESA_VAL),
                    enabled = !isProcessingPopBack
                ) {
                    abrirWhatsApp(context, TELEFONE_EMPRESA_VAL, mensagemPadraoWhatsApp)
                }

                ContactItem(
                    iconPainter = painterResource(id = R.drawable.i_instagram),
                    text = stringResource(id = R.string.fale_conosco_label_instagram, INSTAGRAM_PROFILE_NAME),
                    contentDescription = stringResource(id = R.string.fale_conosco_desc_acao_instagram_oficial),
                    enabled = !isProcessingPopBack
                ) {
                    abrirInstagram(context, INSTAGRAM_URL_VAL)
                }

                ContactItem(
                    iconPainter = painterResource(id = R.drawable.i_web),
                    text = stringResource(id = R.string.fale_conosco_label_site, WEBSITE_NAME),
                    contentDescription = stringResource(id = R.string.fale_conosco_desc_acao_site_nexpart),
                    enabled = !isProcessingPopBack
                ) {
                    abrirUrlGenerica(context, WEBSITE_URL_VAL)
                }
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
                contentDescription = contentDescription, // Já está usando stringResource na chamada
                modifier = Modifier.size(60.dp),
                tint = Color.Unspecified
            )
            Text(
                text = text, // Já está usando stringResource na chamada
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 19.sp)
            )
        }
    }
}