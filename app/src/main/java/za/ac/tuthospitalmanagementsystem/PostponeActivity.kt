package za.ac.tuthospitalmanagementsystem

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class PostponeActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postpone)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Postpone Appointment"
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)
        val appointmentNumber = findViewById<TextView>(R.id.appointmentNumber)
        val appointmentNo : String = intent.getStringExtra("appointmentNo").toString()
        appointmentNumber.text = appointmentNo

        val editTextDate  = findViewById<EditText>(R.id.editTextDate)

        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateMyCalender(myCalender)
        }

        editTextDate.setOnClickListener {
            DatePickerDialog(this,datePicker,myCalender.get(Calendar.YEAR),myCalender.get(Calendar.MONTH),myCalender.get(
                Calendar.DAY_OF_MONTH)).show()
        }
        buttonSubmit.setOnClickListener {
            updateAppointment(appointmentNo)
        }
    }

    private fun updateMyCalender(myCalender: Calendar) {
        val editTextDate  = findViewById<EditText>(R.id.editTextDate)
        val dFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        editTextDate.setText(dFormat.format(myCalender.time))
    }
    private fun updateAppointment(appointmentNo: String) {

        val name: String = intent.getStringExtra("name").toString()
        val surname: String= intent.getStringExtra("surname").toString()
        val number: String= intent.getStringExtra("number").toString()
        val username :String= intent.getStringExtra("username").toString()
        val date = findViewById<EditText>(R.id.editTextDate).text.toString()
        val availability = findViewById<Spinner>(R.id.spinnerAvailability).selectedItem.toString()

        database = FirebaseDatabase.getInstance().getReference("Appointment")
        val updateData = mapOf(
            "availability" to availability,
            "date" to date,
        )
        database.child(appointmentNo).updateChildren(updateData)
        Toast.makeText(this,"Updated", Toast.LENGTH_SHORT).show()
        intent = Intent(this,DoctorActivity::class.java)
        intent.putExtra("username",username)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        startActivity(intent)
    }
}