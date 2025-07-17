package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity() {

    // Common initialization for all activities
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Common setup can be added here if needed
    }

    // Sets up bottom navigation for all activities
    protected fun setupBottomNavigation(selectedTabId: Int) {
        // Navigation items mapping
        val navItems = listOf(
            R.id.nav_news to NewsPatientActivity::class.java,
            R.id.nav_appointment to PatientActivity::class.java,
            R.id.nav_profile to ProfileActivity::class.java,
            R.id.nav_logout to LoginActivity::class.java
        )

        navItems.forEach { (id, activityClass) ->
            findViewById<View>(id)?.setOnClickListener {
                handleNavigationClick(id, activityClass)
            }
        }

        highlightTab(selectedTabId)
    }

    // Handles navigation click events
    private fun handleNavigationClick(tabId: Int, targetActivity: Class<*>) {
        when {
            // Logout special case
            tabId == R.id.nav_logout -> {
                startActivity(Intent(this, targetActivity))
                finish()
            }
            // Prevent reloading current activity
            this::class.java == targetActivity -> return
            // Normal navigation
            else -> {
                startActivity(Intent(this, targetActivity).apply {
                    putExtras(intent?.extras ?: Bundle())
                })
            }
        }
    }

    // Highlights the currently selected tab
    protected fun highlightTab(tabId: Int) {
        val activeColor = ContextCompat.getColor(this, R.color.primary_color)
        val inactiveColor = ContextCompat.getColor(this, android.R.color.darker_gray)

        // Reset all tabs first
        listOf(R.id.nav_news, R.id.nav_appointment, R.id.nav_profile, R.id.nav_logout).forEach { id ->
            val tabView = findViewById<View>(id) ?: return@forEach

            // Get references to icon and text views
            val (iconView, textView) = getTabViews(id) ?: return@forEach

            // Set inactive state
            iconView.setColorFilter(inactiveColor)
            textView.setTextColor(inactiveColor)
        }

        // Highlight selected tab
        getTabViews(tabId)?.let { (iconView, textView) ->
            iconView.setColorFilter(activeColor)
            textView.setTextColor(activeColor)
        }
    }

    // Helper function to get tab views
    private fun getTabViews(tabId: Int): Pair<ImageView, TextView>? {
        return when (tabId) {
            R.id.nav_news -> Pair(
                findViewById(R.id.nav_news_icon),
                findViewById(R.id.nav_news_text)
            )
            R.id.nav_appointment -> Pair(
                findViewById(R.id.nav_appointment_icon),
                findViewById(R.id.nav_appointment_text)
            )
            R.id.nav_profile -> Pair(
                findViewById(R.id.nav_profile_icon),
                findViewById(R.id.nav_profile_text)
            )
            R.id.nav_logout -> Pair(
                findViewById(R.id.nav_logout_icon),
                findViewById(R.id.nav_logout_text)
            )
            else -> null
        }
    }

    // Common method for activities to refresh tab state
    override fun onResume() {
        super.onResume()
        // Activities should override this and call highlightTab()
        // with their specific tab ID
    }
}