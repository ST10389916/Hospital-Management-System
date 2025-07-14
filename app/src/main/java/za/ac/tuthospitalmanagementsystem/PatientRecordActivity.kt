package za.ac.tuthospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class PatientRecordActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private lateinit var patientUsername : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_record)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        val name: String = intent.getStringExtra("name").toString()
        val surname: String= intent.getStringExtra("surname").toString()
        val number: String= intent.getStringExtra("number").toString()
        val doctorUsername: String= intent.getStringExtra("username").toString()

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Patient Record"
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)

        doSomething()

        buttonSubmit.setOnClickListener {
            saveRecord(doctorUsername,name,surname,number)
        }
    }
    private fun doSomething() {
        val actv = findViewById<AutoCompleteTextView>(R.id.auto_complete_text)
        var patients : MutableList<Doctor> = ArrayList()
        ////-----------------------------
        database = FirebaseDatabase.getInstance().getReference("Patient")
        database.get().addOnSuccessListener {
            val patients : MutableList<String> = ArrayList()
            for(i in it.children){

                val patientId = i.key.toString()

                patients.add(patientId)
            }
            val arrayAdapter = ArrayAdapter(this,R.layout.list_of_doctors,patients)

            actv.setAdapter(arrayAdapter)
            actv.setOnItemClickListener { adapterView, _, i, _ ->
                patientUsername  = adapterView.getItemAtPosition(i).toString()
            }
        }.addOnFailureListener {
            Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
        }
    }
    private fun saveRecord(doctorUsername: String, name: String, surname: String, number: String) {
        //val username = findViewById<TextInputEditText>(R.id.textInputEditTextUsername)
        val illness = findViewById<TextInputEditText>(R.id.textInputEditTextIllness)
        val medicalNo = findViewById<TextInputEditText>(R.id.textInputEditMedicalNo)
        val medication = findViewById<TextInputEditText>(R.id.textInputEditTextMedication)
        val allergy = findViewById<Spinner>(R.id.spinnerAllergy)
        val treatment = findViewById<TextInputEditText>(R.id.textInputEditTextTreatment)

        val randomValues = Random.nextInt(1000)
        //val record = "12367"
        val database  = Firebase.database
        val myref = database.getReference("PatientRecord").child(randomValues.toString())

        myref.setValue(PatientRecord(patientUsername,illness.text.toString(),medicalNo.text.toString(),medication.text.toString(),allergy.selectedItem.toString(),treatment.text.toString()))

        val intent = Intent(this,DoctorActivity::class.java)
        intent.putExtra("username",doctorUsername)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        startActivity(intent)
    }
}