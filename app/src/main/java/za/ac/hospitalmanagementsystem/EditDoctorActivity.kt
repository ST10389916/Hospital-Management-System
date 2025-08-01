package za.ac.hospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditDoctorActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_doctor)

        // Setup toolbar with back button
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Update Doctor Details"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Get intent extras
        val username = intent.getStringExtra("username") ?: ""
        val adminName = intent.getStringExtra("name") ?: ""
        val adminSurname = intent.getStringExtra("surname") ?: ""
        val adminNumber = intent.getStringExtra("number") ?: ""
        val adminUsername = intent.getStringExtra("userName") ?: ""

        // Set click listener for update button
        val buttonEdit = findViewById<MaterialButton>(R.id.buttonEdit)
        buttonEdit.setOnClickListener {
            if (validateInputs()) {
                editDoctor(username, adminName, adminSurname, adminNumber, adminUsername)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun validateInputs(): Boolean {
        val firstName = findViewById<TextInputEditText>(R.id.textInputEditTextFirstName).text.toString()
        val lastName = findViewById<TextInputEditText>(R.id.textInputEditTextLastName).text.toString()
        val phone = findViewById<TextInputEditText>(R.id.textInputEditTextPhone).text.toString()

        if (firstName.isEmpty()) {
            Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (lastName.isEmpty()) {
            Toast.makeText(this, "Please enter last name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun editDoctor(
        username: String,
        adminName: String,
        adminSurname: String,
        adminNumber: String,
        adminUsername: String
    ) {
        val availability = findViewById<TextInputEditText>(R.id.spinnerAvailability).text.toString()
        val department = findViewById<TextInputEditText>(R.id.spinnerDepartment).text.toString()
        val specialization = findViewById<TextInputEditText>(R.id.spinnerSpecialization).text.toString()
        val address = findViewById<TextInputEditText>(R.id.textInputEditTextAddress).text.toString()
        val phone = findViewById<TextInputEditText>(R.id.textInputEditTextPhone).text.toString()
        val surname = findViewById<TextInputEditText>(R.id.textInputEditTextLastName).text.toString()
        val name = findViewById<TextInputEditText>(R.id.textInputEditTextFirstName).text.toString()

        database = FirebaseDatabase.getInstance().getReference("Doctor")
        val updateDoctor = mapOf(
            "availability" to availability,
            "department" to department,
            "specialization" to specialization,
            "address" to address,
            "phone" to phone,
            "surname" to surname,
            "name" to name
        )

        database.child(username).updateChildren(updateDoctor)
            .addOnSuccessListener {
                Toast.makeText(this, "Doctor updated successfully", Toast.LENGTH_SHORT).show()
                navigateBackToAdmin(adminName, adminSurname, adminNumber, adminUsername)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update doctor: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateBackToAdmin(
        adminName: String,
        adminSurname: String,
        adminNumber: String,
        adminUsername: String
    ) {
        val intent = Intent(this, AdminActivity::class.java).apply {
            putExtra("name", adminName)
            putExtra("surname", adminSurname)
            putExtra("number", adminNumber)
            putExtra("userName", adminUsername)
        }
        startActivity(intent)
        finish()
    }
}