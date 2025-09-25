package com.example.orgs.ui.activity

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.orgs.dao.ProdutosDao
import com.example.orgs.databinding.ActivityDetalhesProdutoBinding

class DetalhesProdutoActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetalhesProdutoBinding.inflate(layoutInflater) }
    private val dao = ProdutosDao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)

        val produtoId = intent.getStringExtra("produto_id")
        val produto = dao.buscaTodos().find { it.id == produtoId }

        produto?.let {
            binding.detalhesProdutoNome.text = it.nome
            binding.detalhesProdutoDescricao.text = it.descricao
            binding.detalhesProdutoValor.text = it.valor.toPlainString()

            if (!it.imagemUri.isNullOrEmpty()) {
                binding.detalhesProdutoImagem.setImageURI(Uri.parse(it.imagemUri))
            }
        }

        binding.detalhesProdutoBotaoVoltar.setOnClickListener {
            finish()
        }
    }
}
