package com.comets.catalogokmp.util

import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal actual fun abrirEmailPlatform(email: String, assunto: String) {
    println("JVM: Tentando abrir email para $email com assunto $assunto")
    try {
        val desktop = Desktop.getDesktop()
        val uri = URI("mailto:$email?subject=${URLEncoder.encode(assunto, StandardCharsets.UTF_8.name())}")
        if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.MAIL)) {
            desktop.mail(uri)
        } else {
            println("JVM: Desktop.Action.MAIL não é suportado ou Desktop não é suportado.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

internal actual fun abrirDiscadorPlatform(numero: String) {
    println("JVM: Abrir discador não é aplicável na JVM. Número: $numero")
    // Não aplicável em desktop padrão
}

internal actual fun abrirWhatsAppPlatform(numeroCompletoComCodigoPais: String, mensagemPadrao: String) {
    println("JVM: Tentando abrir WhatsApp para $numeroCompletoComCodigoPais")
    try {
        val numeroFiltrado = numeroCompletoComCodigoPais.filter { it.isDigit() }
        val url = "https://wa.me/$numeroFiltrado?text=${URLEncoder.encode(mensagemPadrao, StandardCharsets.UTF_8.name())}"
        val desktop = Desktop.getDesktop()
        if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI(url))
        } else {
            println("JVM: Desktop.Action.BROWSE não é suportado ou Desktop não é suportado.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

internal actual fun abrirUrlGenericaPlatform(url: String) {
    println("JVM: Tentando abrir URL $url")
    try {
        val desktop = Desktop.getDesktop()
        if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI(url))
        } else {
            println("JVM: Desktop.Action.BROWSE não é suportado ou Desktop não é suportado.")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

internal actual fun abrirInstagramPlatform(profileUrl: String) {
    println("JVM: Tentando abrir Instagram URL $profileUrl")
    abrirUrlGenericaPlatform(profileUrl) // Reutiliza a abertura de URL genérica
}

internal actual fun showToastPlatform(message: String) {
    println("JVM Toast: $message")
    // Toasts não são um conceito padrão em JVM desktop, apenas logamos.
}