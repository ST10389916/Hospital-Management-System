package za.ac.hospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PatientRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_register)
        val textViewBack = findViewById<TextView>(R.id.textViewBack)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val role = intent.getStringExtra("role").toString()
        setSupportActionBar(toolbar)
        supportActionBar!!.title = role
        buttonRegister.setOnClickListener {
            //val query = "insert into Patient values()"
            registerPatient(role)
        }
        textViewBack.setOnClickListener {
            goToLoginPage()
        }
    }

    private fun registerPatient(rolename: String) {
        val firstName = findViewById<TextInputEditText>(R.id.textInputEditTextFirstName)
        val lastName = findViewById<TextInputEditText>(R.id.textInputEditTextLastName)
        val patientId = findViewById<TextInputEditText>(R.id.textInputEditTextId)
        val phoneNumber = findViewById<TextInputEditText>(R.id.textInputEditTextPhone)
        val email = findViewById<TextInputEditText>(R.id.textInputEditTextEmail)
        val address = findViewById<TextInputEditText>(R.id.textInputEditTextAddress)
        val gender = findViewById<Spinner>(R.id.spinnerGender)
        val password = findViewById<TextInputEditText>(R.id.textInputEditTextPassword)
        val role = rolename


        val database  = Firebase.database
        val myref = database.getReference(rolename).child(email.text.toString())

        myref.setValue(Patient(patientId.text.toString(),firstName.text.toString(),
            lastName.text.toString(),phoneNumber.text.toString(),
            address.text.toString(),gender.selectedItem.toString(),
            password.text.toString(),role))

        goToLoginPage()
    }

    private fun goToLoginPage() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }
}