package za.ac.hospitalmanagementsystem

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class SetAppointmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_appointment)

        // Set up toolbar with back button
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = " "
//            setDisplayHomeAsUpEnabled(true)
//            setDisplayShowHomeEnabled(true)
        }

        // Get user data from intent
        val username = intent.getStringExtra("userName").toString()
        val name = intent.getStringExtra("name")
        val surname = intent.getStringExtra("surname")
        val number = intent.getStringExtra("number")

        // Initialize views
        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)
        val spinnerAvailability = findViewById<Spinner>(R.id.spinnerAvailability)

        // Setup spinner with time frames
        val timeFrames = resources.getStringArray(R.array.timeframe)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeFrames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAvailability.adapter = adapter

        // Date picker setup
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateCalendar(myCalendar)
        }

        editTextDate.setOnClickListener {
            DatePickerDialog(this, datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        buttonSubmit.setOnClickListener {
            if (validateInputs()) {
                val randomValues = Random.nextInt(1000)
                submitAppointment(randomValues, username, name, surname, number)
            }
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun validateInputs(): Boolean {
        val disease = findViewById<EditText>(R.id.editTextDisease).text.toString()
        val date = findViewById<EditText>(R.id.editTextDate).text.toString()

        if (disease.isEmpty()) {
            findViewById<EditText>(R.id.editTextDisease).error = "Please enter appointment description"
            return false
        }

        if (date.isEmpty()) {
            findViewById<EditText>(R.id.editTextDate).error = "Please select a date"
            return false
        }

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun updateCalendar(calendar: Calendar) {
        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        editTextDate.setText(dateFormat.format(calendar.time))
        editTextDate.error = null
    }

    private fun submitAppointment(
        appointmentNumber: Int,
        username: String,
        name: String?,
        surname: String?,
        number: String?
    ) {
        val disease = findViewById<EditText>(R.id.editTextDisease).text.toString()
        val availability = findViewById<Spinner>(R.id.spinnerAvailability).selectedItem.toString()
        val date = findViewById<EditText>(R.id.editTextDate).text.toString()

        Firebase.database.reference
            .child("Appointment")
            .child(appointmentNumber.toString())
            .setValue(Appointment(username, "null", disease, availability, date))

        val intent = Intent(this, PatientActivity::class.java).apply {
            putExtra("userName", username)
            putExtra("name", name)
            putExtra("surname", surname)
            putExtra("number", number)
        }
        startActivity(intent)
        finish()
    }
}