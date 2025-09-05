package com.example.orgs.module

import java.math.BigDecimal
import java.util.UUID

data class Produto(
    val id: String = UUID.randomUUID().toString(), // chave única
    val nome: String,
    val descricao: String,
    val valor: BigDecimal,
    val imagemUri: String? = null // caminho ou URI da imagem
)
