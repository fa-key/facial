package com.example.facial


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.facial.fer.FerModel
import com.example.facial.fer.FerViewModel
import com.example.facial.ml.LiteModel
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.size.SizeSelectors
import husaynhakeem.io.facedetector.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.ops.ResizeOp


@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    lateinit var viewfinder: CameraView
    lateinit var faceBoundsOverlay: FaceBoundsOverlay
    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(FerViewModel::class.java)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        viewfinder = findViewById(R.id.viewfinder)
        faceBoundsOverlay = findViewById(R.id.faceBoundsOverlay)

        val lensFacing =
            savedInstanceState?.getSerializable(SplashActivity.KEY_LENS_FACING) as Facing? ?: Facing.FRONT
        setupCamera(lensFacing)

        FerModel.load(this)

        setupObservers()

        Handler(Looper.getMainLooper()).postDelayed({
            moveToEditorActivity()
        }, 5000)



    }


    override fun onStart() {
        super.onStart()
        viewfinder.open()
    }

    override fun onStop() {
        super.onStop()
        viewfinder.close()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(SplashActivity.KEY_LENS_FACING, viewfinder.facing)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewfinder.destroy()
    }

    private fun setupObservers() {
        viewModel.emotionLabels().observe(this, { emotions ->
            emotions?.let {
                faceBoundsOverlay.updateEmotionLabels(it)
                faceBoundsOverlay.invalidate()
            }
        })
    }

    private fun setupCamera(lensFacing: Facing) {
        val faceDetector = FaceDetector(faceBoundsOverlay).also { it.setup() }

        viewfinder.facing = lensFacing
        // Lower the frame resolution for better computation performance when working with face images
        viewfinder.setPreviewStreamSize(SizeSelectors.maxWidth(SplashActivity.MAX_PREVIEW_WIDTH))
        viewfinder.audio = Audio.OFF

        viewfinder.addFrameProcessor {
            faceDetector.process(
                Frame(
                    data = it.data,
                    rotation = it.rotation,
                    size = Size(it.size.width, it.size.height),
                    format = it.format,
                    lensFacing = LensFacing.FRONT
                )
            )
        }


    }

    private fun FaceDetector.setup() = run {
        setOnFaceDetectionListener(object : FaceDetector.OnFaceDetectionResultListener {
            override fun onSuccess(faceBounds: List<FaceBounds>, faceBitmaps: List<Bitmap>) {
                viewModel.onFacesDetected(faceBounds, faceBitmaps)
            }
        })
    }

    private fun moveToEditorActivity() {
        val intent = Intent(this, NavbarActivity::class.java)
        intent.putExtra("LABEL", viewModel.logEmotionLabels())
        startActivity(intent)
        finish()
    }
    companion object {
        private const val KEY_LENS_FACING = "key-lens-facing"
        private const val MAX_PREVIEW_WIDTH = 480
        private const val TAG = "SplashActivity"
    }
}



