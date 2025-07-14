package za.ac.tuthospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.StringBuilder

class AdminPatientsActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_patients)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Patients"
        loadingPatient()

        val name = intent.getStringExtra("name").toString()
        val surname= intent.getStringExtra("surname").toString()
        val number= intent.getStringExtra("number").toString()
        val username= intent.getStringExtra("userName").toString()
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)
        val buttonClose = findViewById<Button>(R.id.buttonClose)
        buttonClose.setOnClickListener {
            goToAdmin(name, surname, number, username)
        }
        buttonDelete.setOnClickListener {
            val editTextPatientId = findViewById<TextView>(R.id.editTextDoctorId)

            val patientEmail : String = editTextPatientId.text.toString()
            if(patientEmail.isNotEmpty()){
                deleteRecord(patientEmail)
                editTextPatientId.text = ""
            }else{
                Toast.makeText(this,"Enter patient ID",Toast.LENGTH_LONG).show()
            }
            loadingPatient()
        }
    }

    private fun goToAdmin(name: String?, surname: String?, number: String?, username: String?) {
        intent = Intent(this,AdminActivity::class.java)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        intent.putExtra("userName",username)
        startActivity(intent)
    }

    private fun loadingPatient() {
        val textViewAppointments = findViewById<TextView>(R.id.textViewDoctors)

        database = FirebaseDatabase.getInstance().getReference("Patient")
        database.get().addOnSuccessListener {
            val sb = StringBuilder()
            for(i in it.children){

                val name = i.child("name").value
                val surname = i.child("surname").value
                val gender = i.child("gender").value
                val id = i.key

                sb.append("Username $id\nName $name\nSurname $surname\nGender $gender\n_____________________________\n")
            }
            textViewAppointments.text = sb
        }.addOnFailureListener {
            Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteRecord(patientEmail: String) {

        database = FirebaseDatabase.getInstance().getReference("Patient").child(patientEmail)
        database.removeValue().addOnSuccessListener {
            Toast.makeText(this,"Patient removed",Toast.LENGTH_LONG).show()
        }
    }
}