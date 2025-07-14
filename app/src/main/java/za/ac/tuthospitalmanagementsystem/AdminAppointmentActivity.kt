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

class AdminAppointmentActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_appointment)
        val buttonEdit = findViewById<Button>(R.id.buttonEdit)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val name= intent.getStringExtra("name")
        val surname= intent.getStringExtra("surname")
        val number= intent.getStringExtra("number")
        val username= intent.getStringExtra("userName")
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Appointments"
        loadAppointment()
        buttonEdit.setOnClickListener {
            goToEditAppointment(name,surname,number,username)
        }
        buttonDelete.setOnClickListener {
            deleteAppointment()
        }
        loadAppointment()
    }

    private fun deleteAppointment() {
        val editTextAppointmentNo = findViewById<EditText>(R.id.editTextAppointmentNo).text.toString()
        database = FirebaseDatabase.getInstance().getReference("Appointment").child(editTextAppointmentNo)
        database.removeValue().addOnSuccessListener {
            Toast.makeText(this,"Appointment removed",Toast.LENGTH_LONG).show()
        }
    }

    private fun goToEditAppointment(
        name: String?,
        surname: String?,
        number: String?,
        username: String?
    ) {
        val editTextAppointmentNo = findViewById<EditText>(R.id.editTextAppointmentNo).text.toString()
        database = FirebaseDatabase.getInstance().getReference("Appointment").child(editTextAppointmentNo)
        database.get().addOnSuccessListener {
            if(it.exists()){

                intent = Intent(this,EditAppointmentActivity::class.java)
                intent.putExtra("name",name)
                intent.putExtra("surname",surname)
                intent.putExtra("number",number)
                intent.putExtra("userName",username)

                intent.putExtra("appointmentNo",editTextAppointmentNo)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Appointment Number does not exist",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadAppointment() {
        val textViewAppointments = findViewById<TextView>(R.id.textViewAppointments)

        database = FirebaseDatabase.getInstance().getReference("Appointment")
        database.get().addOnSuccessListener {
            val sb = StringBuilder()
            for(i in it.children){

                val availability = i.child("availability").value
                val date = i.child("date").value
                val disease = i.child("disease").value
                var doctor : String = i.child("doctor").value.toString()
                if(doctor.contentEquals("null")){
                    doctor = "Not yet assigned"
                }
                val patient = i.child("patient").value
                val id = i.key

                sb.append("Appointment number: $id\nPatient: $patient\nDoctor: $doctor\nAvailability: $availability\nDate: $date\nDisease: $disease\n_____________________________\n")
            }
            textViewAppointments.text = sb
        }.addOnFailureListener {
            Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
        }
    }
}