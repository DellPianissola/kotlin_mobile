package com.example.orgs.ui.recyclerview.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.orgs.databinding.ProdutoItemBinding
import com.example.orgs.module.Produto

class ListaProdutosAdapter(
    private val onItemClick: (Produto) -> Unit // callback de clique
) : ListAdapter<Produto, ListaProdutosAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Produto>() {
            override fun areItemsTheSame(oldItem: Produto, newItem: Produto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Produto, newItem: Produto): Boolean {
                val valorIgual = oldItem.valor.compareTo(newItem.valor) == 0
                val imagemIgual = oldItem.imagemUri == newItem.imagemUri
                return oldItem.nome == newItem.nome &&
                        oldItem.descricao == newItem.descricao &&
                        valorIgual &&
                        imagemIgual
            }
        }
    }

    inner class ViewHolder(private val binding: ProdutoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun vincula(produto: Produto) = with(binding) {
            produtoItemNome.text = produto.nome
            produtoItemDescricao.text = produto.descricao
            produtoItemValor.text = produto.valor.toPlainString()

            // Exibe a imagem se tiver
            if (!produto.imagemUri.isNullOrEmpty()) {
                produtoItemImageView.setImageURI(Uri.parse(produto.imagemUri))
            } else {
                produtoItemImageView.setImageResource(android.R.color.transparent)
            }

            // clique no card
            root.setOnClickListener {
                onItemClick(produto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProdutoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.vincula(getItem(position))
    }

    fun atualiza(novosProdutos: List<Produto>) = submitList(novosProdutos.toList())
}
