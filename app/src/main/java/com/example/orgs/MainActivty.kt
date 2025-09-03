package com.example.orgs

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MainActivty : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val nome = findViewById<TextView>(R.id.nome)
//        nome.text = ("Cesta de Frutas")
//
//        val descricao = findViewById<TextView>(R.id.descricao)
//        descricao.text = ("Laranja, uva e maçã")
//
//        val valor = findViewById<TextView>(R.id.valor)
//        valor.text = ("19.99")
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = ListaProdutosAdapter()
    }
}