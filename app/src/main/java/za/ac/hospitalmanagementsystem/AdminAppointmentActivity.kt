package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import za.ac.hospitalmanagementsystem.admin.AdminBaseActivity

class AdminAppointmentActivity : AdminBaseActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var appointmentsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_appointment)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        appointmentsContainer = findViewById(R.id.appointmentsContainer)
        loadAppointments()
    }

    private fun loadAppointments() {
        database = FirebaseDatabase.getInstance().getReference("Appointment")
        database.get().addOnSuccessListener { snapshot ->
            appointmentsContainer.removeAllViews()

            if (!snapshot.exists()) {
                val noAppointmentsText = TextView(this).apply {
                    text = "No appointments found"
                    setTextAppearance(android.R.style.TextAppearance_Medium)
                    gravity = Gravity.CENTER
                    setPadding(0, 32.dpToPx(), 0, 0)
                }
                appointmentsContainer.addView(noAppointmentsText)
                return@addOnSuccessListener
            }

            for (appointmentSnapshot in snapshot.children) {
                val appointmentCard = layoutInflater.inflate(
                    R.layout.appointment_card,
                    appointmentsContainer,
                    false
                ) as MaterialCardView

                val appointmentNo = appointmentSnapshot.key.toString()
                val patient = appointmentSnapshot.child("patient").value.toString()
                var doctor = appointmentSnapshot.child("doctor").value.toString()
                val date = appointmentSnapshot.child("date").value.toString()
                val disease = appointmentSnapshot.child("disease").value.toString()
                val availability = appointmentSnapshot.child("availability").value.toString()

                if (doctor == "null") doctor = "Not yet assigned"

                appointmentCard.findViewById<TextView>(R.id.textAppointmentNo).text =
                    "Appointment #$appointmentNo"
                appointmentCard.findViewById<TextView>(R.id.textPatientName).text =
                    "Patient: $patient"
                appointmentCard.findViewById<TextView>(R.id.textDoctorName).text =
                    "Doctor: $doctor"
                appointmentCard.findViewById<TextView>(R.id.textDate).text =
                    "Date: $date"
                appointmentCard.findViewById<TextView>(R.id.textDisease).text =
                    "Disease: $disease"
                appointmentCard.findViewById<TextView>(R.id.textAvailability).text =
                    "Availability: $availability"

                appointmentCard.findViewById<MaterialButton>(R.id.buttonEdit).setOnClickListener {
                    val name = intent.getStringExtra("name").toString()
                    val surname = intent.getStringExtra("surname").toString()
                    val number = intent.getStringExtra("number").toString()
                    val username = intent.getStringExtra("userName").toString()

                    val intent = Intent(this, EditAppointmentActivity::class.java)
                    intent.putExtra("appointmentNo", appointmentNo)
                    intent.putExtra("name", name)
                    intent.putExtra("surname", surname)
                    intent.putExtra("number", number)
                    intent.putExtra("userName", username)
                    intent.putExtra("patient", patient)
                    intent.putExtra("doctor", doctor)
                    intent.putExtra("date", date)
                    intent.putExtra("disease", disease)
                    intent.putExtra("availability", availability)
                    startActivity(intent)
                }

                appointmentCard.findViewById<MaterialButton>(R.id.buttonDelete).setOnClickListener {
                    deleteAppointment(appointmentNo, appointmentCard)
                }

                appointmentsContainer.addView(appointmentCard)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load appointments", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteAppointment(appointmentNo: String, cardView: View) {
        database.child(appointmentNo).removeValue().addOnSuccessListener {
            appointmentsContainer.removeView(cardView)
            Toast.makeText(this, "Appointment deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
