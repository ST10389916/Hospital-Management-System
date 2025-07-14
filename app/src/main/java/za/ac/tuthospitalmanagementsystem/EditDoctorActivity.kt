package za.ac.tuthospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditDoctorActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_doctor)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Update Doctor Details"
        val username :String = intent.getStringExtra("username").toString()
        val buttonEdit = findViewById<Button>(R.id.buttonEdit)
        val adminName = intent.getStringExtra("name").toString()
        val adminSurname= intent.getStringExtra("surname").toString()
        val adminNumber= intent.getStringExtra("number").toString()
        val adminUsername= intent.getStringExtra("userName").toString()
        buttonEdit.setOnClickListener {
            editDoctor(username,adminName,adminSurname,adminNumber,adminUsername)
        }
    }

    private fun editDoctor(username: String,
                           adminName: String,
                           adminSurname: String,
                           adminNumber: String,
                           adminUsername: String) {
        val availability :String = findViewById<Spinner>(R.id.spinnerAvailability).selectedItem.toString()
        val department :String = findViewById<Spinner>(R.id.spinnerDepartment).selectedItem.toString()
        val specialization :String = findViewById<Spinner>(R.id.spinnerSpecialization).selectedItem.toString()
        val address :String = findViewById<TextInputEditText>(R.id.textInputEditTextAddress).text.toString()
        val phone :String = findViewById<TextInputEditText>(R.id.textInputEditTextPhone).text.toString()
        val surname :String = findViewById<TextInputEditText>(R.id.textInputEditTextLastName).text.toString()
        val name :String = findViewById<TextInputEditText>(R.id.textInputEditTextFirstName).text.toString()

        database = FirebaseDatabase.getInstance().getReference("Doctor")
        val updateDoctor = mapOf(
            "availability" to availability,
            "department" to department,
            "specialization" to specialization,
            "address" to address,
            "phone" to phone,
            "surname" to surname,
            "name" to name
        )
        database.child(username).updateChildren(updateDoctor)
        Toast.makeText(this,"Updated",Toast.LENGTH_SHORT).show()
        intent = Intent(this,AdminActivity::class.java)
        intent.putExtra("name",adminName)
        intent.putExtra("surname",adminSurname)
        intent.putExtra("number",adminNumber)
        intent.putExtra("userName",adminUsername)
        startActivity(intent)
    }
}