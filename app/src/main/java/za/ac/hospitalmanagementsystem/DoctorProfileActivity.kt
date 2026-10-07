package za.ac.hospitalmanagementsystem

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
    // Removed etEmail
    private lateinit var etPhone: TextInputEditText
    private lateinit var etSpecialization: TextInputEditText
    private lateinit var btnEditProfile: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profile)

        etName = findViewById(R.id.etName)
        etSurname = findViewById(R.id.etSurname)
        // Removed etEmail initialization
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

        database = FirebaseDatabase.getInstance().getReference("Doctor").child(username)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java) ?: ""
                    val surname = snapshot.child("surname").getValue(String::class.java) ?: ""
                    // Removed email retrieval
                    val phone = snapshot.child("phone").getValue(String::class.java) ?: snapshot.child("number").getValue(String::class.java) ?: ""
                    val specialization = snapshot.child("specialization").getValue(String::class.java) ?: ""

                    etName.setText(name)
                    etSurname.setText(surname)
                    // Removed etEmail setText
                    etPhone.setText(phone)
                    etSpecialization.setText(specialization)
                } else {
                    Toast.makeText(this@DoctorProfileActivity, "Profile data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DoctorProfileActivity, "Failed to load profile: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveProfileData() {
        val name = etName.text.toString().trim()
        val surname = etSurname.text.toString().trim()
        // Removed email validation and retrieval
        val phone = etPhone.text.toString().trim()
        val specialization = etSpecialization.text.toString().trim()

        if (name.isEmpty() || surname.isEmpty()) {  // removed email from required
            Toast.makeText(this, "Name and surname are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val updates = mapOf<String, Any>(
            "name" to name,
            "surname" to surname,
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
