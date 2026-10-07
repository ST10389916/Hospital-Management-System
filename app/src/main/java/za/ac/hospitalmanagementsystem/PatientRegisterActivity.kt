package za.ac.hospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PatientRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_register)

        // Initialize views
        val textViewBack = findViewById<TextView>(R.id.textViewBack)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val genderDropdown = findViewById<AutoCompleteTextView>(R.id.spinnerGender)

        // Set up gender dropdown
        val genderOptions = resources.getStringArray(R.array.gender)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        genderDropdown.setAdapter(adapter)

        val role = intent.getStringExtra("role").toString()
        setSupportActionBar(toolbar)
        supportActionBar!!.title = role

        buttonRegister.setOnClickListener {
            registerPatient(role)
        }

        textViewBack.setOnClickListener {
            goToLoginPage()
        }
    }

    private fun registerPatient(rolename: String) {
        val firstName = findViewById<TextInputEditText>(R.id.textInputEditTextFirstName)
        val lastName = findViewById<TextInputEditText>(R.id.textInputEditTextLastName)
        val patientId = findViewById<TextInputEditText>(R.id.textInputEditTextId)
        val phoneNumber = findViewById<TextInputEditText>(R.id.textInputEditTextPhone)
        val email = findViewById<TextInputEditText>(R.id.textInputEditTextEmail)
        val address = findViewById<TextInputEditText>(R.id.textInputEditTextAddress)
        val gender = findViewById<AutoCompleteTextView>(R.id.spinnerGender)
        val password = findViewById<TextInputEditText>(R.id.textInputEditTextPassword)
        val role = rolename

        // Validate inputs
        if (firstName.text.isNullOrEmpty() || lastName.text.isNullOrEmpty() ||
            patientId.text.isNullOrEmpty() || phoneNumber.text.isNullOrEmpty() ||
            email.text.isNullOrEmpty() || address.text.isNullOrEmpty() ||
            gender.text.isNullOrEmpty() || password.text.isNullOrEmpty()) {
            // Show error message
            return
        }

        val database = Firebase.database

        val myref = database.getReference(rolename).child(email.text.toString())

        myref.setValue(
            Patient(
                patientId.text.toString(),
                firstName.text.toString(),
                lastName.text.toString(),
                phoneNumber.text.toString(),
                address.text.toString(),
                gender.text.toString(), // Changed from selectedItem to text
                password.text.toString(),
                role
            )
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                goToLoginPage()
            } else {
                // Handle registration error
            }
        }
    }

    private fun goToLoginPage() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Optional: finish current activity to prevent going back
    }
}