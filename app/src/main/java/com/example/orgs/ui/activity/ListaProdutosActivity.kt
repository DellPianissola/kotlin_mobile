package com.example.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.orgs.R
import com.example.orgs.dao.ProdutosDao
import com.example.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ListaProdutosActivity : AppCompatActivity(R.layout.activity_list_produtos) {

    private val dao = ProdutosDao()
    private val adapter = ListaProdutosAdapter(this, dao.buscaTodos())
//    private val binding by lazy {
//        ActivityListaProdutosActivityBinding.inflate()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configuraRecylcerView()
    }

    override fun onResume() {
        super.onResume()
        adapter.atualiza(dao.buscaTodos())
        configuraFab()
    }

    private fun configuraRecylcerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.activity_lista_produto_recyclerView)
        recyclerView.adapter = adapter
    }

    private fun configuraFab() {
        val fab = findViewById<FloatingActionButton>(R.id.activity_lista_produto_fab)
        fab.setOnClickListener {
            vai_para_formulario_produto()
        }
    }

    private fun vai_para_formulario_produto() {
        val intent = Intent(this, FormularioProdutoActivity::class.java)
        startActivity(intent)
    }
}