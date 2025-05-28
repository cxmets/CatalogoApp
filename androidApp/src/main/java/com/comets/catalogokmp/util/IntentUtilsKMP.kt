package com.comets.catalogokmp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
// Se você tiver um Res gerado específico para androidApp, senão use o compartilhado com cuidado para strings de erro
// ou passe o contexto e use context.getString() diretamente.

// Reutilize seu IntentUtils.kt original do androidApp
// Certifique-se que ele usa o `appContext` que definimos anteriormente ou recebe Context.

internal actual fun abrirEmailPlatform(email: String, assunto: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, assunto) // Assunto agora vem como parâmetro
    }
    if (intent.resolveActivity(appContext.packageManager) != null) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(intent)
        } catch (e: Exception) {
            showToastPlatform("Erro ao tentar abrir o e-mail.") // Usar string de recurso KMP
        }
    } else {
        showToastPlatform("Nenhum aplicativo de e-mail encontrado.") // Usar string de recurso KMP
    }
}

internal actual fun abrirDiscadorPlatform(numero: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply { data = "tel:$numero".toUri() }
    if (intent.resolveActivity(appContext.packageManager) != null) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(intent)
        } catch (e: Exception) {
            showToastPlatform("Erro ao tentar abrir o discador.")
        }
    } else {
        showToastPlatform("Não foi possível abrir o discador.")
    }
}

private fun tryAbrirWhatsAppWebPlatform(numeroFiltrado: String, mensagem: String) {
    val webUrl = "https://api.whatsapp.com/send?phone=$numeroFiltrado&text=${Uri.encode(mensagem)}"
    val webIntent = Intent(Intent.ACTION_VIEW, webUrl.toUri())
    if (webIntent.resolveActivity(appContext.packageManager) != null) {
        try {
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(webIntent)
        } catch (e: Exception) {
            showToastPlatform("Erro ao tentar abrir o WhatsApp.")
        }
    } else {
        showToastPlatform("Não foi possível abrir o WhatsApp. Verifique se está instalado ou se há um navegador disponível.")
    }
}

internal actual fun abrirWhatsAppPlatform(numeroCompletoComCodigoPais: String, mensagemPadrao: String) {
    val numeroFiltrado = numeroCompletoComCodigoPais.filter { it.isDigit() }
    val urlWaMe = "https://wa.me/$numeroFiltrado?text=${Uri.encode(mensagemPadrao)}"

    var whatsAppIntent = Intent(Intent.ACTION_VIEW, urlWaMe.toUri())
    whatsAppIntent.setPackage("com.whatsapp")
    if (whatsAppIntent.resolveActivity(appContext.packageManager) != null) {
        try {
            whatsAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(whatsAppIntent); return
        } catch (e: Exception) {
            // Log.e e fallback
        }
    }

    whatsAppIntent = Intent(Intent.ACTION_VIEW, urlWaMe.toUri()) // Recria sem setPackage
    if (whatsAppIntent.resolveActivity(appContext.packageManager) != null) {
        try {
            whatsAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(whatsAppIntent); return
        } catch (e: Exception) {
            // Log.e e fallback
        }
    }
    tryAbrirWhatsAppWebPlatform(numeroFiltrado, mensagemPadrao)
}

internal actual fun abrirUrlGenericaPlatform(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply { data = url.toUri() }
    if (intent.resolveActivity(appContext.packageManager) != null) {
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(intent)
        } catch (e: Exception) {
            showToastPlatform("Erro ao abrir o link.")
        }
    } else {
        showToastPlatform("Não foi possível abrir o link (navegador ou app não encontrado?).")
    }
}

internal actual fun abrirInstagramPlatform(profileUrl: String) {
    var instagramIntent = Intent(Intent.ACTION_VIEW, profileUrl.toUri())
    instagramIntent.setPackage("com.instagram.android")
    if (instagramIntent.resolveActivity(appContext.packageManager) != null) {
        try {
            instagramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(instagramIntent); return
        } catch (e: Exception) {
            // Log e fallback
        }
    }
    instagramIntent = Intent(Intent.ACTION_VIEW, profileUrl.toUri())
    if (instagramIntent.resolveActivity(appContext.packageManager) != null) {
        try {
            instagramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(instagramIntent)
        } catch (e: Exception) {
            showToastPlatform("Erro ao tentar abrir o Instagram.")
        }
    } else {
        showToastPlatform("Não foi possível abrir o Instagram. Verifique se está instalado ou se há um navegador.")
    }
}

internal actual fun showToastPlatform(message: String) {
    Toast.makeText(appContext, message, Toast.LENGTH_LONG).show()
}