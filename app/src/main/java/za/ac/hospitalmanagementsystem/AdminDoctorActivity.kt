package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import za.ac.hospitalmanagementsystem.admin.AdminAISupportActivity
import za.ac.hospitalmanagementsystem.admin.AdminBaseActivity

class AdminDoctorActivity : AdminBaseActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var doctorListContainer: ViewGroup
    private lateinit var noDoctorsText: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_doctor)

        // Initialize views
        doctorListContainer = findViewById(R.id.doctorListContainer)
        noDoctorsText = findViewById(R.id.textViewNoDoctors)

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Manage Doctors"
            setDisplayHomeAsUpEnabled(true)
        }

        // Initialize database reference
        database = FirebaseDatabase.getInstance().getReference("Doctor")

        // Load doctors
        loadDoctors()

        // Initialize BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_doctors -> {
                    goToDoctors(name, surname, number, username)
                    true
                }
                R.id.nav_patients -> {
                    goToPatients(name, surname, number, username)
                    true
                }
                R.id.nav_appointments -> {
                    goToAppointment(name, surname, number, username)
                    true
                }
                R.id.nav_ai_support -> {
                    goToAISupport(name, surname, number, username)
                    true
                }
                R.id.nav_logout -> {
                    goToLogin()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadDoctors() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorListContainer.removeAllViews()

                if (!snapshot.exists()) {
                    noDoctorsText.visibility = View.VISIBLE
                    return
                } else {
                    noDoctorsText.visibility = View.GONE
                }

                for (doctorSnapshot in snapshot.children) {
                    val doctor = doctorSnapshot.getValue(Doctor::class.java)
                    doctor?.let {
                        addDoctorCard(it, doctorSnapshot.key ?: "")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminDoctorActivity, "Failed to load doctors: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addDoctorCard(doctor: Doctor, doctorId: String) {
        val inflater = LayoutInflater.from(this)
        val cardView = inflater.inflate(R.layout.item_doctor_card, doctorListContainer, false)

        cardView.findViewById<MaterialTextView>(R.id.textDoctorName).text = "Dr. ${doctor.name} ${doctor.surname}"
        cardView.findViewById<MaterialTextView>(R.id.textDoctorSpecialty).text = "Specialty: ${doctor.specialization}"
        cardView.findViewById<MaterialTextView>(R.id.textDoctorDepartment).text = "Department: ${doctor.department}"
        cardView.findViewById<MaterialTextView>(R.id.textDoctorEmail).text = "Email: $doctorId"
        cardView.findViewById<MaterialTextView>(R.id.textDoctorGender).text = "Gender: ${doctor.gender}"
        cardView.findViewById<MaterialTextView>(R.id.textDoctorContact).text = "Contact: ${doctor.phoneNumber}"

        cardView.findViewById<MaterialButton>(R.id.btnEditDoctor).setOnClickListener {
            navigateToEditDoctor(doctorId, doctor)
        }

        cardView.findViewById<MaterialButton>(R.id.btnDeleteDoctor).setOnClickListener {
            deleteDoctor(doctorId)
        }

        doctorListContainer.addView(cardView)
    }

    private fun navigateToEditDoctor(doctorId: String, doctor: Doctor) {
        val intent = Intent(this, EditDoctorActivity::class.java).apply {
            putExtra("doctorId", doctorId)
            putExtra("name", doctor.name)
            putExtra("surname", doctor.surname)
            putExtra("number", doctor.phoneNumber)
            putExtra("gender", doctor.gender)
            putExtra("department", doctor.department)
            putExtra("specialization", doctor.specialization)
        }
        startActivity(intent)
    }

    private fun deleteDoctor(doctorId: String) {
        database.child(doctorId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Doctor deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete doctor: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    private fun goToAISupport(name: String?, surname: String?, number: String?, username: String?) {
        val intent = Intent(this, AdminAISupportActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("number", number)
        intent.putExtra("username", username)
        startActivity(intent)
    }
    private fun goToDoctors(name: String?, surname: String?, number: String?, username: String?) {
        val intent = Intent(this, AdminDoctorActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("number", number)
        intent.putExtra("userName", username)
        startActivity(intent)
    }

    private fun goToPatients(name: String?, surname: String?, number: String?, username: String?) {
        val intent = Intent(this, AdminPatientsActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("number", number)
        intent.putExtra("userName", username)
        startActivity(intent)
    }

    private fun goToDoctorRegister() {
        val intent = Intent(this, DoctorRegisterActivity::class.java)
        startActivity(intent)
    }

    private fun goToAppointment(name: String?, surname: String?, number: String?, username: String?) {
        val intent = Intent(this, AdminAppointmentActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("number", number)
        intent.putExtra("userName", username)
        startActivity(intent)
    }
    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    data class Doctor(
        val name: String = "",
        val surname: String = "",
        val gender: String = "",
        val phoneNumber: String = "",
        val department: String = "",
        val specialization: String = ""
    )
}