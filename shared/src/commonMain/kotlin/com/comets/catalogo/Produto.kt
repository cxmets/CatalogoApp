package com.comets.catalogo

import kotlinx.serialization.Serializable

@Serializable
data class Produto(
    val codigo: String,
    val nome: String,
    val descricao: String,
    val tipo: String,
    val lente: String,
    val haste: String,
    val rosca: String,
    val imagemUrl: String,
    val detalhes: String,
    val apelido: String = ""
)