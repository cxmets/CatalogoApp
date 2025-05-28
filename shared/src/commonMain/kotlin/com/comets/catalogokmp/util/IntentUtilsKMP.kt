package com.comets.catalogokmp.util

// Constantes podem permanecer aqui ou ser movidas para um local mais específico se necessário
const val EMAIL_EMPRESA_VAL = "contato@nexpart.com.br"
const val TELEFONE_EMPRESA_VAL = "+551122071624" // Usado para WhatsApp com código do país
const val NUMERO_TELEFONE_DISCADOR_VAL = "1122072006" // Usado para discador local
const val INSTAGRAM_PROFILE_NAME = "@nexpartoficial"
const val INSTAGRAM_URL_VAL = "https://www.instagram.com/nexpartoficial/"
const val WEBSITE_NAME = "nexpart.com.br"
const val WEBSITE_URL_VAL = "https://nexpart.com.br/"


internal expect fun abrirEmailPlatform(email: String, assunto: String)
internal expect fun abrirDiscadorPlatform(numero: String)
internal expect fun abrirWhatsAppPlatform(numeroCompletoComCodigoPais: String, mensagemPadrao: String)
internal expect fun abrirUrlGenericaPlatform(url: String)
internal expect fun abrirInstagramPlatform(profileUrl: String)

// Opcional: Adicionar expects para mostrar toasts/mensagens de erro KMP
internal expect fun showToastPlatform(message: String)