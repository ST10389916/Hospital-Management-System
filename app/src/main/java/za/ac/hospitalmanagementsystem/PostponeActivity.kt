package za.ac.hospitalmanagementsystem

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class PostponeActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var tvAppointmentNumber: TextView
    private lateinit var etDate: EditText
    private lateinit var spinnerAvailability: Spinner
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postpone)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Postpone Appointment"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views
        tvAppointmentNumber = findViewById(R.id.appointmentNumber)
        etDate = findViewById(R.id.editTextDate)
        spinnerAvailability = findViewById(R.id.spinnerAvailability)
        btnSubmit = findViewById(R.id.buttonSubmit)

        val appointmentNo = intent.getStringExtra("appointmentNo") ?: ""
        tvAppointmentNumber.text = "Appointment #$appointmentNo"

        // Load current appointment details
        loadAppointmentDetails(appointmentNo)

        // Date picker setup
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateCalendar(myCalendar)
        }

        etDate.setOnClickListener {
            DatePickerDialog(this, datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSubmit.setOnClickListener {
            updateAppointment(appointmentNo)
        }
    }

    private fun loadAppointmentDetails(appointmentNo: String) {
        database = FirebaseDatabase.getInstance().getReference("Appointment").child(appointmentNo)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Populate fields with current values
                    snapshot.child("date").getValue(String::class.java)?.let {
                        etDate.setText(it)
                    }

                    // Set spinner to current availability
                    val currentAvailability = snapshot.child("availability").getValue(String::class.java)
                    if (currentAvailability != null) {
                        val adapter = spinnerAvailability.adapter as ArrayAdapter<String>
                        val position = adapter.getPosition(currentAvailability)
                        if (position >= 0) {
                            spinnerAvailability.setSelection(position)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PostponeActivity,
                    "Failed to load appointment details", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateCalendar(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etDate.setText(dateFormat.format(calendar.time))
    }

    private fun updateAppointment(appointmentNo: String) {
        val date = etDate.text.toString()
        val availability = spinnerAvailability.selectedItem.toString()

        if (date.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mapOf(
            "date" to date,
            "availability" to availability
        )

        database.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Appointment postponed successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to postpone appointment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}