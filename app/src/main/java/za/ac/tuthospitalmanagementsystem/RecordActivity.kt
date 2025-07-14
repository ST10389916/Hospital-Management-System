package za.ac.tuthospitalmanagementsystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.StringBuilder

class RecordActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private lateinit var recordNo : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)


        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Record"
        val buttonEdit = findViewById<Button>(R.id.buttonEdit)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)

        val name: String = intent.getStringExtra("name").toString()
        val surname: String= intent.getStringExtra("surname").toString()
        val number: String= intent.getStringExtra("number").toString()
        val username: String= intent.getStringExtra("username").toString()

        viewAllRecords()
        buttonEdit.setOnClickListener {
            goToEdit(username,name,surname,number)
        }
        buttonDelete.setOnClickListener {
            deleteRecord(username,name,surname,number)
        }
        viewAllRecords()
    }


    private fun deleteRecord(username: String, name: String, surname: String, number: String) {
        recordNo = findViewById<EditText>(R.id.editTextRecordNo).text.toString()
        database = FirebaseDatabase.getInstance().getReference("PatientRecord").child(recordNo)
        database.get().addOnSuccessListener {
            if(it.exists()){
                database.removeValue().addOnSuccessListener {
                    Toast.makeText(this,"Record removed",Toast.LENGTH_LONG).show()
                }
                val intent = Intent(this,DoctorActivity::class.java)
                intent.putExtra("recordNo",recordNo)
                intent.putExtra("username",username)
                intent.putExtra("name",name)
                intent.putExtra("surname",surname)
                intent.putExtra("number",number)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Patient does not exist",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun viewAllRecords() {
        val record = findViewById<TextView>(R.id.textViewRecord)
        database = FirebaseDatabase.getInstance().getReference("PatientRecord")
        database.get().addOnSuccessListener {
            val sb = StringBuilder()
            for(i in it.children){

                val username = i.child("username").value
                val illness = i.child("illness").value
                val allergy = i.child("allergy").value
                val treatment = i.child("treatment").value
                val id = i.key

                sb.append("Record No: $id\nUsername: $username\nIllness: $illness\nAllergy: $allergy\nTreatment: $treatment\n_____________________________\n")
            }
            record.text = sb
        }.addOnFailureListener {
            Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
        }
    }

    private fun goToEdit(username: String, name: String, surname: String, number: String) {
        recordNo = findViewById<EditText>(R.id.editTextRecordNo).text.toString()
        database = FirebaseDatabase.getInstance().getReference("PatientRecord").child(recordNo)
        database.get().addOnSuccessListener {
            if(it.exists()){
                val intent = Intent(this,EditRecordActivity::class.java)
                intent.putExtra("recordNo",recordNo)
                intent.putExtra("username",username)
                intent.putExtra("name",name)
                intent.putExtra("surname",surname)
                intent.putExtra("number",number)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Patient does not exist",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
        }
    }
}