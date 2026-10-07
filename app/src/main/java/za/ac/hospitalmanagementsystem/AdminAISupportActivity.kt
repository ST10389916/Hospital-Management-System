package za.ac.hospitalmanagementsystem.admin

import android.os.Bundle
import android.os.Handler
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
                askAzureOpenAI(question)
            } else {
                questionInput.error = "Please enter a question"
            }
        }

        questionInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val question = questionInput.text.toString().trim()
                if (question.isNotEmpty()) {
                    askButton.isEnabled = false      // Disable button here
                    askAzureOpenAI(question)
                } else {
                    questionInput.error = "Please enter a question"
                }
                true
            } else false
        }
    }

    private fun askAzureOpenAI(question: String, retryCount: Int = 0) {
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
                askButton.isEnabled = true
                showErrorDialog("Too many retries due to rate limiting. Please try again later.")
            }
            return
        }
        val apiKey = "6iry3Aofh6tipi3tDlbIqVr0JPuWIguZZMK6cxlsQkbLOfQhdR6rJQQJ99BIACHYHv6XJ3w3AAAAACOG5ygd"
        val deploymentId = "gpt-35-turbo"  // Same as in your C# code
        //val endpoint = "https://justi-mfyfvof6-eastus2.cognitiveservices.azure.com/"
        val apiVersion = "2023-05-15" // Or latest supported
        val endpoint ="https://justi-mfyfvof6-eastus2.cognitiveservices.azure.com/openai/deployments/$deploymentId/chat/completions?api-version=$apiVersion"

        val jsonBody = JSONObject().apply {
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are an AI assistant that helps people find information.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", question)
                })
            })
            put("temperature", 0.7)
            put("max_tokens", 800)
            put("top_p", 0.95)
            put("frequency_penalty", 0)
            put("presence_penalty", 0)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(endpoint)
            .addHeader("Content-Type", "application/json")
            .addHeader("api-key", apiKey)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    askButton.isEnabled = true
                    showErrorDialog("Network error: ${e.localizedMessage}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.d("AZURE_OPENAI_RESPONSE", body ?: "Empty body")

                if (response.code == 429) {
                    // Retry using exponential backoff
                    val delayMillis = (1000L * Math.pow(2.0, retryCount.toDouble())).toLong()
                    Handler(mainLooper).postDelayed({
                        askAzureOpenAI(question, retryCount + 1)
                    }, delayMillis)
                    return
                }

                if (!response.isSuccessful || body == null) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        askButton.isEnabled = true
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
                        askButton.isEnabled = true
                        responseText.text = textResult.trim()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        progressBar.visibility = View.GONE
                        askButton.isEnabled = true
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
