package com.example.facial


import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class ViewActivity : AppCompatActivity() {
    private val api by lazy { ApiRetrofit().endpoint }
    private lateinit var articleAdapter: ArticleAdapter
    lateinit var title: TextView
    lateinit var article: TextView
    lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Dejour"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        val titleId = getIntent().getStringExtra("title")
        title = findViewById(R.id.title)
        title.textSize = 40f
        title.setText(titleId)

        val articleId = getIntent().getStringExtra("article")
        article = findViewById(R.id.article)
        article.textSize = 15f
        article.setText(articleId)

        val CategoryId = getIntent().getStringExtra("category")
        imageView = findViewById(R.id.ImageView)
        if (CategoryId=="angry") {
            imageView.setImageResource(R.drawable.angry_bg)
        }else if(CategoryId=="sad") {
            imageView.setImageResource(R.drawable.sad_bg)
        }else{
            imageView.setImageResource(R.drawable.happy_bg)
        }
    }
}