package com.example.facial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.facial.data.entity.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleActivity : AppCompatActivity(){

//    private val api by lazy { ApiRetrofit().endpoint }
//
//    private lateinit var articleAdapter: ArticleAdapter
//    private lateinit var userList: List<User>
//    private lateinit var articleList: ArrayList<ArticleModel.Data>
//    private lateinit var listArticle: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
//        setupList()
//
//        val actionbar = supportActionBar
//        //set actionbar title
//        actionbar!!.title = "Article"
//        //set back button
//        actionbar.setDisplayHomeAsUpEnabled(true)
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//
//        bottomNavigationView.selectedItemId = R.id.page_2
//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            when(item.itemId) {
//                R.id.page_1 -> {
//                    val intent = Intent(this, BreathActivity::class.java)
//                    startActivity(intent)
//                    true  // Return true to consume the item selection
//                }
//                R.id.page_2 -> {
//                    startActivity(Intent(this, ArticleActivity::class.java))
//                    true
//                }
//                R.id.page_3 -> {
//                    startActivity(Intent(this, SecondActivity::class.java))
//                    true
//                }
//                R.id.page_4 -> {
//                    startActivity(Intent(this, CalendarActivity::class.java))
//                    true
//                }
//                R.id.page_5 -> {
//                    startActivity(Intent(this, BreathActivity::class.java))
//                    true
//                }
//                else ->  false
//            }
//        }

    }

//    override fun onStart(){
////        super.onStart()
////        getArticle()
//    }
//
//    override fun onItemClick(position: Int) {
////        // Handle item click here
////        val clickedArticle = articleAdapter.articles[position]
////
////
////        val intent = Intent(this, ViewActivity::class.java)
////
////        intent.putExtra("title", clickedArticle.title)
////        intent.putExtra("body", clickedArticle.body)
////
////        startActivity(intent)
//
//    }
//
//    private fun setupList(view: View){
////        listArticle = view.findViewById(R.id.list_article)
////        val articleAdapter = ArticleAdapter(userList, articleList, this)
////        listArticle.adapter = articleAdapter
//    }
//
//
//    private fun getArticle(){
////        api.data().enqueue(object : Callback<ArticleModel>{
////            override fun onFailure(call: Call<ArticleModel>, t: Throwable) {
////                Log.e("ArticleActivity", t.toString())
////            }
////
////            override fun onResponse(call: Call<ArticleModel>, response: Response<ArticleModel>) {
////                if(response.isSuccessful){
////                    val listData = response.body()!!.articles
////                    articleAdapter.setDatas(listData)
////                }
////            }
////
////
////
////        })
////    }



}