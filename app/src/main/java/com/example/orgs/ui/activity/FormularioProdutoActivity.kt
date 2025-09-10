package com.example.orgs.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.orgs.dao.ProdutosDao
import com.example.orgs.databinding.ActivityFormularioProdutoBinding
import com.example.orgs.module.Produto
import java.io.File
import java.math.BigDecimal


class FormularioProdutoActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFormularioProdutoBinding.inflate(layoutInflater) }
    private val dao = ProdutosDao()
    private var imagemUri: String? = null

    // dentro da Activity (propriedade)
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uriString = result.data?.getStringExtra(CameraActivity.RESULT_URI)
            uriString?.let {
                imagemUri = it
                binding.activityFormularioProdutoImagemPreview.setImageURI(Uri.parse(it))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)

        // Botão para abrir a câmera
        binding.activityFormularioProdutoBotaoSelecionarFoto.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            cameraLauncher.launch(intent)
        }

        // Botão salvar
        binding.activityFormularioProdutoBotaoSalvar.setOnClickListener {
            val produtoNovo = criaProduto()
            dao.adiciona(produtoNovo)
            finish()
        }
    }

    private fun criaProduto(): Produto = with(binding) {
        val nome = activityFormularioProdutoNome.text.toString()
        val descricao = activityFormularioProdutoDescricao.text.toString()
        val valor = activityFormularioProdutoValor.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO

        Produto(nome = nome, descricao = descricao, valor = valor, imagemUri = imagemUri)
    }
}
