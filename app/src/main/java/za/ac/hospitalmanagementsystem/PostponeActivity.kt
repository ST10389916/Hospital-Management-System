package za.ac.hospitalmanagementsystem

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class PostponeActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var tvAppointmentNumber: TextView
    private lateinit var etDate: EditText
    private lateinit var autoCompleteAvailability: AutoCompleteTextView
    private lateinit var btnSubmit: Button
    private lateinit var timeframeInputLayout: TextInputLayout
    private lateinit var dateInputLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postpone)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize views
        tvAppointmentNumber = findViewById(R.id.appointmentNumber)
        etDate = findViewById(R.id.editTextDate)
        autoCompleteAvailability = findViewById(R.id.spinnerAvailability)
        btnSubmit = findViewById(R.id.buttonSubmit)
        timeframeInputLayout = findViewById(R.id.timeframeInputLayout)
        dateInputLayout = findViewById(R.id.dateInputLayout)

        // Setup dropdown
        setupTimeframeDropdown()

        val appointmentNo = intent.getStringExtra("appointmentNo") ?: ""
        tvAppointmentNumber.text = "Appointment #$appointmentNo"

        // Load current appointment details
        loadAppointmentDetails(appointmentNo)

        // Setup date picker
        setupDatePicker()

        btnSubmit.setOnClickListener {
            updateAppointment(appointmentNo)
        }
    }

    private fun setupTimeframeDropdown() {
        val timeframes = resources.getStringArray(R.array.timeframe)
        val adapter = ArrayAdapter(
            this,
            R.layout.dropdown_item,
            timeframes
        )
        autoCompleteAvailability.setAdapter(adapter)

        autoCompleteAvailability.setOnClickListener {
            autoCompleteAvailability.showDropDown()
        }

        autoCompleteAvailability.threshold = 1
    }

    private fun setupDatePicker() {
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateCalendar(myCalendar)
        }

        etDate.setOnClickListener {
            val dialog = DatePickerDialog(
                this,
                datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.datePicker.minDate = System.currentTimeMillis() - 1000
            dialog.show()
        }
    }

    private fun loadAppointmentDetails(appointmentNo: String) {
        database = FirebaseDatabase.getInstance().getReference("Appointment").child(appointmentNo)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.child("date").getValue(String::class.java)?.let {
                        etDate.setText(it)
                    }

                    val currentAvailability = snapshot.child("availability").getValue(String::class.java)
                    if (currentAvailability != null) {
                        autoCompleteAvailability.setText(currentAvailability, false)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@PostponeActivity,
                    "Failed to load appointment details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateCalendar(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etDate.setText(dateFormat.format(calendar.time))
        dateInputLayout.error = null
    }

    private fun updateAppointment(appointmentNo: String) {
        val date = etDate.text.toString()
        val availability = autoCompleteAvailability.text.toString()

        if (date.isEmpty()) {
            dateInputLayout.error = "Please select a date"
            return
        } else {
            dateInputLayout.error = null
        }

        if (availability.isEmpty()) {
            timeframeInputLayout.error = "Please select a timeframe"
            return
        } else {
            timeframeInputLayout.error = null
        }

        val updates = mapOf(
            "date" to date,
            "availability" to availability
        )

        database.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this,
                    "Appointment postponed successfully",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Failed to postpone appointment",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}