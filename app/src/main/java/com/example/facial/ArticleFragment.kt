package com.example.facial

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.facial.data.AppDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 * Use the [ArticleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ArticleFragment : Fragment(), ArticleAdapter.OnItemClickListener {

    private val api by lazy { ApiRetrofit().endpoint }
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var listArticle: RecyclerView

    private lateinit var emotion: TextView
    lateinit var imageView: ImageView
    private lateinit var cvNote: CardView

    private lateinit var database: AppDatabase
    private var count_sad = 0
    private var count_happy = 0
    private var count_angry = 0
    var highestEmotion = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_article, container, false)
        setupList(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        getArticle()
    }

    override fun onItemClick(position: Int) {
        // Handle item click here
        val clickedArticle = articleAdapter.articles[position]

        val intent = Intent(requireContext(), ViewActivity::class.java)
        intent.putExtra("title", clickedArticle.title)
        intent.putExtra("article", clickedArticle.body)
        intent.putExtra("category", clickedArticle.category)

        startActivity(intent)
    }

    private fun setupList(view: View) {
        val context = requireContext()
        listArticle = view.findViewById(R.id.list_article)
        articleAdapter = ArticleAdapter(arrayListOf(), this)
        listArticle.adapter = articleAdapter

        emotion = view.findViewById(R.id.emotion)
        imageView = view.findViewById(R.id.ImageView)
        cvNote = view.findViewById(R.id.cvNote)


        database = AppDatabase.getInstance(requireContext())

        count_sad = 0
        count_happy = 0
        count_angry = 0


        val list = database.userDao().getAll()
        for (user in list) {
            when (user.emotion) {
                "sad" -> count_sad++
                "happy" -> count_happy++
                "angry" -> count_angry++
            }
        }

        highestEmotion = when {
            count_sad >= count_happy && count_sad >= count_angry -> "sad"
            count_happy >= count_sad && count_happy >= count_angry -> "happy"
            else -> "angry"
        }

        if (highestEmotion=="angry") {
            cvNote.setCardBackgroundColor(ContextCompat.getColor(context, R.color.angry_note))
            emotion.setText("Your dominant feeling is angry. I hope you got be more patient after read this.")
            imageView.setImageResource(R.drawable.angry_bg)
        }else if(highestEmotion=="sad") {
            cvNote.setCardBackgroundColor(ContextCompat.getColor(context, R.color.sad_note))
            emotion.setText("Your dominant feeling is sad. I hope you feel better after read this")
            imageView.setImageResource(R.drawable.sad_bg)
        }else{
            cvNote.setCardBackgroundColor(ContextCompat.getColor(context, R.color.happy_note))
            emotion.setText("Your dominant feeling is happy. I hope your brightness smile will shinee every day")
            imageView.setImageResource(R.drawable.happy_bg)
        }

    }

    private fun getArticle() {
        api.data().enqueue(object : Callback<ArticleModel> {
            override fun onFailure(call: Call<ArticleModel>, t: Throwable) {
                Log.e("ArticleFragment", t.toString())
            }

            override fun onResponse(call: Call<ArticleModel>, response: Response<ArticleModel>) {
                if (response.isSuccessful) {
                    val listData = response.body()?.articles ?: emptyList()
                    val happyArticles = listData.filter { article ->
                        article.category.equals(highestEmotion, ignoreCase = true)
                    }
                    articleAdapter.setDatas(happyArticles)
                }
            }
        })
    }


    companion object {
        @JvmStatic
        fun newInstance() = ArticleFragment()
    }
}
