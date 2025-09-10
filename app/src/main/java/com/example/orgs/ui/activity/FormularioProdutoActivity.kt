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
import java.math.BigDecimal

class FormularioProdutoActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFormularioProdutoBinding.inflate(layoutInflater) }
    private val dao = ProdutosDao()
    private var imagemUri: String? = null

    // Launcher moderno para abrir CameraActivity e receber resultado
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagePath = result.data?.getStringExtra("photo_path")
            if (!imagePath.isNullOrEmpty()) {
                imagemUri = imagePath
                binding.activityFormularioProdutoImagemPreview.setImageURI(Uri.parse(imagePath))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.activityFormularioProdutoBotaoSelecionarFoto.setOnClickListener {
            abrirCamera()
        }

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

        Produto(nome = nome, descricao = descricao, valor = valor, imagemUri = imagemUri)
    }

    private fun abrirCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        cameraLauncher.launch(intent)
    }
}
