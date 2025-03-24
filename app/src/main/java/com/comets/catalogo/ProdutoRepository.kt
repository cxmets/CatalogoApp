package com.comets.catalogo

object ProdutoRepository {
    fun getProdutos(): List<Produto> {
        return listOf(
            Produto(
                nome = "Retrovisor Biz 125",
                descricao = "Retrovisor Biz 125",
                categoria = "Retrovisores",
                categoria2 = "Honda",
                imagemUrl = "biz.png",
                codigo = "1109",
                detalhes = "detalhes teste"
            ),
            Produto(
                nome = "Retroisor Bmw F800",
                descricao = "Retrovisor Bmw Gs650 F800",
                categoria = "Retrovisores",
                categoria2 = "Honda",
                imagemUrl = "bmw.png",
                codigo = "1370",
                detalhes = "detalhes teste"
            ),
            Produto(
                nome = "Retrovisor Cb 300",
                descricao = "Retrovisor Cb 300",
                categoria = "Retrovisores",
                categoria2 = "Honda",
                imagemUrl = "cb300.png",
                codigo = "1148",
                detalhes = "detalhes teste"
            ),
            Produto(
                nome = "Retrovisor Z400",
                descricao = "Retrovisor Z400",
                categoria = "Retrovisores",
                categoria2 = "Yamaha",
                imagemUrl = "z400.png",
                codigo = "1412",
                detalhes = "detalhes teste"
            ),
            Produto(
                nome = "Retrovisor Factor/Nmax",
                descricao = "Retrovisor Factor / Nmax",
                categoria = "Retrovisores",
                categoria2 = "Yamaha",
                imagemUrl = "factor.png",
                codigo = "1158",
                detalhes = "detalhes teste"
            ),
            Produto(
                nome = "Retrovisor Titan 125",
                descricao = "Retrovisor Titan 125 - 2014",
                categoria = "Retrovisores",
                categoria2 = "Honda",
                imagemUrl = "titan125.png",
                codigo = "1106",
                detalhes = "detalhes teste"
            ),
            Produto(
                nome = "Retrovisor Titan 150",
                descricao = "Retrovisor Titan 150",
                categoria = "Retrovisores",
                categoria2 = "Honda",
                imagemUrl = "titan150.png",
                codigo = "1103",
                detalhes = "detalhes teste"
            )
        )
    }
}