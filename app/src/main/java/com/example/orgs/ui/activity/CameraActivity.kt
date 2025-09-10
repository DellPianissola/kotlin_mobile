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
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
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

    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var photoFile: File

    companion object {
        const val RESULT_URI = "RESULT_URI"
        private const val TAG = "CameraActivity"
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCameraInternal()
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.CameraPreview)
        previewImage = findViewById(R.id.previewImage)
        captureButton = findViewById(R.id.captureButton)
        closeButton = findViewById(R.id.closeCameraButton)
        confirmButton = findViewById(R.id.confirmButton)
        retakeButton = findViewById(R.id.retakeButton)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        captureButton.setOnClickListener {
            captureButton.isEnabled = false
            takePhoto()
        }

        closeButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        confirmButton.setOnClickListener {
            val intent = Intent().apply {
                putExtra(RESULT_URI, photoFile.absolutePath)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        retakeButton.setOnClickListener {
            // Deleta arquivo temporário se existir
            if (::photoFile.isInitialized && photoFile.exists()) {
                photoFile.delete()
            }
            previewImage.setImageDrawable(null)
            previewImage.visibility = View.GONE
            previewView.visibility = View.VISIBLE
            captureButton.visibility = View.VISIBLE
            confirmButton.visibility = View.GONE
            retakeButton.visibility = View.GONE

            // (re)inicia câmera se necessário
            startCameraInternal()
        }

        // Verifica permissão de câmera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            startCameraInternal()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCameraInternal() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Erro ao bind camera", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val fileName = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
        photoFile = File(outputDirectory, fileName)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Erro ao salvar imagem: ${exc.message}", exc)
                    captureButton.isEnabled = true
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // mostra preview e troca botões
                    previewView.visibility = View.GONE
                    previewImage.visibility = View.VISIBLE
                    previewImage.setImageURI(Uri.fromFile(photoFile))

                    captureButton.visibility = View.GONE
                    confirmButton.visibility = View.VISIBLE
                    retakeButton.visibility = View.VISIBLE
                    captureButton.isEnabled = true
                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
