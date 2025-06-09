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
import io.ktor.http.encodeURLQueryComponent // Importa a função de codificação do Ktor

actual class IntentHandler {

    private fun openUrl(urlString: String): Boolean {
        val url = NSURL(string = urlString)
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
            return true
        }
        return false
    }

    actual fun abrirEmail(email: String, assunto: String) {
        val encodedAssunto = assunto.encodeURLQueryComponent() // Usa a função do Ktor
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
        val encodedMensagem = mensagemPadrao.encodeURLQueryComponent() // Usa a função do Ktor
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
        val appSchemeUsername = profileUrl.substringAfterLast("instagram.com/").removeSuffix("/")
        val appUrlString = "instagram://user?username=$appSchemeUsername"

        val nsAppUrl = NSURL(string = appUrlString)
        if (nsAppUrl != null && UIApplication.sharedApplication.canOpenURL(nsAppUrl)) {
            UIApplication.sharedApplication.openURL(nsAppUrl)
        } else {
            if (!openUrl(profileUrl)) {
                showToast("Não foi possível abrir o Instagram.")
            }
        }
    }

    actual fun showToast(message: String) {
        dispatch_async(dispatch_get_main_queue()) {
            val window: UIWindow? = UIApplication.sharedApplication.windows.firstOrNull {
                (it as? UIWindow)?.isKeyWindow() == true
            } as? UIWindow

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
                var presenter = rootViewController
                while (presenter?.presentedViewController != null) {
                    presenter = presenter.presentedViewController
                }
                presenter?.presentViewController(alertController, animated = true, completion = null)
            }
        }
    }
}