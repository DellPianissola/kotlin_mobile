package com.example.orgs.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.orgs.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var previewImage: ImageView
    private lateinit var captureButton: ImageButton
    private lateinit var closeButton: ImageButton
    private lateinit var confirmButton: ImageButton
    private lateinit var retakeButton: ImageButton

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var photoFile: File? = null

    companion object {
        const val RESULT_URI = "RESULT_URI"
        private const val TAG = "CameraActivity"
    }

    // Launcher para pedir permissão
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera() else cancelAndClose()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_camera)

        setupUI()
        setupListeners()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        handlePermissions()
    }

    /** Inicializa os componentes visuais */
    private fun setupUI() {
        previewView = findViewById(R.id.CameraPreview)
        previewImage = findViewById(R.id.previewImage)
        captureButton = findViewById(R.id.captureButton)
        closeButton = findViewById(R.id.closeCameraButton)
        confirmButton = findViewById(R.id.confirmButton)
        retakeButton = findViewById(R.id.retakeButton)

        showCameraUI()
    }

    /** Configura os cliques dos botões */
    private fun setupListeners() {
        captureButton.setOnClickListener {
            captureButton.isEnabled = false
            takePhoto()
        }

        closeButton.setOnClickListener { cancelAndClose() }

        confirmButton.setOnClickListener {
            photoFile?.let {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(RESULT_URI, it.absolutePath)
                })
            }
            finish()
        }

        retakeButton.setOnClickListener {
            deleteTempPhoto()
            showCameraUI()
            startCamera()
        }
    }

    /** Verifica permissão da câmera */
    private fun handlePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /** Inicia a câmera */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetRotation(previewView.display.rotation)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Erro ao iniciar câmera", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /** Captura a foto */
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
        photoFile = File(outputDirectory, fileName)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile!!).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Erro ao salvar imagem: ${exc.message}", exc)
                    captureButton.isEnabled = true
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    showPreviewUI()
                    previewImage.setImageURI(Uri.fromFile(photoFile))
                    captureButton.isEnabled = true
                }
            })
    }

    /** Mostra UI da câmera */
    private fun showCameraUI() {
        previewView.visibility = View.VISIBLE
        previewImage.visibility = View.GONE

        captureButton.visibility = View.VISIBLE
        confirmButton.visibility = View.GONE
        retakeButton.visibility = View.GONE
    }

    /** Mostra UI do preview TESTE 2*/
    private fun showPreviewUI() {
        previewView.visibility = View.GONE
        previewImage.visibility = View.VISIBLE

        captureButton.visibility = View.GONE
        confirmButton.visibility = View.VISIBLE
        retakeButton.visibility = View.VISIBLE
    }

    /** Deleta foto temporária TESTE 2*/
    private fun deleteTempPhoto() {
        photoFile?.takeIf { it.exists() }?.delete()
        photoFile = null
    }

    /** Cancela e fecha a activity */
    private fun cancelAndClose() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    /** Retorna o diretório de saída das fotos */
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return mediaDir?.takeIf { it.exists() } ?: filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
