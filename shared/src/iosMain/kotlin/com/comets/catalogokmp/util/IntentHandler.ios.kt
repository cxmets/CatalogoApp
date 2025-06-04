package com.comets.catalogokmp.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIWindow
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class IntentHandler {

    private fun openUrl(urlString: String): Boolean {
        val url = NSURL(string = urlString)
        if (UIApplication.sharedApplication.canOpenURL(url)) {
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
        val appUrlString = profileUrl.replace("https://www.instagram.com/", "instagram://user?username=")
            .removeSuffix("/")

        val nsAppUrl = NSURL(string = appUrlString)
        if (UIApplication.sharedApplication.canOpenURL(nsAppUrl)) {
            UIApplication.sharedApplication.openURL(nsAppUrl)
        } else {
            if (!openUrl(profileUrl)) {
                showToast("Não foi possível abrir o Instagram.")
            }
        }
    }

    actual fun showToast(message: String) {
        dispatch_async(dispatch_get_main_queue()) {
            val window: UIWindow? = UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow
            val rootViewController = window?.rootViewController
            if (rootViewController != null) {
                val alertController = UIAlertController.alertControllerWithTitle(
                    title = null,
                    message = message,
                    preferredStyle = UIAlertControllerStyleAlert
                )
                alertController.addAction(
                    UIAlertAction.actionWithTitle(
                        title = "OK",
                        style = UIAlertActionStyleDefault,
                        handler = null
                    )
                )
                rootViewController.presentViewController(alertController, animated = true, completion = null)
            } else {
                println("KMP/iOS Toast: $message (RootViewController não encontrado)")
            }
        }
    }

    private fun String.encodeForUrl(): String {
        var encoded = this
        val charMap = mapOf(
            " " to "%20", "!" to "%21", "#" to "%23", "$" to "%24", "&" to "%26", "'" to "%27",
            "(" to "%28", ")" to "%29", "*" to "%2A", "+" to "%2B", "," to "%2C", "/" to "%2F",
            ":" to "%3A", ";" to "%3B", "=" to "%3D", "?" to "%3F", "@" to "%40", "[" to "%5B", "]" to "%5D"
        )
        charMap.forEach { (k, v) -> encoded = encoded.replace(k, v) }
        return encoded
    }
}