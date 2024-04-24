package com.example.facial.fer

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import husaynhakeem.io.facedetector.FaceBounds

/**
 * Once it receives face pictures, it runs the model and returns prediction emotion labels
 */
class FerViewModel : ViewModel() {

    private val emotionLabels = MutableLiveData<Map<Int, String>>()
    fun emotionLabels(): LiveData<Map<Int, String>> = emotionLabels

    private var processing: Boolean = false

    fun onFacesDetected(faceBounds: List<FaceBounds>, faceBitmaps: List<Bitmap>) {
        if (faceBitmaps.isEmpty()) return

        synchronized(FerViewModel::class.java) {
            if (!processing) {
                processing = true
                Handler(Looper.getMainLooper()).post {
                    val emotionsMap = faceBounds.mapNotNull { it.id }
                        .zip(faceBitmaps)
                        .toMap()
                        .run { getEmotionsMap(this) }
                    emotionLabels.value = emotionsMap
                    logEmotionLabels()
                    processing = false
                }
            }
        }
    }

    /**
     * Given map of (faceId, faceBitmap), runs prediction on the model and
     * returns a map of (faceId, emotionLabel)
     */
    private fun getEmotionsMap(faceImages: Map<Int, Bitmap>): Map<Int, String> {
        val emotionLabels = faceImages.map { FerModel.classify(it.value) }
        return faceImages.keys.zip(emotionLabels).toMap()
    }

    fun logEmotionLabels(): String? {
        val emotions = emotionLabels.value ?: return null
        val labelCounts = emotions.values.groupingBy { it }.eachCount()
        val labeld = labelCounts.maxByOrNull { it.value }?.key
        Log.d("EmotionLabels", "Emotion Label: $labelCounts")
        return labeld
    }



}