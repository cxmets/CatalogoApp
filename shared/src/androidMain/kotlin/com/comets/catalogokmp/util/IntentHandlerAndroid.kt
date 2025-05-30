package com.comets.catalogokmp.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri

actual class IntentHandler(private val context: Context) { // Construtor sem 'actual' aqui

    actual fun abrirEmail(email: String, assunto: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, assunto)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (_: Exception) {
                showToast("Erro ao tentar abrir o e-mail.")
            }
        } else {
            showToast("Nenhum aplicativo de e-mail encontrado.")
        }
    }

    actual fun abrirDiscador(numero: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply { data = "tel:$numero".toUri() }
        if (intent.resolveActivity(context.packageManager) != null) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (_: Exception) {
                showToast("Erro ao tentar abrir o discador.")
            }
        } else {
            showToast("Não foi possível abrir o discador.")
        }
    }

    private fun tryAbrirWhatsAppWeb(numeroFiltrado: String, mensagem: String) {
        val webUrl = "https://api.whatsapp.com/send?phone=$numeroFiltrado&text=${Uri.encode(mensagem)}"
        val webIntent = Intent(Intent.ACTION_VIEW, webUrl.toUri())
        if (webIntent.resolveActivity(context.packageManager) != null) {
            try {
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (_: Exception) {
                showToast("Erro ao tentar abrir o WhatsApp.")
            }
        } else {
            showToast("Não foi possível abrir o WhatsApp. Verifique se está instalado ou se há um navegador disponível.")
        }
    }

    actual fun abrirWhatsApp(numeroCompletoComCodigoPais: String, mensagemPadrao: String) {
        val numeroFiltrado = numeroCompletoComCodigoPais.filter { it.isDigit() }
        val urlWaMe = "https://wa.me/$numeroFiltrado?text=${Uri.encode(mensagemPadrao)}"

        var whatsAppIntent = Intent(Intent.ACTION_VIEW, urlWaMe.toUri())
        whatsAppIntent.setPackage("com.whatsapp")
        if (whatsAppIntent.resolveActivity(context.packageManager) != null) {
            try {
                whatsAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(whatsAppIntent); return
            } catch (_: Exception) { /* fallback */ }
        }

        whatsAppIntent = Intent(Intent.ACTION_VIEW, urlWaMe.toUri())
        if (whatsAppIntent.resolveActivity(context.packageManager) != null) {
            try {
                whatsAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(whatsAppIntent); return
            } catch (_: Exception) { /* fallback */ }
        }
        tryAbrirWhatsAppWeb(numeroFiltrado, mensagemPadrao)
    }

    actual fun abrirUrlGenerica(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply { data = url.toUri() }
        if (intent.resolveActivity(context.packageManager) != null) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (_: Exception) {
                showToast("Erro ao abrir o link.")
            }
        } else {
            showToast("Não foi possível abrir o link (navegador ou app não encontrado?).")
        }
    }

    actual fun abrirInstagram(profileUrl: String) {
        var instagramIntent = Intent(Intent.ACTION_VIEW, profileUrl.toUri())
        instagramIntent.setPackage("com.instagram.android")
        if (instagramIntent.resolveActivity(context.packageManager) != null) {
            try {
                instagramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(instagramIntent); return
            } catch (_: Exception) { /* fallback */ }
        }
        instagramIntent = Intent(Intent.ACTION_VIEW, profileUrl.toUri())
        if (instagramIntent.resolveActivity(context.packageManager) != null) {
            try {
                instagramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(instagramIntent)
            } catch (_: Exception) {
                showToast("Erro ao tentar abrir o Instagram.")
            }
        } else {
            showToast("Não foi possível abrir o Instagram. Verifique se está instalado ou se há um navegador.")
        }
    }

    actual fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}