package za.ac.tuthospitalmanagementsystem

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var radioPatient :RadioButton
    private lateinit var radioAdmin :RadioButton
    private lateinit var radioDoctor : RadioButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        val textViewAccount= findViewById<TextView>(R.id.textViewAccount)
        val textViewRegister = findViewById<TextView>(R.id.textViewRegister)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        radioPatient = findViewById(R.id.radioPatient)
        radioAdmin = findViewById(R.id.radioAdmin)
        radioDoctor = findViewById(R.id.radioDoctor)
        textViewRegister.setOnClickListener{
            goToRegisterPage()
        }

        textViewAccount.setOnClickListener {
            goToTwitterAccount()
        }
        loginButton.setOnClickListener {
            val userName : String = findViewById<TextInputEditText>(R.id.textInputEditTextEmail).text.toString().trim()
            if(userName.isNotEmpty()){
                goToPatient(userName)
            }else{
                 Toast.makeText(this,"Enter your your username",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToTwitterAccount() {
        val url = "https://twitter.com/HealthZA?ref_src=twsrc%5Egoogle%7Ctwcamp%5Eserp%7Ctwgr%5Eauthor"

        val openURL = Intent(Intent.ACTION_VIEW)

        openURL.data = Uri.parse(url)

        startActivity(openURL)
    }

    private fun goToPatient(userName : String) {

        if(radioAdmin.isChecked){

            database = FirebaseDatabase.getInstance().getReference("Admin").child(userName)
            database.get().addOnSuccessListener {
                if(it.exists()){
                    val password : String = findViewById<TextInputEditText>(R.id.textInputEditTextPassword).text.toString()

                    val passwordData : String = it.child("password").value.toString()

                    if(password.contentEquals(passwordData)){
                        val name : String = it.child("name").value.toString()
                        val surname : String = it.child("surname").value.toString()
                        val number : String = it.child("number").value.toString()

                        val intent = Intent(this,AdminActivity::class.java)
                        intent.putExtra("name",name)
                        intent.putExtra("surname",surname)
                        intent.putExtra("number",number)
                        intent.putExtra("userName",userName)
                        startActivity(intent)
                        Toast.makeText(this,"Login successful",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,"Incorrect Login Details",Toast.LENGTH_LONG).show()
                    }
                    //Toast.makeText(this,"Login as admin",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Incorrect Login Details",Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this,"failed",Toast.LENGTH_LONG).show()
            }
        }
        if(radioDoctor.isChecked){

            database = FirebaseDatabase.getInstance().getReference("Doctor").child(userName)
            database.get().addOnSuccessListener {
                if(it.exists()){
                    val password : String = findViewById<TextInputEditText>(R.id.textInputEditTextPassword).text.toString()

                    val passwordData : String = it.child("password").value.toString()

                    if(password.contentEquals(passwordData)){
                        val name : String = it.child("name").value.toString()
                        val surname : String = it.child("surname").value.toString()
                        val number : String = it.child("phone").value.toString()

                        val intent = Intent(this,DoctorActivity::class.java)
                        intent.putExtra("name",name)
                        intent.putExtra("surname",surname)
                        intent.putExtra("number",number)
                        intent.putExtra("username",userName)
                        startActivity(intent)
                        Toast.makeText(this,"Login successful",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,"Incorrect Login Details",Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(this,"Incorrect Login Details",Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this,"failed",Toast.LENGTH_LONG).show()
            }
        }
        if(radioPatient.isChecked){

            database = FirebaseDatabase.getInstance().getReference("Patient").child(userName)
            database.get().addOnSuccessListener {
                if(it.exists()){
                    val password : String = findViewById<TextInputEditText>(R.id.textInputEditTextPassword).text.toString()

                    val passwordData : String = it.child("password").value.toString()

                    if(password.contentEquals(passwordData)){

                        val name : String = it.child("name").value.toString()
                        val surname : String = it.child("surname").value.toString()
                        val number : String = it.child("phoneNumber").value.toString()

                        val intent = Intent(this,PatientActivity::class.java)
                        intent.putExtra("name",name)
                        intent.putExtra("surname",surname)
                        intent.putExtra("number",number)
                        intent.putExtra("userName",userName)
                        startActivity(intent)
                        Toast.makeText(this,"Login successful",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this,"Incorrect Login Details",Toast.LENGTH_LONG).show()
                    }
                    //Toast.makeText(this,"Login as patient",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Incorrect Login Details",Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this,"failed",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToRegisterPage() {

        if (radioPatient.isChecked) {
            val intent = Intent(this, PatientRegisterActivity::class.java)
            // Pass the role "patient" to the registration activity
            intent.putExtra("role", "Patient") // Directly pass the string
            startActivity(intent)
        }
        if (radioAdmin.isChecked) {
            val intent = Intent(this, PatientRegisterActivity::class.java)
            // Pass the username to the registration activity
            intent.putExtra("role", "Admin") // Directly pass the string
            startActivity(intent)
        }
    }
}