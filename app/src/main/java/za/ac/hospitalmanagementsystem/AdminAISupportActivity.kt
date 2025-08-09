package za.ac.hospitalmanagementsystem.admin

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import za.ac.hospitalmanagementsystem.R

class AdminAISupportActivity : AdminBaseActivity() {

    private lateinit var questionInput: TextInputEditText
    private lateinit var askButton: Button
    private lateinit var responseText: TextView
    private lateinit var progressBar: ProgressBar

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_ai_support)

        questionInput = findViewById(R.id.questionInput)
        askButton = findViewById(R.id.askButton)
        responseText = findViewById(R.id.responseText)
        progressBar = findViewById(R.id.progressBar)

        setupBottomNavigation(R.id.nav_ai_support)

        askButton.setOnClickListener {
            val question = questionInput.text.toString().trim()
            if (question.isNotEmpty()) {
                askButton.isEnabled = false        // Disable button here
                askOpenAI(question)
            } else {
                questionInput.error = "Please enter a question"
            }
        }

        questionInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val question = questionInput.text.toString().trim()
                if (question.isNotEmpty()) {
                    askButton.isEnabled = false      // Disable button here
                    askOpenAI(question)
                } else {
                    questionInput.error = "Please enter a question"
                }
                true
            } else false
        }
    }

    private fun askOpenAI(question: String, retryCount: Int = 0) {
        if (retryCount == 0) {
            runOnUiThread {
                responseText.text = "Please wait..."
                progressBar.visibility = View.VISIBLE
                questionInput.setText("")
            }
        }

        if (retryCount > 5) {
            runOnUiThread {
                progressBar.visibility = View.GONE
                askButton.isEnabled = true        // Enable button here on too many retries
                showErrorDialog("Too many retries due to rate limiting. Please try again later.")
            }
            return
        }

        val apiKey = "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" // Replace with your actual OpenAI API key
        val url = "https://api.openai.com/v1/chat/completions"

        val jsonBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("max_tokens", 500)
            put("temperature", 0)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", question)
                })
            })
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    askButton.isEnabled = true    // Enable button on failure
                    showErrorDialog("Network error: ${e.localizedMessage}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.d("OPENAI_RESPONSE", body ?: "Empty body")

                if (response.code == 429) {
                    // Rate limited - retry with exponential backoff
                    val delayMillis = (1000L * Math.pow(2.0, retryCount.toDouble())).toLong()
                    Log.d("OPENAI_RETRY", "Rate limited. Retrying in $delayMillis ms...")
                    Thread.sleep(delayMillis)
                    askOpenAI(question, retryCount + 1)
                    return
                }

                if (!response.isSuccessful || body == null) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        askButton.isEnabled = true    // Enable button on API error
                        showErrorDialog("API error: ${response.code} - ${response.message}")
                    }
                    return
                }

                try {
                    val jsonObject = JSONObject(body)
                    val choices = jsonObject.getJSONArray("choices")
                    val message = choices.getJSONObject(0).getJSONObject("message")
                    val textResult = message.getString("content")

                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        askButton.isEnabled = true    // Enable button on success
                        responseText.text = textResult.trim()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        askButton.isEnabled = true    // Enable button on parse error
                        showErrorDialog("Parsing error: ${e.localizedMessage}")
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
