package za.ac.tuthospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DoctorRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_register)
        //val textViewBack = findViewById<TextView>(R.id.textViewBack)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Doctor Registration"
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonRegister.setOnClickListener {
            val username : String= findViewById<TextInputEditText>(R.id.textInputEditTextEmail).text.toString()
            if(username.isNotEmpty()){
                registerDoctor(username)
                val intent = Intent(this,AdminActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun registerDoctor(username: String) {
        val password :String = findViewById<TextInputEditText>(R.id.textInputEditTextPassword).text.toString()
        val availability :String = findViewById<Spinner>(R.id.spinnerAvailability).selectedItem.toString()
        val department :String = findViewById<Spinner>(R.id.spinnerDepartment).selectedItem.toString()
        val specialization :String = findViewById<Spinner>(R.id.spinnerSpecialization).selectedItem.toString()
        val address :String = findViewById<TextInputEditText>(R.id.textInputEditTextAddress).text.toString()
        val gender :String = findViewById<Spinner>(R.id.spinnerGender).selectedItem.toString()
        val age :String = findViewById<TextInputEditText>(R.id.textInputEditTextAge).text.toString()
        val phone :String = findViewById<TextInputEditText>(R.id.textInputEditTextPhone).text.toString()
        val id :String = findViewById<TextInputEditText>(R.id.textInputEditTextId).text.toString()
        val surname :String = findViewById<TextInputEditText>(R.id.textInputEditTextLastName).text.toString()
        val name :String = findViewById<TextInputEditText>(R.id.textInputEditTextFirstName).text.toString()
        val role= "doctor"

        val database  = Firebase.database
        val myref = database.getReference("Doctor").child(username)

        myref.setValue(Doctor(name,surname,id,age,gender,phone,address,availability,department,specialization,password,role))

    }
}