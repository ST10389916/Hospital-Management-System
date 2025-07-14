package za.ac.tuthospitalmanagementsystem

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class SetAppointmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_appointment)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Set Appointment"
        val buttonBack = findViewById<Button>(R.id.buttonBack)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)
        val editTextDate  = findViewById<EditText>(R.id.editTextDate)
        val username : String = intent.getStringExtra("userName").toString()
        val name= intent.getStringExtra("name")
        val surname= intent.getStringExtra("surname")
        val number= intent.getStringExtra("number")

        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateMyCalender(myCalender)
        }

        editTextDate.setOnClickListener {
            DatePickerDialog(this,datePicker,myCalender.get(Calendar.YEAR),myCalender.get(Calendar.MONTH),myCalender.get(Calendar.DAY_OF_MONTH)).show()
        }
        buttonSubmit.setOnClickListener {
            val randomValues = Random.nextInt(1000)
            submitAppointment(randomValues,username,name,surname,number)
        }

        buttonBack.setOnClickListener {
            goToPatientActivity(username,name,surname,number)
        }
    }

    private fun updateMyCalender(myCalender: Calendar) {
        val editTextDate  = findViewById<EditText>(R.id.editTextDate)
        val dFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        editTextDate.setText(dFormat.format(myCalender.time))
    }

    private fun submitAppointment(
        appointmentNumber: Int,
        username: String,
        name: String?,
        surname: String?,
        number: String?
    ) {
        val editTextDisease :String = findViewById<EditText>(R.id.editTextDisease).text.toString()
        val spinnerAvailability :String = findViewById<Spinner>(R.id.spinnerAvailability).selectedItem.toString()
        val editTextDate : String = findViewById<EditText>(R.id.editTextDate).text.toString()

        val database  = Firebase.database
        val myref = database.getReference("Appointment").child(appointmentNumber.toString())

        myref.setValue(Appointment(username, doctor="null",editTextDisease,spinnerAvailability,editTextDate))

        val intent = Intent(this,PatientActivity::class.java)
        intent.putExtra("userName",username)
        intent.putExtra("userName",username)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        startActivity(intent)
    }

    private fun goToPatientActivity(
        username: String,
        name: String?,
        surname: String?,
        number: String?
    ) {
        val intent = Intent(this,PatientActivity::class.java)
        intent.putExtra("userName",username)
        intent.putExtra("userName",username)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        startActivity(intent)
    }
}