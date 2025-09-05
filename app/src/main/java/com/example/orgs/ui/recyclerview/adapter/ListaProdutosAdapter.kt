package com.example.orgs.ui.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.orgs.databinding.ProdutoItemBinding
import com.example.orgs.module.Produto

class ListaProdutosAdapter :
    ListAdapter<Produto, ListaProdutosAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Produto>() {

            // Agora usamos o ID como identidade
            override fun areItemsTheSame(oldItem: Produto, newItem: Produto): Boolean {
                return oldItem.id == newItem.id
            }

            // Conteúdo igual? Compare campo a campo (id não conta, porque não muda)
            override fun areContentsTheSame(oldItem: Produto, newItem: Produto): Boolean {
                val valorIgual = oldItem.valor.compareTo(newItem.valor) == 0
                return oldItem.nome == newItem.nome &&
                        oldItem.descricao == newItem.descricao &&
                        valorIgual
            }
        }
    }

    class ViewHolder(private val binding: ProdutoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun vincula(produto: Produto) = with(binding) {
            produtoItemNome.text = produto.nome
            produtoItemDescricao.text = produto.descricao
            produtoItemValor.text = produto.valor.toPlainString()
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

    // API parecida com antes, mas aproveita o ListAdapter
    fun atualiza(novosProdutos: List<Produto>) = submitList(novosProdutos.toList())
}
