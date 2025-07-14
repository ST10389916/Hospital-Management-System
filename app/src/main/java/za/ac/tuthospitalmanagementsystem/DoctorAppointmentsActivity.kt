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
import java.lang.StringBuilder

class DoctorAppointmentsActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_appointments)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Appointments"

        val name: String = intent.getStringExtra("name").toString()
        val surname: String= intent.getStringExtra("surname").toString()
        val number: String= intent.getStringExtra("number").toString()
        val username: String = intent.getStringExtra("username").toString()

        val buttonPostpone = findViewById<Button>(R.id.buttonPostpone)

        buttonPostpone.setOnClickListener {
            goToPostpone(username,name,surname,number)
        }
        getAppointments(username)
    }

    private fun goToPostpone(username: String, name: String, surname: String, number: String) {
        val appointmentNo = findViewById<EditText>(R.id.editTextAppointmentNo)
        intent = Intent(this,PostponeActivity::class.java)
        intent.putExtra("appointmentNo",appointmentNo.text.toString())
        intent.putExtra("username",username)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        startActivity(intent)
        appointmentNo.setText("")
    }

    private fun getAppointments(username: String) {
        val appointments = findViewById<TextView>(R.id.textViewAppointments)

        database = FirebaseDatabase.getInstance().getReference("Appointment")
        database.get().addOnSuccessListener {
            val sb = StringBuilder()
            for(i in it.children){
                val doctor:String = i.child("doctor").value.toString()
                if(username.contentEquals(doctor)){
                    val id = i.key
                    val patient:String = i.child("patient").value.toString()
                    val date = i.child("date").value
                    val disease = i.child("disease").value
                    val availability = i.child("availability").value
                    sb.append("Appointment Number : $id\nPatient id : $patient\nDate : $date\ndisease : $disease\nAppointment Time : $availability\n_____________________________\n")
                }
            }
            appointments.text = sb
        }.addOnFailureListener {
            Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
        }
    }
}