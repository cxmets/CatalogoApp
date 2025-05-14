package com.comets.catalogo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.CameraAlt // Placeholder para Instagram
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Placeholders
const val EMAIL_EMPRESA = "contato@nexpart.com.br"
const val TELEFONE_EMPRESA = "+551122071624"
const val NUMERO_TELEFONE_DISCADOR = "1122072006"
const val INSTAGRAM_URL = "https://www.instagram.com/nexpartoficial/"
const val WEBSITE_URL = "https://nexpart.com.br/"

fun abrirEmail(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, "Contato via App Catálogo")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        try { context.startActivity(intent) } catch (e: Exception) {
            Log.e("TelaFaleConosco", "Erro ao abrir e-mail: ${e.message}")
            Toast.makeText(context, "Erro ao tentar abrir o e-mail.", Toast.LENGTH_LONG).show()
        }
    } else {
        Log.w("TelaFaleConosco", "Nenhum app de e-mail encontrado.")
        Toast.makeText(context, "Nenhum aplicativo de e-mail encontrado.", Toast.LENGTH_SHORT).show()
    }
}

fun abrirDiscador(context: Context, numero: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply { data = "tel:$numero".toUri() }
    if (intent.resolveActivity(context.packageManager) != null) {
        try { context.startActivity(intent) } catch (e: Exception) {
            Log.e("TelaFaleConosco", "Erro ao abrir discador: ${e.message}")
            Toast.makeText(context, "Erro ao tentar abrir o discador.", Toast.LENGTH_LONG).show()
        }
    } else {
        Log.w("TelaFaleConosco", "Nenhum app de discagem encontrado.")
        Toast.makeText(context, "Não foi possível abrir o discador.", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, "Erro ao tentar abrir o WhatsApp.", Toast.LENGTH_LONG).show()
        }
    } else {
        Log.w(tag, "Nenhuma activity encontrada para api.whatsapp.com (fallback).")
        Toast.makeText(context, "Não foi possível abrir o WhatsApp. Verifique se está instalado ou se há um navegador disponível.", Toast.LENGTH_LONG).show()
    }
}

fun abrirWhatsApp(context: Context, numeroCompletoComCodigoPais: String, mensagem: String = "Olá! Gostaria de mais informações.") {
    val tag = "WhatsAppLauncher"
    val numeroFiltrado = numeroCompletoComCodigoPais.filter { it.isDigit() }
    Log.d(tag, "Tentando abrir WhatsApp com número (filtrado): $numeroFiltrado")
    val urlWaMe = "https://wa.me/$numeroFiltrado?text=${Uri.encode(mensagem)}"
    var whatsAppIntent = Intent(Intent.ACTION_VIEW, urlWaMe.toUri())
    whatsAppIntent.setPackage("com.whatsapp")
    Log.d(tag, "Tentando intent direta para com.whatsapp com URL: ${whatsAppIntent.data}")
    if (whatsAppIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para com.whatsapp (direto), iniciando...")
        try { context.startActivity(whatsAppIntent); return } catch (e: Exception) {
            Log.e(tag, "Erro ao iniciar activity direta para com.whatsapp: ${e.message}")
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
    tryAbrirWhatsAppWeb(context, numeroFiltrado, mensagem)
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
            Toast.makeText(context, "Erro ao abrir o link.", Toast.LENGTH_SHORT).show()
        }
    } else {
        Log.w(tag, "Nenhuma activity encontrada para URL: $url")
        Toast.makeText(context, "Não foi possível abrir o link (navegador ou app não encontrado?).", Toast.LENGTH_SHORT).show()
    }
}

// NOVA FUNÇÃO para abrir Instagram com tentativas específicas
fun abrirInstagram(context: Context, profileUrl: String) {
    val tag = "InstagramLauncher"

    // Tentativa 1: Intent com setPackage para o app Instagram usando a URL HTTPS
    var instagramIntent = Intent(Intent.ACTION_VIEW, profileUrl.toUri())
    instagramIntent.setPackage("com.instagram.android")
    Log.d(tag, "Tentando abrir Instagram com setPackage: ${instagramIntent.data}")

    if (instagramIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para com.instagram.android, iniciando...")
        try {
            context.startActivity(instagramIntent)
            return // Sucesso
        } catch (e: Exception) {
            Log.e(tag, "Erro ao iniciar activity com setPackage para com.instagram.android: ${e.message}")
            // Se falhar, prossegue para a tentativa genérica
        }
    } else {
        Log.d(tag, "Nenhuma activity encontrada para com.instagram.android com setPackage.")
    }

    // Tentativa 2: Intent HTTPS genérica (fallback para navegador ou se o app Instagram responder genericamente)
    // Esta é a mesma lógica da abrirUrlGenerica, mas específica para o fluxo do Instagram.
    Log.d(tag, "Tentando abrir Instagram com intent HTTPS genérica: $profileUrl")
    instagramIntent = Intent(Intent.ACTION_VIEW, profileUrl.toUri()) // Recria sem setPackage
    if (instagramIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para URL HTTPS genérica do Instagram, iniciando...")
        try {
            context.startActivity(instagramIntent)
        } catch (e: Exception) {
            Log.e(tag, "Erro ao abrir URL HTTPS do Instagram (genérico): ${e.message}")
            Toast.makeText(context, "Erro ao tentar abrir o Instagram.", Toast.LENGTH_SHORT).show()
        }
    } else {
        Log.w(tag, "Nenhuma activity encontrada para URL HTTPS do Instagram (genérico): $profileUrl")
        Toast.makeText(context, "Não foi possível abrir o Instagram. Verifique se está instalado ou se há um navegador.", Toast.LENGTH_SHORT).show()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFaleConosco(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fale Conosco") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Entre em contato conosco!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ContactItem(
                icon = Icons.Filled.Email,
                text = "E-mail: $EMAIL_EMPRESA",
                contentDescription = "Enviar e-mail para $EMAIL_EMPRESA"
            ) {
                abrirEmail(context, EMAIL_EMPRESA)
            }

            ContactItem(
                icon = Icons.Filled.Phone,
                text = "Telefone: $NUMERO_TELEFONE_DISCADOR",
                contentDescription = "Ligar para $NUMERO_TELEFONE_DISCADOR"
            ) {
                abrirDiscador(context, NUMERO_TELEFONE_DISCADOR)
            }

            ContactItem(
                icon = Icons.AutoMirrored.Filled.Chat,
                text = "WhatsApp: $TELEFONE_EMPRESA",
                contentDescription = "Conversar pelo WhatsApp com $TELEFONE_EMPRESA"
            ) {
                abrirWhatsApp(context, TELEFONE_EMPRESA)
            }

            ContactItem(
                icon = Icons.Filled.CameraAlt, // Placeholder, idealmente use o logo do Instagram
                text = "Instagram: @nexpartoficial",
                contentDescription = "Acessar Instagram Nexpart Oficial"
            ) {
                abrirInstagram(context, INSTAGRAM_URL) // Chama a nova função
            }

            ContactItem(
                icon = Icons.Filled.Language,
                text = "Nosso Site: nexpart.com.br",
                contentDescription = "Acessar site Nexpart"
            ) {
                abrirUrlGenerica(context, WEBSITE_URL) // Mantém usando a genérica para o site
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Corrigido

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Aguardamos seu contato!",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ContactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    contentDescription: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = icon, contentDescription = contentDescription)
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}