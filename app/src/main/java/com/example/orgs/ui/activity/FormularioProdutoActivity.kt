package com.example.orgs.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.orgs.dao.ProdutosDao
import com.example.orgs.databinding.ActivityFormularioProdutoBinding
import com.example.orgs.module.Produto
import java.io.File
import java.math.BigDecimal

class FormularioProdutoActivity : AppCompatActivity() {

    private val binding by lazy { ActivityFormularioProdutoBinding.inflate(layoutInflater) }
    private val dao = ProdutosDao()
    private var imagemUri: String? = null
    private lateinit var fotoFile: File

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
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
        val valor = activityFormularioProdutoValor.text.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO

        Produto(nome = nome, descricao = descricao, valor = valor, imagemUri = imagemUri)
    }

    private fun abrirCamera() {
        // Criar arquivo temporário para a foto
        fotoFile = File.createTempFile("produto_", ".jpg", cacheDir)
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", fotoFile)

        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Atualiza o preview da imagem
            imagemUri = fotoFile.absolutePath
            binding.activityFormularioProdutoImagemPreview.setImageURI(Uri.fromFile(fotoFile))
        }
    }
}
