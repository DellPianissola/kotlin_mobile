package com.example.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.orgs.dao.ProdutosDao
import com.example.orgs.databinding.ActivityListProdutosBinding
import com.example.orgs.ui.recyclerview.adapter.ListaProdutosAdapter

class ListaProdutosActivity : AppCompatActivity() {

    private val dao by lazy { ProdutosDao() }
    private val adapter by lazy { ListaProdutosAdapter() }
    private val binding by lazy { ActivityListProdutosBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.activityListaProdutoRecyclerView.adapter = adapter
        binding.activityListaProdutoFab.setOnClickListener { vaiParaFormularioProduto() }
    }

    override fun onResume() {
        super.onResume()
        adapter.atualiza(dao.buscaTodos())
    }

    private fun vaiParaFormularioProduto() {
        startActivity(Intent(this, FormularioProdutoActivity::class.java))
    }
}
