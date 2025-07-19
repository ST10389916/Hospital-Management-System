package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.*

class EditDoctorProfileActivity : DoctorBaseActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etSpecialization: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_doctor_profile)

        // Initialize views
        etName = findViewById(R.id.etName)
        etSurname = findViewById(R.id.etSurname)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etSpecialization = findViewById(R.id.etSpecialization)
        btnSave = findViewById(R.id.btnSave)

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Edit Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load current profile data
        loadCurrentProfile()

        // Set up button click listener
        btnSave.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadCurrentProfile() {
        database = FirebaseDatabase.getInstance().getReference("Doctors").child(username)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    etName.setText(snapshot.child("name").value.toString())
                    etSurname.setText(snapshot.child("surname").value.toString())
                    etEmail.setText(snapshot.child("email").value.toString())
                    etPhone.setText(snapshot.child("number").value.toString())
                    etSpecialization.setText(snapshot.child("specialization").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditDoctorProfileActivity,
                    "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProfile() {
        val name = etName.text.toString().trim()
        val surname = etSurname.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val specialization = etSpecialization.text.toString().trim()

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || phone.isEmpty() || specialization.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = hashMapOf<String, Any>(
            "name" to name,
            "surname" to surname,
            "email" to email,
            "number" to phone,
            "specialization" to specialization
        )

        database.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Return to profile view
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}