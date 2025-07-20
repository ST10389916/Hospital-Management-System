package za.ac.hospitalmanagementsystem.admin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import za.ac.hospitalmanagementsystem.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AdminAISupportActivity : AdminBaseActivity() {

    private lateinit var questionInput: EditText
    private lateinit var askButton: Button
    private lateinit var responseText: TextView
    private lateinit var progressBar: ProgressBar
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_ai_support)

        // Initialize views
        questionInput = findViewById(R.id.questionInput)
        askButton = findViewById(R.id.askButton)
        responseText = findViewById(R.id.responseText)
        progressBar = findViewById(R.id.progressBar)

        // Setup bottom navigation
        setupBottomNavigation(R.id.nav_ai_support)

        // Set click listener for ask button
        askButton.setOnClickListener {
            val question = questionInput.text.toString().trim()
            if (question.isNotEmpty()) {
                sendQuestionToAI(question)
            } else {
                questionInput.error = "Please enter a question"
            }
        }
    }

    private fun sendQuestionToAI(question: String) {
        progressBar.visibility = View.VISIBLE
        responseText.text = ""

        // In a real app, you would replace this with your actual API key and proper endpoint
        val apiKey = "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"
        val url = "https://api.openai.com/v1/chat/completions"

        val json = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [{"role": "user", "content": "$question"}],
                "temperature": 0.7
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    showErrorDialog("Network error: ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            showErrorDialog("API error: ${response.code}")
                        }
                        return
                    }

                    val responseBody = response.body?.string()
                    val jsonResponse = responseBody?.let { JSONObject(it) }
                    val aiResponse = jsonResponse
                        ?.getJSONArray("choices")
                        ?.getJSONObject(0)
                        ?.getJSONObject("message")
                        ?.getString("content")

                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        if (aiResponse != null) {
                            responseText.text = aiResponse
                        } else {
                            showErrorDialog("Failed to parse AI response")
                        }
                    }
                }
            }
        })
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}