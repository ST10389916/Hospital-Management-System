package za.ac.tuthospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Update Profile"
        val buttonEdit = findViewById<Button>(R.id.buttonEdit)
        val username : String = intent.getStringExtra("userName").toString()

        val textViewUsername = findViewById<TextView>(R.id.textViewUsername)

        textViewUsername.text = username
        buttonEdit.setOnClickListener {
            editProfile(username)
        }
    }

    private fun editProfile(username: String) {
        val name = findViewById<EditText>(R.id.editTextViewName).text.toString()
        val surname = findViewById<EditText>(R.id.editTextViewSurname).text.toString()
        val address = findViewById<EditText>(R.id.editTextViewAddress).text.toString()
        val number = findViewById<EditText>(R.id.editTextViewNumber).text.toString()

        database = FirebaseDatabase.getInstance().getReference("Patient")
        val updatePatient = mapOf(
            "name" to name,
            "surname" to surname,
            "address" to address,
            "phoneNumber" to number
        )
        database.child(username).updateChildren(updatePatient)
        Toast.makeText(this,"Updated", Toast.LENGTH_SHORT).show()
        intent = Intent(this,PatientActivity::class.java)
        intent.putExtra("userName",username)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        startActivity(intent)
    }
}