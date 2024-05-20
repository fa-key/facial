package com.example.facial.fer

import android.content.Context
import android.graphics.*
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp

private const val MODEL_FILE_NAME = "fer_rms.tflite"
private const val LABELS_FILE_NAME = "fer_model.names"

private const val INPUT_IMAGE_WIDTH = 150
private const val INPUT_IMAGE_HEIGHT = 150
private const val N_CLASSES = 3

object FerModel {

    private lateinit var interpreter: Interpreter
    private lateinit var labels: List<String>

    fun load(context: Context): Interpreter {
        if (!this::interpreter.isInitialized) {
            synchronized(FerModel::class.java) {
                interpreter = loadModelFromAssets(context)
                labels = loadLabelsFromAssets(context)
            }
        }
        return interpreter
    }

    fun classify(inputImage: Bitmap): String {
        val input = Bitmap.createScaledBitmap(
            inputImage,
            INPUT_IMAGE_WIDTH,
            INPUT_IMAGE_HEIGHT,
            true
        )
            .toGrayscale()
            .toByteBuffer()

        return predict(input).toPrediction().toLabel()
    }

    private fun loadModelFromAssets(context: Context): Interpreter {
        val model = context.assets.open(MODEL_FILE_NAME).readBytes()
        val buffer = ByteBuffer.allocateDirect(model.size).order(ByteOrder.nativeOrder())
        buffer.put(model)
        return Interpreter(buffer)
    }

    private fun loadLabelsFromAssets(context: Context): List<String> =
        context.assets.open(LABELS_FILE_NAME)
            .bufferedReader()
            .useLines { it.toList() }

    private fun Bitmap.toGrayscale(): Bitmap {
        val grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(grayBitmap)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(this, 0f, 0f, paint)
        return grayBitmap
    }

    private fun Bitmap.toByteBuffer(): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(INPUT_IMAGE_WIDTH * INPUT_IMAGE_HEIGHT * 3 * java.lang.Float.SIZE / java.lang.Byte.SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(INPUT_IMAGE_WIDTH * INPUT_IMAGE_HEIGHT)
        getPixels(pixels, 0, width, 0, 0, width, height)
        for (pixel in pixels) {
            val r = Color.red(pixel) / 255.0f
            val g = Color.green(pixel) / 255.0f
            val b = Color.blue(pixel) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }
        byteBuffer.rewind()
        return byteBuffer
    }

    private fun predict(input: ByteBuffer): FloatArray {
        val outputByteBuffer = ByteBuffer.allocateDirect(N_CLASSES * java.lang.Float.SIZE / java.lang.Byte.SIZE)
            .order(ByteOrder.nativeOrder())

        interpreter.run(input, outputByteBuffer)

        val logits = FloatArray(N_CLASSES)
        outputByteBuffer.rewind()
        outputByteBuffer.asFloatBuffer().get(logits)

        return logits
    }

    private fun FloatArray.toPrediction(): Int {
        // Softmax
        val probabilities = this.map { exp(it.toDouble()) }
        val sumProb = probabilities.sum()
        val normalizedProbabilities = probabilities.map { it / sumProb }

        // Class with max probability
        val maxIndex = normalizedProbabilities.indices.maxByOrNull { normalizedProbabilities[it] } ?: -1
        return maxIndex
    }

    private fun Int.toLabel() = labels.getOrElse(this) { "Unknown" }
}

