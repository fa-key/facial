package com.example.facial


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.facial.fer.FerModel
import com.example.facial.fer.FerViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.size.SizeSelectors
import husaynhakeem.io.facedetector.*

class PredictActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(FerViewModel::class.java)
    }

    lateinit var viewfinder: CameraView
    lateinit var faceBoundsOverlay: FaceBoundsOverlay
    lateinit var toggleCameraButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predict)

        viewfinder = findViewById(R.id.viewfinder)
        faceBoundsOverlay = findViewById(R.id.faceBoundsOverlay)
        toggleCameraButton = findViewById(R.id.toggleCameraButton)


        val lensFacing =
            savedInstanceState?.getSerializable(KEY_LENS_FACING) as Facing? ?: Facing.FRONT
        setupCamera(lensFacing)

        // Load model
        FerModel.load(this)

        setupObservers()

        // Post a delayed task to move to EditorActivity after 3 seconds
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
        outState.putSerializable(KEY_LENS_FACING, viewfinder.facing)
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
        viewfinder.setPreviewStreamSize(SizeSelectors.maxWidth(MAX_PREVIEW_WIDTH))
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

        toggleCameraButton.setOnClickListener {
            viewfinder.toggleFacing()
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
        val intent = Intent(this, EditorActivity::class.java)
        intent.putExtra("emotion", viewModel.logEmotionLabels())
        intent.putExtra("fromThisActivity", true)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val KEY_LENS_FACING = "key-lens-facing"
        private const val MAX_PREVIEW_WIDTH = 480
    }
}
