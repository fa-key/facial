package com.example.facial

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.facial.ml.LiteModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import android.Manifest
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    lateinit var selectBtn: Button
    lateinit var predBtn: Button
    lateinit var nextBtn: Button
    lateinit var calendarBtn: Button
    lateinit var chartBtn: Button
    lateinit var breathBtn: Button
    lateinit var resView: TextView
    lateinit var imageView: ImageView
    lateinit var bitmap: Bitmap
    private val cameraRequest = 1888
    var capturedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        selectBtn = findViewById(R.id.selectBtn)
        predBtn = findViewById(R.id.predictBtn)
        nextBtn = findViewById(R.id.next)
        calendarBtn = findViewById(R.id.calendar)
        chartBtn = findViewById(R.id.chart)
        breathBtn = findViewById(R.id.breath)
        resView = findViewById(R.id.resView)
        imageView = findViewById(R.id.ImageView)
        // Retrieve photo file path from intent extras
        val photoFilePath = intent.getStringExtra("PHOTO_FILE_PATH")
        val labeling = getIntent().getStringExtra("LABEL")



        // Load image into ImageView
        Glide.with(this).load(photoFilePath).into(imageView)
        resView.setText(labeling)

        var labels = application.assets.open("facial_labels.txt").bufferedReader().readLines()

        var imageProcessor = ImageProcessor.Builder()
//            .add(NormalizeOp(0.0f, 255.0f))
//            .add(TransformToGrayscaleOp())
            .add(ResizeOp(150,150, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraRequest)

        val photoButton: Button = findViewById(R.id.photoBtn)
        photoButton.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, cameraRequest)
        }

        selectBtn.setOnClickListener {
            var intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, 100)
        }

        predBtn.setOnClickListener {
            // Check if capturedBitmap is not null before proceeding
            capturedBitmap?.let { bitmap ->
                var tensorImage = TensorImage(DataType.FLOAT32)
                tensorImage.load(bitmap)

                // Rest of your code for processing the image and making predictions remains the same
                // Ensure 'imageProcessor' and 'labels' are defined somewhere accessible in the code.

                tensorImage = imageProcessor.process(tensorImage)
                val model = LiteModel.newInstance(this)

                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 48, 48, 3), DataType.FLOAT32)
                inputFeature0.loadBuffer(tensorImage.buffer)

                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

                var maxIdx = 0
                outputFeature0.forEachIndexed { index, fl ->
                    if (outputFeature0[maxIdx] < fl) {
                        maxIdx = index
                    }
                }

                resView.setText(labels[maxIdx])
                model.close()
            }
        }

        nextBtn.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        calendarBtn.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
        chartBtn.setOnClickListener {
            val intent = Intent(this, ChartActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
        breathBtn.setOnClickListener {
            val intent = Intent(this, BreathActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
        bottomNavigationView.selectedItemId = R.id.page_3
        bottomNavigationView.setOnNavigationItemSelectedListener  { item ->
            when(item.itemId) {
                R.id.page_1 -> {
                    val intent = Intent(this, BreathActivity::class.java)
                    startActivity(intent)
                    true  // Return true to consume the item selection
                }
                R.id.page_2-> {
                    startActivity(Intent(this, ArticleActivity::class.java))
                    true
                }
                R.id.page_3-> {
                // Respond to navigation item 2 click
                    val intent = Intent(this, ChartActivity::class.java)
                    // start your next activity
                    startActivity(intent)
                    true
                }
                R.id.page_4-> {
                    // Respond to navigation item 2 click
                    val intent = Intent(this, CalendarActivity::class.java)
                    // start your next activity
                    startActivity(intent)
                    true
                }

                else ->  false
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == cameraRequest) {
            val photo: Bitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(photo)
            capturedBitmap = photo
        }else if(requestCode == 100){
            var uri = data?.data;
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
            imageView.setImageBitmap(bitmap)
            capturedBitmap = bitmap
         }

    }


}



