package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class NewsPatientActivity : BaseActivity() {

    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_news)

        // Initialize views
        newsRecyclerView = findViewById(R.id.newsRecyclerView)
        progressBar = findViewById(R.id.progressBar)

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Setup RecyclerView
        newsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Show loading indicator
        progressBar.visibility = View.VISIBLE

        // Load news
        loadNews()

        setupBottomNavigation(R.id.nav_news)
    }

    private fun loadNews() {
        val apiKey = "2ee6a80f01e04cf9ac2c1ccbd842bf99"
        val url = "https://newsapi.org/v2/everything?q=health&language=en&sortBy=publishedAt&apiKey=2ee6a80f01e04cf9ac2c1ccbd842bf99"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@NewsPatientActivity, "Failed to load news", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@NewsPatientActivity, "Unexpected response", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    val body = response.body?.string()
                    val json = JSONObject(body)
                    val articles = json.getJSONArray("articles")

                    val newsList = mutableListOf<NewsItem>()
                    for (i in 0 until articles.length()) {
                        val article = articles.getJSONObject(i)
                        val title = article.getString("title")
                        val date = article.getString("publishedAt").substring(0, 10)
                        val summary = article.optString("description", "No summary available")
                        val url = article.getString("url")
                        newsList.add(NewsItem(title, date, summary, url))
                    }

                    runOnUiThread {
                        newsRecyclerView.adapter = NewsAdapter(newsList)
                        progressBar.visibility = View.GONE
                    }
                }
            }
        })
    }

    data class NewsItem(
        val title: String,
        val date: String,
        val summary: String,
        val url: String
    )

    inner class NewsAdapter(private val newsItems: List<NewsItem>) :
        RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

        inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title: TextView = itemView.findViewById(R.id.newsTitle)
            val date: TextView = itemView.findViewById(R.id.newsDate)
            val summary: TextView = itemView.findViewById(R.id.newsSummary)
            val readMoreButton: Button = itemView.findViewById(R.id.readMoreButton)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news, parent, false)
            return NewsViewHolder(view)
        }

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            val item = newsItems[position]
            holder.title.text = item.title
            holder.date.text = item.date
            holder.summary.text = item.summary

            holder.readMoreButton.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
                    holder.itemView.context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Couldn't open the news article",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        override fun getItemCount(): Int = newsItems.size
    }
}
