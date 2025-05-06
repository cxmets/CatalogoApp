package com.comets.catalogo

data class Produto(
    val codigo: String,
    val nome: String,
    val descricao: String,
    val categoria: String,
    val categoria2: String,
    val imagemUrl: String,
    val detalhes: String,
    val apelido: String = ""
)