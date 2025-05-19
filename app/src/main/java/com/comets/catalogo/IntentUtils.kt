package com.comets.catalogo // Ou com.comets.catalogo.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri

fun abrirEmail(context: Context, email: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.fale_conosco_assunto_email_padrao))
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("IntentUtils", "Erro ao abrir e-mail: ${e.message}")
            Toast.makeText(context, context.getString(R.string.erro_tentar_abrir_email), Toast.LENGTH_LONG).show()
        }
    } else {
        Log.w("IntentUtils", "Nenhum app de e-mail encontrado.")
        Toast.makeText(context, context.getString(R.string.nenhum_app_email_encontrado), Toast.LENGTH_SHORT).show()
    }
}

fun abrirDiscador(context: Context, numero: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply { data = "tel:$numero".toUri() }
    if (intent.resolveActivity(context.packageManager) != null) {
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("IntentUtils", "Erro ao abrir discador: ${e.message}")
            Toast.makeText(context, context.getString(R.string.erro_tentar_abrir_discador), Toast.LENGTH_LONG).show()
        }
    } else {
        Log.w("IntentUtils", "Nenhum app de discagem encontrado.")
        Toast.makeText(context, context.getString(R.string.nenhum_app_discagem_encontrado), Toast.LENGTH_SHORT).show()
    }
}

private fun tryAbrirWhatsAppWeb(context: Context, numeroFiltrado: String, mensagem: String) {
    val tag = "WhatsAppUtils"
    val webUrl = "https://api.whatsapp.com/send?phone=$numeroFiltrado&text=${Uri.encode(mensagem)}"
    Log.d(tag, "URL api.whatsapp.com (fallback): $webUrl")
    val webIntent = Intent(Intent.ACTION_VIEW, webUrl.toUri())
    if (webIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para api.whatsapp.com, iniciando...")
        try {
            context.startActivity(webIntent)
        } catch (e: Exception) {
            Log.e(tag, "Erro ao iniciar activity para api.whatsapp.com: ${e.message}")
            Toast.makeText(context, context.getString(R.string.erro_tentar_abrir_whatsapp), Toast.LENGTH_LONG).show()
        }
    } else {
        Log.w(tag, "Nenhuma activity encontrada para api.whatsapp.com (fallback).")
        Toast.makeText(context, context.getString(R.string.whatsapp_nao_encontrado_ou_navegador), Toast.LENGTH_LONG).show()
    }
}

fun abrirWhatsApp(context: Context, numeroCompletoComCodigoPais: String, mensagemPadrao: String) {
    val tag = "WhatsAppUtils"
    val numeroFiltrado = numeroCompletoComCodigoPais.filter { it.isDigit() }
    Log.d(tag, "Tentando abrir WhatsApp com número (filtrado): $numeroFiltrado")
    val urlWaMe = "https://wa.me/$numeroFiltrado?text=${Uri.encode(mensagemPadrao)}"

    var whatsAppIntent = Intent(Intent.ACTION_VIEW, urlWaMe.toUri())
    whatsAppIntent.setPackage("com.whatsapp")
    Log.d(tag, "Tentando intent direta para com.whatsapp com URL: ${whatsAppIntent.data}")
    if (whatsAppIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para com.whatsapp (direto), iniciando...")
        try {
            context.startActivity(whatsAppIntent); return
        } catch (e: Exception) {
            Log.e(tag, "Erro ao iniciar activity direta para com.whatsapp: ${e.message}")
        }
    } else {
        Log.d(tag, "Nenhuma activity encontrada para com.whatsapp (direto) com wa.me.")
    }

    Log.d(tag, "Tentando URL wa.me genérica: $urlWaMe")
    whatsAppIntent = Intent(Intent.ACTION_VIEW, urlWaMe.toUri()) // Recria sem setPackage
    if (whatsAppIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para wa.me genérico, iniciando...")
        try {
            context.startActivity(whatsAppIntent); return
        } catch (e: Exception) {
            Log.e(tag, "Erro ao iniciar activity para wa.me genérico: ${e.message}")
        }
    } else {
        Log.d(tag, "Nenhuma activity encontrada para wa.me genérico.")
    }

    Log.d(tag, "Nenhuma das tentativas com wa.me funcionou. Tentando fallback web.")
    tryAbrirWhatsAppWeb(context, numeroFiltrado, mensagemPadrao)
}

fun abrirUrlGenerica(context: Context, url: String) {
    val tag = "GenericUrlUtils"
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
    val tag = "InstagramUtils"
    var instagramIntent = Intent(Intent.ACTION_VIEW, profileUrl.toUri())
    instagramIntent.setPackage("com.instagram.android") // Tenta o app Instagram primeiro
    Log.d(tag, "Tentando abrir Instagram com setPackage: ${instagramIntent.data}")
    if (instagramIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para com.instagram.android, iniciando...")
        try {
            context.startActivity(instagramIntent); return
        } catch (e: Exception) {
            Log.e(tag, "Erro ao iniciar activity com setPackage para com.instagram.android: ${e.message}")
        }
    } else {
        Log.d(tag, "Nenhuma activity encontrada para com.instagram.android com setPackage.")
    }

    Log.d(tag, "Tentando abrir Instagram com intent HTTPS genérica: $profileUrl")
    instagramIntent = Intent(Intent.ACTION_VIEW, profileUrl.toUri()) // Recria sem setPackage
    if (instagramIntent.resolveActivity(context.packageManager) != null) {
        Log.d(tag, "Activity encontrada para URL HTTPS genérica do Instagram, iniciando...")
        try {
            context.startActivity(instagramIntent)
        } catch (e: Exception) {
            Log.e(tag, "Erro ao abrir URL HTTPS do Instagram (genérico): ${e.message}")
            Toast.makeText(context, context.getString(R.string.erro_tentar_abrir_instagram), Toast.LENGTH_SHORT).show()
        }
    } else {
        Log.w(tag, "Nenhuma activity encontrada para URL HTTPS do Instagram (genérico): $profileUrl")
        Toast.makeText(context, context.getString(R.string.instagram_nao_encontrado_ou_navegador), Toast.LENGTH_SHORT).show()
    }
}