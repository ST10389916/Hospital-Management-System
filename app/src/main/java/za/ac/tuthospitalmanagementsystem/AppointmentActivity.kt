package za.ac.tuthospitalmanagementsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.StringBuilder

class AppointmentActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val username : String = intent.getStringExtra("userName").toString()
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Appointments"

        getAppointment(username)
    }

    private fun getAppointment(username: String) {

        val appointments = findViewById<TextView>(R.id.textViewAppointments)

        database = FirebaseDatabase.getInstance().getReference("Appointment")
        database.get().addOnSuccessListener {
            val sb = StringBuilder()
            for(i in it.children){
                val patient:String = i.child("patient").value.toString()
                if(username.contentEquals(patient)){
                    val id = i.key
                    var doctor:String = i.child("doctor").value.toString()
                    if(doctor.contentEquals("null")){
                        doctor = "no yet assigned"
                    }
                    val date = i.child("date").value
                    val disease = i.child("disease").value
                    val availability = i.child("availability").value
                    sb.append("Appointment Number : $id\nDoctor name : $doctor\nDate : $date\ndisease : $disease\nAppointment Time : $availability\n_____________________________\n")
                }
            }
            appointments.text = sb
        }.addOnFailureListener {
            Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
        }
    }
}