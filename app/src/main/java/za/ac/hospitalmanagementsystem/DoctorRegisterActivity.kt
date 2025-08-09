package za.ac.hospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DoctorRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_register)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Doctor Registration"

        // Find dropdown views
        val genderDropdown = findViewById<AutoCompleteTextView>(R.id.spinnerGender)
        val availabilityDropdown = findViewById<AutoCompleteTextView>(R.id.spinnerAvailability)
        val departmentDropdown = findViewById<AutoCompleteTextView>(R.id.spinnerDepartment)
        val specializationDropdown = findViewById<AutoCompleteTextView>(R.id.spinnerSpecialization)

        // Sample data lists for dropdowns
        val genders = listOf("Male", "Female", "Other")
        val availabilities = listOf("Full Time", "Part Time", "On Call")
        val departments = listOf("General Medicine", "Pediatrics", "Surgery", "Gynecology")
        val specializations = listOf("Cardiology", "Neurology", "Orthopedics", "Dermatology")

        // Create adapters
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, genders)
        val availabilityAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, availabilities)
        val departmentAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, departments)
        val specializationAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, specializations)

        // Assign adapters
        genderDropdown.setAdapter(genderAdapter)
        availabilityDropdown.setAdapter(availabilityAdapter)
        departmentDropdown.setAdapter(departmentAdapter)
        specializationDropdown.setAdapter(specializationAdapter)

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            val username: String = findViewById<TextInputEditText>(R.id.textInputEditTextEmail).text.toString()
            if (username.isNotEmpty()) {
                registerDoctor(username)
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun registerDoctor(username: String) {
        val password: String = findViewById<TextInputEditText>(R.id.textInputEditTextPassword).text.toString()
        val availability: String = findViewById<AutoCompleteTextView>(R.id.spinnerAvailability).text.toString()
        val department: String = findViewById<AutoCompleteTextView>(R.id.spinnerDepartment).text.toString()
        val specialization: String = findViewById<AutoCompleteTextView>(R.id.spinnerSpecialization).text.toString()
        val address: String = findViewById<TextInputEditText>(R.id.textInputEditTextAddress).text.toString()
        val gender: String = findViewById<AutoCompleteTextView>(R.id.spinnerGender).text.toString()
        val age: String = findViewById<TextInputEditText>(R.id.textInputEditTextAge).text.toString()
        val phone: String = findViewById<TextInputEditText>(R.id.textInputEditTextPhone).text.toString()
        val id: String = findViewById<TextInputEditText>(R.id.textInputEditTextId).text.toString()
        val surname: String = findViewById<TextInputEditText>(R.id.textInputEditTextLastName).text.toString()
        val name: String = findViewById<TextInputEditText>(R.id.textInputEditTextFirstName).text.toString()
        val role = "doctor"

        val database = Firebase.database
        val myref = database.getReference("Doctor").child(username)

        myref.setValue(Doctor(name, surname, id, age, gender, phone, address, availability, department, specialization, password, role))
        goToLoginPage()
    }

    private fun goToLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
