package za.ac.hospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : BaseActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var currentUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Get username from intent
        currentUsername = intent.getStringExtra("userName").toString()
        val textViewUsername = findViewById<TextView>(R.id.textViewUsername)
        textViewUsername.text = currentUsername

        // Initialize UI components
        val buttonEdit = findViewById<Button>(R.id.buttonEdit)
        buttonEdit.setOnClickListener {
            editProfile()
        }

        // Load patient data
        loadPatientData()

        setupBottomNavigation(R.id.nav_profile)
    }

    private fun loadPatientData() {
        database = FirebaseDatabase.getInstance().getReference("Patient")
        database.child(currentUsername).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Get patient data
                    val name = snapshot.child("name").value.toString()
                    val surname = snapshot.child("surname").value.toString()
                    val address = snapshot.child("address").value.toString()
                    val phoneNumber = snapshot.child("phoneNumber").value.toString()

                    // Set values to EditText fields
                    findViewById<EditText>(R.id.editTextViewName).setText(name)
                    findViewById<EditText>(R.id.editTextViewSurname).setText(surname)
                    findViewById<EditText>(R.id.editTextViewAddress).setText(address)
                    findViewById<EditText>(R.id.editTextViewNumber).setText(phoneNumber)
                } else {
                    Toast.makeText(this@ProfileActivity, "Patient data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Failed to load patient data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun editProfile() {
        val name = findViewById<EditText>(R.id.editTextViewName).text.toString()
        val surname = findViewById<EditText>(R.id.editTextViewSurname).text.toString()
        val address = findViewById<EditText>(R.id.editTextViewAddress).text.toString()
        val number = findViewById<EditText>(R.id.editTextViewNumber).text.toString()

        // Validate inputs
        if (name.isEmpty() || surname.isEmpty() || number.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Update patient data in Firebase
        val updatePatient = mapOf(
            "name" to name,
            "surname" to surname,
            "address" to address,
            "phoneNumber" to number
        )

        database.child(currentUsername).updateChildren(updatePatient)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                // Return to PatientActivity with updated data
                val intent = Intent(this, PatientActivity::class.java).apply {
                    putExtra("userName", currentUsername)
                    putExtra("name", name)
                    putExtra("surname", surname)
                    putExtra("number", number)
                }
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }
}