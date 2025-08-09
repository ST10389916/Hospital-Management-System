package za.ac.hospitalmanagementsystem

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
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

        database = FirebaseDatabase.getInstance().getReference("Doctor")

        val firstNameInput = findViewById<TextInputEditText>(R.id.textInputEditTextFirstName)
        val lastNameInput = findViewById<TextInputEditText>(R.id.textInputEditTextLastName)
        val phoneInput = findViewById<TextInputEditText>(R.id.textInputEditTextPhone)
        val departmentInput = findViewById<MaterialAutoCompleteTextView>(R.id.spinnerDepartment)
        val specializationInput = findViewById<MaterialAutoCompleteTextView>(R.id.spinnerSpecialization)
        val availabilityInput = findViewById<MaterialAutoCompleteTextView>(R.id.spinnerAvailability)
        val addressInput = findViewById<TextInputEditText>(R.id.textInputEditTextAddress)

        // Setup adapters for dropdowns from string arrays
        val availabilityAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.timeframe)
        )
        val departmentAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.departments)
        )
        val specializationAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.specialization)
        )

        availabilityInput.setAdapter(availabilityAdapter)
        departmentInput.setAdapter(departmentAdapter)
        specializationInput.setAdapter(specializationAdapter)

        // Show dropdown on click
        availabilityInput.setOnClickListener { availabilityInput.showDropDown() }
        departmentInput.setOnClickListener { departmentInput.showDropDown() }
        specializationInput.setOnClickListener { specializationInput.showDropDown() }

        // Get doctorId from intent extras
        val doctorId = intent.getStringExtra("doctorId") ?: ""

        // Load doctor data from Firebase and populate fields
        if (doctorId.isNotEmpty()) {
            database.child(doctorId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val doctorData = snapshot.value as Map<*, *>

                    firstNameInput.setText(doctorData["name"] as? String ?: "")
                    lastNameInput.setText(doctorData["surname"] as? String ?: "")
                    phoneInput.setText(doctorData["phone"] as? String ?: "")
                    departmentInput.setText(doctorData["department"] as? String ?: "", false)
                    specializationInput.setText(doctorData["specialization"] as? String ?: "", false)
                    availabilityInput.setText(doctorData["availability"] as? String ?: "", false)
                    addressInput.setText(doctorData["address"] as? String ?: "")
                } else {
                    Toast.makeText(this, "Doctor data not found", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to fetch doctor data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Doctor ID is missing", Toast.LENGTH_SHORT).show()
        }

        val buttonEdit = findViewById<MaterialButton>(R.id.buttonEdit)
        buttonEdit.setOnClickListener {
            if (validateInputs()) {
                val updatedDoctor = mapOf(
                    "name" to firstNameInput.text.toString(),
                    "surname" to lastNameInput.text.toString(),
                    "phone" to phoneInput.text.toString(),
                    "department" to departmentInput.text.toString(),
                    "specialization" to specializationInput.text.toString(),
                    "availability" to availabilityInput.text.toString(),
                    "address" to addressInput.text.toString()
                )

                database.child(doctorId).updateChildren(updatedDoctor)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Doctor updated successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update doctor: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
