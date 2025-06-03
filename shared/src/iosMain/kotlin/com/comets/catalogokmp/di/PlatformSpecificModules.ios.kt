package com.comets.catalogokmp.di

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyle
import platform.UIKit.UIWindow
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class IntentHandler {

    private fun openUrl(urlString: String): Boolean {
        val url = NSURL(string = urlString)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
            return true
        }
        println("KMP/iOS: Não foi possível abrir a URL: $urlString")
        return false
    }

    actual fun abrirEmail(email: String, assunto: String) {
        val encodedAssunto = assunto.encodeForUrl()
        val urlString = "mailto:$email?subject=$encodedAssunto"
        if (!openUrl(urlString)) {
            showToast("Não foi possível abrir o app de e-mail.")
        }
    }

    actual fun abrirDiscador(numero: String) {
        val urlString = "tel:$numero"
        if (!openUrl(urlString)) {
            showToast("Não foi possível abrir o discador.")
        }
    }

    actual fun abrirWhatsApp(numeroCompletoComCodigoPais: String, mensagemPadrao: String) {
        val numeroFiltrado = numeroCompletoComCodigoPais.filter { it.isDigit() }
        val encodedMensagem = mensagemPadrao.encodeForUrl()
        val urlString = "whatsapp://send?phone=$numeroFiltrado&text=$encodedMensagem"
        if (!openUrl(urlString)) {
            // Fallback para WhatsApp Web se o app não estiver instalado (raro no iOS, mas para consistência)
            val webUrlString = "https://api.whatsapp.com/send?phone=$numeroFiltrado&text=$encodedMensagem"
            if(!openUrl(webUrlString)){
                showToast("WhatsApp não instalado ou erro ao abrir.")
            }
        }
    }

    actual fun abrirUrlGenerica(url: String) {
        if (!openUrl(url)) {
            showToast("Não foi possível abrir o link.")
        }
    }

    actual fun abrirInstagram(profileUrl: String) {
        // Tenta abrir diretamente no app do Instagram
        val appUrlString = profileUrl.replace("https://www.instagram.com/", "instagram://user?username=")
            .removeSuffix("/")

        val nsAppUrl = NSURL(string = appUrlString)
        if (nsAppUrl != null && UIApplication.sharedApplication.canOpenURL(nsAppUrl)) {
            UIApplication.sharedApplication.openURL(nsAppUrl)
        } else {
            // Fallback para abrir no navegador
            if (!openUrl(profileUrl)) {
                showToast("Não foi possível abrir o Instagram.")
            }
        }
    }

    actual fun showToast(message: String) {
        // Toasts não são nativos do iOS. Usando UIAlertController para feedback simples.
        // Para uma experiência melhor, uma biblioteca de Toasts ou UI customizada seria ideal.
        dispatch_async(dispatch_get_main_queue()) {
            val window: UIWindow? = UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
            val rootViewController = window?.rootViewController
            if (rootViewController != null) {
                val alertController = UIAlertController.alertControllerWithTitle(
                    title = null,
                    message = message,
                    preferredStyle = UIAlertControllerStyle.UIAlertControllerStyleAlert
                )
                alertController.addAction(
                    UIAlertAction.actionWithTitle(
                        title = "OK",
                        style = UIAlertActionStyle.UIAlertActionStyleDefault,
                        handler = null
                    )
                )
                rootViewController.presentViewController(alertController, animated = true, completion = null)

                // Auto-dismiss after a delay (optional)
                // val popTime = dispatch_time(DISPATCH_TIME_NOW, (2 * NSEC_PER_SEC).toLong())
                // dispatch_after(popTime, dispatch_get_main_queue()) {
                //     alertController.dismissViewControllerAnimated(true, null)
                // }
            } else {
                println("KMP/iOS Toast: $message (RootViewController não encontrado)")
            }
        }
    }

    private fun String.encodeForUrl(): String {
        // Implementação simples de URL encoding. Para casos complexos, usar uma lib ou API nativa mais robusta.
        return this.replace(" ", "%20")
            .replace("!", "%21")
            .replace("#", "%23")
            .replace("$", "%24")
            .replace("&", "%26")
            .replace("'", "%27")
            .replace("(", "%28")
            .replace(")", "%29")
            .replace("*", "%2A")
            .replace("+", "%2B")
            .replace(",", "%2C")
            .replace("/", "%2F")
            .replace(":", "%3A")
            .replace(";", "%3B")
            .replace("=", "%3D")
            .replace("?", "%3F")
            .replace("@", "%40")
            .replace("[", "%5B")
            .replace("]", "%5D")
    }
}