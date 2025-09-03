package com.example.orgs.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orgs.R
import com.example.orgs.module.Produto
import com.example.orgs.ui.recyclerview.adapter.ListaProdutosAdapter
import java.math.BigDecimal


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = ListaProdutosAdapter(
            this, listOf(
                Produto(nome = "teste", descricao = "teste desc", valor = BigDecimal("19.99")),
                Produto(nome = "teste", descricao = "teste desc", valor = BigDecimal("19.99"))
            ),
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}