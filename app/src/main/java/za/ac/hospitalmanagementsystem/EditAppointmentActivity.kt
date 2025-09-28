package za.ac.hospitalmanagementsystem

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private var doc: String? = null // Selected doctor ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appointment)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Update Appointment"

        // Intent extras
        val appointmentNo = intent.getStringExtra("appointmentNo").orEmpty()
        val name = intent.getStringExtra("name").orEmpty()
        val surname = intent.getStringExtra("surname").orEmpty()
        val number = intent.getStringExtra("number").orEmpty()
        val username = intent.getStringExtra("userName").orEmpty()

        val patient = intent.getStringExtra("patient").orEmpty()
        val disease = intent.getStringExtra("disease").orEmpty()
        val doctor = intent.getStringExtra("doctor").orEmpty()
        val availability = intent.getStringExtra("availability").orEmpty()
        val dateValue = intent.getStringExtra("date").orEmpty()

        // Set static fields immediately
        findViewById<TextView>(R.id.appointmentNumber).text = appointmentNo
        findViewById<EditText>(R.id.editTextPatientName).setText(patient)
        findViewById<EditText>(R.id.editTextDisease).setText(disease)
        findViewById<EditText>(R.id.editTextDate).setText(dateValue)

        // Setup DatePicker
        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateMyCalendar(myCalendar)
        }
        findViewById<EditText>(R.id.editTextDate).setOnClickListener {
            DatePickerDialog(
                this, datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Load dropdowns and set initial selections
        loadDoctorsAndAvailability(doctor, availability)

        // Update button listener
        findViewById<Button>(R.id.buttonSubmit).setOnClickListener {
            updateAppointment(appointmentNo, name, surname, number, username)
        }
    }

    private fun loadDoctorsAndAvailability(selectedDoctor: String, selectedAvailability: String) {
        val doctorDropdown = findViewById<AutoCompleteTextView>(R.id.auto_complete_text)
        val availabilityDropdown = findViewById<AutoCompleteTextView>(R.id.spinnerAvailability)

        // Availability adapter from local string array
        val availabilityArray = resources.getStringArray(R.array.timeframe)
        val availabilityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, availabilityArray)
        availabilityDropdown.setAdapter(availabilityAdapter)
        availabilityDropdown.setText(selectedAvailability, false)

        // Show availability dropdown on click
        availabilityDropdown.setOnClickListener {
            availabilityDropdown.showDropDown()
        }

        // Load doctors from Firebase
        val doctorRef = FirebaseDatabase.getInstance().getReference("Doctor")
        val doctorNames = mutableListOf<String>()
        val doctorMap = mutableMapOf<String, String>() // Map fullName -> doctorId

        doctorRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorNames.clear()
                doctorMap.clear()

                for (docSnap in snapshot.children) {
                    val name = docSnap.child("name").getValue(String::class.java) ?: ""
                    val surname = docSnap.child("surname").getValue(String::class.java) ?: ""
                    val fullName = "$name $surname".trim()
                    val id = docSnap.key ?: ""

                    if (fullName.isNotEmpty()) {
                        doctorNames.add(fullName)
                        doctorMap[fullName] = id
                    }
                }

                val doctorAdapter = ArrayAdapter(this@EditAppointmentActivity, android.R.layout.simple_dropdown_item_1line, doctorNames)
                doctorDropdown.setAdapter(doctorAdapter)

                // Set initial doctor text AFTER adapter set
                if (selectedDoctor.isNotEmpty()) {
                    doctorDropdown.setText(selectedDoctor, false)
                    doc = doctorMap[selectedDoctor]
                }

                // Show doctor dropdown on click
                doctorDropdown.setOnClickListener {
                    doctorDropdown.showDropDown()
                }

                doctorDropdown.setOnItemClickListener { parent, _, position, _ ->
                    val selectedName = parent.getItemAtPosition(position) as String
                    doc = doctorMap[selectedName] ?: ""
                    Toast.makeText(this@EditAppointmentActivity, "Selected Doctor: $selectedName", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditAppointmentActivity, "Failed to load doctors: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateMyCalendar(calendar: Calendar) {
        val date = findViewById<EditText>(R.id.editTextDate)
        val dFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        date.setText(dFormat.format(calendar.time))
    }

    private fun updateAppointment(appointmentNo: String, name: String, surname: String, number: String, username: String) {
        val availability = findViewById<AutoCompleteTextView>(R.id.spinnerAvailability).text.toString()
        val date = findViewById<EditText>(R.id.editTextDate).text.toString()
        val doctorId = doc ?: ""

        database = FirebaseDatabase.getInstance().getReference("Appointment")
        val updateData = mapOf(
            "availability" to availability,
            "date" to date,
            "doctor" to doctorId
        )

        database.child(appointmentNo).updateChildren(updateData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Appointment Updated", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, AdminAppointmentActivity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("surname", surname)
                intent.putExtra("number", number)
                intent.putExtra("userName", username)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to update appointment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
