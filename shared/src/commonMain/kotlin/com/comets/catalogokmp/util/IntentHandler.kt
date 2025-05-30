package com.comets.catalogokmp.util

expect class IntentHandler {
    fun abrirEmail(email: String, assunto: String)
    fun abrirDiscador(numero: String)
    fun abrirWhatsApp(numeroCompletoComCodigoPais: String, mensagemPadrao: String)
    fun abrirUrlGenerica(url: String)
    fun abrirInstagram(profileUrl: String)
    fun showToast(message: String)
}