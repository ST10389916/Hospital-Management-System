package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class DoctorBaseActivity : AppCompatActivity() {
    protected lateinit var username: String
    protected lateinit var name: String
    protected lateinit var surname: String
    protected lateinit var number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize user data from intent
        username = intent.getStringExtra("username").orEmpty()
        name = intent.getStringExtra("name").orEmpty()
        surname = intent.getStringExtra("surname").orEmpty()
        number = intent.getStringExtra("number").orEmpty()
    }

    protected fun setupBottomNavigation(selectedTabId: Int) {
        val navItems = listOf(
            R.id.nav_home to DoctorActivity::class.java,
            R.id.nav_appointments to DoctorAppointmentsActivity::class.java,
            R.id.nav_records to PatientRecordActivity::class.java,
            R.id.nav_profile to DoctorProfileActivity::class.java,
            R.id.nav_logout to LoginActivity::class.java
        )

        navItems.forEach { (id, activityClass) ->
            findViewById<View>(id)?.setOnClickListener {
                handleNavigationClick(id, activityClass)
            }
        }

        highlightTab(selectedTabId)
    }

    private fun handleNavigationClick(tabId: Int, targetActivity: Class<*>) {
        when {
            tabId == R.id.nav_logout -> {
                startActivity(Intent(this, targetActivity))
                finishAffinity()
            }
            this::class.java == targetActivity -> return
            targetActivity == DoctorAppointmentsActivity::class.java -> {
                // Special handling for Appointments to maintain proper back stack
                startActivity(Intent(this, targetActivity).apply {
                    putExtras(intent?.extras ?: Bundle())
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }
            else -> {
                startActivity(Intent(this, targetActivity).apply {
                    putExtras(intent?.extras ?: Bundle())
                    flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                })
            }
        }
        overridePendingTransition(0, 0)
    }

    protected fun highlightTab(tabId: Int) {
        val activeColor = ContextCompat.getColor(this, R.color.primary_color)
        val inactiveColor = ContextCompat.getColor(this, android.R.color.darker_gray)

        val tabs = listOf(
            Triple(R.id.nav_home, R.id.nav_home_icon, R.id.nav_home_text),
            Triple(R.id.nav_appointments, R.id.nav_appointments_icon, R.id.nav_appointments_text),
            Triple(R.id.nav_records, R.id.nav_records_icon, R.id.nav_records_text),
            Triple(R.id.nav_profile, R.id.nav_profile_icon, R.id.nav_profile_text),
            Triple(R.id.nav_logout, R.id.nav_logout_icon, R.id.nav_logout_text)
        )

        tabs.forEach { (navId, iconId, textId) ->
            val color = if (navId == tabId) activeColor else inactiveColor
            findViewById<ImageView>(iconId)?.setColorFilter(color)
            findViewById<TextView>(textId)?.setTextColor(color)
        }
    }

    protected fun goToAppointment() {
        startActivity(Intent(this, DoctorAppointmentsActivity::class.java).apply {
            putExtra("username", username)
            putExtra("name", name)
            putExtra("surname", surname)
            putExtra("number", number)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        })
    }

    protected fun goToPatientRecord() {
        startActivity(Intent(this, PatientRecordActivity::class.java).apply {
            putExtra("username", username)
            putExtra("name", name)
            putExtra("surname", surname)
            putExtra("number", number)
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        })
    }

    protected fun goToView() {
        startActivity(Intent(this, RecordActivity::class.java).apply {
            putExtra("username", username)
            putExtra("name", name)
            putExtra("surname", surname)
            putExtra("number", number)
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        })
    }

    protected fun goToPostpone(appointmentNo: String) {
        startActivity(Intent(this, PostponeActivity::class.java).apply {
            putExtra("appointmentNo", appointmentNo)
            putExtra("username", username)
            putExtra("name", name)
            putExtra("surname", surname)
            putExtra("number", number)
        })
    }

    protected fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}