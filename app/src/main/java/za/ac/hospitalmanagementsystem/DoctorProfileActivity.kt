package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DoctorProfileActivity : DoctorBaseActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var tvName: TextView
    private lateinit var tvSurname: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvSpecialization: TextView
    private lateinit var btnEditProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_profile)

        // Initialize views
        tvName = findViewById(R.id.tvName)
        tvSurname = findViewById(R.id.tvSurname)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvSpecialization = findViewById(R.id.tvSpecialization)
        btnEditProfile = findViewById(R.id.btnEditProfile)

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load profile data
        loadProfileData()

        // Set up button click listener
        btnEditProfile.setOnClickListener {
            goToEditProfile()
        }

        // Setup bottom navigation
        setupBottomNavigation(R.id.nav_profile)
    }

    override fun onResume() {
        super.onResume()
        loadProfileData() // Refresh data when returning from EditProfile
        highlightTab(R.id.nav_profile)
    }

    private fun loadProfileData() {
        database = FirebaseDatabase.getInstance().getReference("Doctors").child(username)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    tvName.text = snapshot.child("name").value.toString()
                    tvSurname.text = snapshot.child("surname").value.toString()
                    tvEmail.text = snapshot.child("email").value.toString()
                    tvPhone.text = snapshot.child("number").value.toString()
                    tvSpecialization.text = snapshot.child("specialization").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DoctorProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun goToEditProfile() {
        startActivity(Intent(this, EditDoctorProfileActivity::class.java).apply {
            putExtra("username", username)
            putExtra("name", name)
            putExtra("surname", surname)
            putExtra("number", number)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}