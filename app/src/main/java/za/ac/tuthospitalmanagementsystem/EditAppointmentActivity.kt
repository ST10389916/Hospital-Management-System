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
import kotlin.collections.ArrayList

class EditAppointmentActivity : AppCompatActivity() {

    private lateinit var database : DatabaseReference
    private lateinit var doc:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_appointment)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Update Appointment"
        val appointmentNo: String = intent.getStringExtra("appointmentNo").toString()
        val date = findViewById<EditText>(R.id.editTextDate)
        doSomething()

        val myCalender = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR,year)
            myCalender.set(Calendar.MONTH,month)
            myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateMyCalender(myCalender)
        }

        date.setOnClickListener {
            DatePickerDialog(this,datePicker,myCalender.get(Calendar.YEAR),myCalender.get(Calendar.MONTH),myCalender.get(
                Calendar.DAY_OF_MONTH)).show()
        }

        val name = intent.getStringExtra("name").toString()
        val surname= intent.getStringExtra("surname").toString()
        val number= intent.getStringExtra("number").toString()
        val username= intent.getStringExtra("userName").toString()

        val appointmentNumber = findViewById<TextView>(R.id.appointmentNumber)
        appointmentNumber.text = appointmentNo

        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            updateAppointment(appointmentNo,name,surname,number,username)
        }
    }

    private fun doSomething() {
        val actv = findViewById<AutoCompleteTextView>(R.id.auto_complete_text)
        var doctors : MutableList<Doctor> = ArrayList()
        ////-----------------------------
        database = FirebaseDatabase.getInstance().getReference("Doctor")
        database.get().addOnSuccessListener {
            val doctors : MutableList<String> = ArrayList()
            for(i in it.children){

                val id = i.key.toString()

                doctors.add(id)
            }
            val arrayAdapter = ArrayAdapter(this,R.layout.list_of_doctors,doctors)

            actv.setAdapter(arrayAdapter)
            actv.setOnItemClickListener { adapterView, _, i, _ ->
                doc  = adapterView.getItemAtPosition(i).toString()
                Toast.makeText(this,"You Appointed $doc",Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateMyCalender(myCalender: Calendar) {
        val date = findViewById<EditText>(R.id.editTextDate)
        val dFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        date.setText(dFormat.format(myCalender.time))
    }

    private fun updateAppointment(appointmentNo: String,name: String,surname: String,number: String,username: String) {

        val availability = findViewById<Spinner>(R.id.spinnerAvailability).selectedItem.toString()
        val date = findViewById<EditText>(R.id.editTextDate).text.toString()

        database = FirebaseDatabase.getInstance().getReference("Appointment")
        val updateData = mapOf(
            "availability" to availability,
            "date" to date,
            "doctor" to doc
        )
        database.child(appointmentNo).updateChildren(updateData)
        Toast.makeText(this,"Updated",Toast.LENGTH_SHORT).show()
        intent = Intent(this,AdminActivity::class.java)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        intent.putExtra("userName",username)
        startActivity(intent)
    }
}