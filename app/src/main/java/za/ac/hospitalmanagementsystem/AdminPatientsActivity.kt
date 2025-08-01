package za.ac.hospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import za.ac.hospitalmanagementsystem.admin.AdminBaseActivity
import java.lang.StringBuilder

class AdminPatientsActivity : AdminBaseActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var patientsContainer: LinearLayout
    private lateinit var patientsListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_patients)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        patientsContainer = findViewById(R.id.patientsContainer)
        patientsListContainer = findViewById(R.id.patientsListContainer)
        loadPatients()
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

                patientCard.findViewById<MaterialButton>(R.id.buttonClose).setOnClickListener {
                    goToAdmin(
                        name,
                        surname,
                        "", // number if available
                        username
                    )
                }

                patientCard.findViewById<MaterialButton>(R.id.buttonDelete).setOnClickListener {
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

    private fun goToAdmin(name: String, surname: String, number: String, username: String) {
        startActivity(Intent(this, AdminActivity::class.java).apply {
            putExtra("name", name)
            putExtra("surname", surname)
            putExtra("number", number)
            putExtra("userName", username)
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}