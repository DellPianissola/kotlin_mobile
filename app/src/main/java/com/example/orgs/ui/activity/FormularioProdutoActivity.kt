package com.example.orgs.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.orgs.dao.ProdutosDao
import com.example.orgs.databinding.ActivityFormularioProdutoBinding
import com.example.orgs.module.Produto
import java.math.BigDecimal

class FormularioProdutoActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFormularioProdutoBinding.inflate(layoutInflater) }
    private val dao = ProdutosDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.activityFormularioProdutoBotaoSalvar.setOnClickListener {
            val produtoNovo = criaProduto()
            dao.adiciona(produtoNovo)
            finish()
        }
    }

    private fun criaProduto(): Produto = with(binding) {
        val nome = activityFormularioProdutoNome.text.toString()
        val descricao = activityFormularioProdutoDescricao.text.toString()
        val valor = activityFormularioProdutoValor.text.toString()
            .toBigDecimalOrNull() ?: BigDecimal.ZERO

        Produto(nome = nome, descricao = descricao, valor = valor)
    }
}
