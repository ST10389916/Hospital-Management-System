package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import za.ac.hospitalmanagementsystem.admin.AdminAISupportActivity
import za.ac.hospitalmanagementsystem.admin.AdminBaseActivity

class AdminPatientsActivity : AdminBaseActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var patientsContainer: LinearLayout
    private lateinit var patientsListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_patients)

        patientsContainer = findViewById(R.id.patientsContainer)
        patientsListContainer = findViewById(R.id.patientsListContainer)
        loadPatients()

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

    private fun loadPatients() {
        database = FirebaseDatabase.getInstance().getReference("Patient")
        database.get().addOnSuccessListener { snapshot ->
            patientsListContainer.removeAllViews() // Clear existing views

            if (!snapshot.exists()) {
                // Show "No patients" message
                val noPatientsText = TextView(this).apply {
                    text = "No patients found"
                    setTextAppearance(android.R.style.TextAppearance_Medium)
                    gravity = Gravity.CENTER
                    setPadding(0, 32.dpToPx(), 0, 0)
                }
                patientsListContainer.addView(noPatientsText)
                return@addOnSuccessListener
            }

            for (patientSnapshot in snapshot.children) {
                val patientCard = layoutInflater.inflate(
                    R.layout.item_patient_card,
                    patientsListContainer,
                    false
                ) as MaterialCardView

                val name = patientSnapshot.child("name").value.toString()
                val surname = patientSnapshot.child("surname").value.toString()
                val gender = patientSnapshot.child("gender").value.toString()
                val username = patientSnapshot.key.toString()

                patientCard.findViewById<TextView>(R.id.textPatientName).text =
                    "$name $surname"
                patientCard.findViewById<TextView>(R.id.textPatientUsername).text =
                    "Username: $username"
                patientCard.findViewById<TextView>(R.id.textPatientGender).text =
                    "Gender: $gender"

                patientCard.findViewById<MaterialButton>(R.id.buttonDelete).setOnClickListener {
                    deletePatient(username, patientCard)
                }
                patientCard.findViewById<MaterialButton>(R.id.buttonClose).setOnClickListener {
                    deletePatient(username, patientCard)
                }
                patientsListContainer.addView(patientCard)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load patients", Toast.LENGTH_LONG).show()
        }
    }

    private fun deletePatient(username: String, cardView: View) {
        database.child(username).removeValue().addOnSuccessListener {
            patientsListContainer.removeView(cardView)
            Toast.makeText(this, "Patient deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()



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
}