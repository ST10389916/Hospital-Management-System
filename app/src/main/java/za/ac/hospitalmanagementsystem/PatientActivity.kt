package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class PatientActivity : BaseActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var currentUsername: String
    private lateinit var appointmentsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

        // Initialize time displays
        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

        findViewById<TextView>(R.id.textViewTime).text = currentDate
        findViewById<TextView>(R.id.textViewLoginTime).text = "Time logged in: $currentTime"

        // Initialize toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Get user data from intent
        val extras = intent?.extras ?: run {
            Toast.makeText(this, "User data missing!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        currentUsername = extras.getString("userName", "")
        val fullName = "${extras.getString("name", "")} ${extras.getString("surname", "")}"
        val phoneNumber = extras.getString("number", "")

        // Set user information
        findViewById<TextView>(R.id.textViewName).text = fullName
        findViewById<TextView>(R.id.textViewUsername).text = currentUsername
        findViewById<TextView>(R.id.textViewUserNumber).text = phoneNumber

        // Initialize appointments container
        appointmentsContainer = findViewById(R.id.appointmentsContainer)

        // Load appointments
        loadAppointments()

        // Set click listeners
        findViewById<Button>(R.id.buttonSetAppointment).setOnClickListener {
            navigateToSetAppointment()
        }

        highlightTab(R.id.nav_appointment)
        setupBottomNavigation(R.id.nav_appointment)
    }

    private fun loadAppointments() {
        database = FirebaseDatabase.getInstance().getReference("Appointment")
        database.get().addOnSuccessListener { snapshot ->
            appointmentsContainer.removeAllViews() // Clear existing views

            if (!snapshot.exists()) {
                val noAppointmentsText = TextView(this).apply {
                    text = "No appointments found"
                    setTextColor(resources.getColor(android.R.color.darker_gray))
                    textSize = 16f
                    setPadding(0, 16, 0, 16)
                }
                appointmentsContainer.addView(noAppointmentsText)
                return@addOnSuccessListener
            }

            for (appointment in snapshot.children) {
                val availability = appointment.child("availability").value.toString()
                val date = appointment.child("date").value.toString()
                val disease = appointment.child("disease").value.toString()
                var doctor = appointment.child("doctor").value.toString()
                if (doctor == "null") {
                    doctor = "Not yet assigned"
                }
                val patient = appointment.child("patient").value.toString()
                val id = appointment.key

                // Only show appointments for current user
                if (patient == currentUsername) {
                    val appointmentCard = createAppointmentCard(
                        date = date,
                        doctor = doctor,
                        disease = disease,
                        status = availability
                    )
                    appointmentsContainer.addView(appointmentCard)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load appointments", Toast.LENGTH_LONG).show()
        }
    }

    private fun createAppointmentCard(date: String, doctor: String, disease: String, status: String): CardView {
        return CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            radius = 12f
            elevation = 4f
            setContentPadding(16, 16, 16, 16)

            val cardContent = LinearLayout(this@PatientActivity).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Add fields to the card
            arrayOf(
                "Date: $date",
                "Doctor: $doctor",
                "Condition: $disease",
                "Status: $status"
            ).forEach { text ->
                val textView = TextView(this@PatientActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 4, 0, 4)
                    }
                    setTextColor(resources.getColor(android.R.color.black))
                    textSize = 16f
                    this.text = text
                }
                cardContent.addView(textView)
            }

            addView(cardContent)
        }
    }

    private fun setupBottomNavigation() {
        val navItems = listOf(
            R.id.nav_news to NewsPatientActivity::class.java,
            R.id.nav_appointment to AppointmentActivity::class.java,
            R.id.nav_profile to ProfileActivity::class.java,
            R.id.nav_logout to LoginActivity::class.java
        )

        navItems.forEach { (id, activityClass) ->
            findViewById<View>(id)?.setOnClickListener {
                if (id == R.id.nav_logout) {
                    startActivity(Intent(this, activityClass))
                    finish()
                } else if (id != R.id.nav_appointment) {
                    highlightTab(id)
                    startActivity(Intent(this, activityClass).apply {
                        putExtras(intent?.extras ?: Bundle())
                    })
                }
            }
        }
    }

    private fun navigateToSetAppointment() {
        startActivity(Intent(this, SetAppointmentActivity::class.java).apply {
            putExtras(intent?.extras ?: Bundle())
        })
    }

    override fun onResume() {
        super.onResume()
        highlightTab(R.id.nav_appointment)
        loadAppointments() // Refresh appointments when returning to activity
    }
}