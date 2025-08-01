package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class DoctorProfileActivity : DoctorBaseActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var etName: TextInputEditText
    private lateinit var etSurname: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etSpecialization: TextInputEditText
    private lateinit var btnEditProfile: MaterialButton

    // Remove the username property here! Use the base class's protected username directly

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profile)

        etName = findViewById(R.id.etName)
        etSurname = findViewById(R.id.etSurname)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etSpecialization = findViewById(R.id.etSpecialization)
        btnEditProfile = findViewById(R.id.btnEditProfile)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadProfileData()

        btnEditProfile.setOnClickListener {
            saveProfileData()
        }

        setupBottomNavigation(R.id.nav_profile)
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
        highlightTab(R.id.nav_profile)
    }

    private fun loadProfileData() {
        if (username.isBlank()) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        database = FirebaseDatabase.getInstance().getReference("Doctors").child(username)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    etName.setText(snapshot.child("name").value?.toString() ?: "")
                    etSurname.setText(snapshot.child("surname").value?.toString() ?: "")
                    etEmail.setText(snapshot.child("email").value?.toString() ?: "")
                    etPhone.setText(snapshot.child("number").value?.toString() ?: "")
                    etSpecialization.setText(snapshot.child("specialization").value?.toString() ?: "")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DoctorProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveProfileData() {
        val name = etName.text.toString().trim()
        val surname = etSurname.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val specialization = etSpecialization.text.toString().trim()

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name, surname and email are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mapOf<String, Any>(
            "name" to name,
            "surname" to surname,
            "email" to email,
            "number" to phone,
            "specialization" to specialization
        )

        database = FirebaseDatabase.getInstance().getReference("Doctors").child(username)
        database.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
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

