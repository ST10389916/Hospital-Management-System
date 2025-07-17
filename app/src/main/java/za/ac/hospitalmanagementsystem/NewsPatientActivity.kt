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
        // Simulate API delay
        newsRecyclerView.postDelayed({
            val newsItems = listOf(
                NewsItem(
                    "New Breakthrough in Cancer Treatment",
                    "May 15, 2023",
                    "Researchers have discovered a new approach that significantly improves outcomes...",
                    "https://example.com/cancer-breakthrough"
                ),
                NewsItem(
                    "Hospital Wins Patient Care Award",
                    "April 28, 2023",
                    "Our hospital has been recognized for excellence in patient satisfaction...",
                    "https://example.com/hospital-award"
                )
            )

            newsRecyclerView.adapter = NewsAdapter(newsItems)
            progressBar.visibility = View.GONE
        }, 1500)
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