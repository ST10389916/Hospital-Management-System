package za.ac.hospitalmanagementsystem

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class DoctorAppointmentsActivity : DoctorBaseActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var noAppointmentsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_appointments)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views with correct IDs
        recyclerView = findViewById(R.id.recyclerViewAppointments)
        noAppointmentsText = findViewById(R.id.textViewNoAppointments)

        recyclerView.layoutManager = LinearLayoutManager(this)

        getAppointments()
        setupBottomNavigation(R.id.nav_appointments)
    }

    private fun getAppointments() {
        database = FirebaseDatabase.getInstance().getReference("Appointment")
        database.orderByChild("doctor").equalTo(username)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appointments = mutableListOf<AppointmentDTO>()  // Changed here

                    for (appointmentSnapshot in snapshot.children) {
                        val appointment = appointmentSnapshot.getValue(AppointmentDTO::class.java)  // Changed here
                        appointment?.appointmentNo = appointmentSnapshot.key ?: ""
                        if (appointment != null) {
                            appointments.add(appointment)
                        }
                    }

                    if (appointments.isEmpty()) {
                        noAppointmentsText.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        noAppointmentsText.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        recyclerView.adapter = AppointmentAdapter(appointments) { appointmentNo ->
                            goToPostpone(appointmentNo)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DoctorAppointmentsActivity,
                        "Failed to load appointments", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

