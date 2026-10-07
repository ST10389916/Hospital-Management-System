package za.ac.hospitalmanagementsystem

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import za.ac.hospitalmanagementsystem.admin.AdminAISupportActivity
import java.text.SimpleDateFormat
import java.util.*

class AdminActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private var STORAGE_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Initialize TextViews
        val nameTextView = findViewById<TextView>(R.id.textViewName)
        val usernameTextView = findViewById<TextView>(R.id.textViewUsername)
        val numberTextView = findViewById<TextView>(R.id.textViewUserNumber)
        val dateTextView = findViewById<TextView>(R.id.textViewTime)

        // Get intent extras
        val name = intent.getStringExtra("name").toString()
        val surname = intent.getStringExtra("surname").toString()
        val number = intent.getStringExtra("number").toString()
        val username = intent.getStringExtra("username").toString()
        val date = Date()

        // Set text to TextViews
        dateTextView.text = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(date)
        nameTextView.text = "$name $surname"
        usernameTextView.text = "Username: $username"
        numberTextView.text = "Phone: $number"

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Admin Dashboard"

        // Initialize BottomNavigationView
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_doctors -> {
                    goToDoctors(name, surname, number, username)
                    true
                }
                R.id.nav_patients -> {
                    goToPatients(name, surname, number, username)
                    true
                }
                R.id.nav_appointments -> {
                    goToAppointment(name, surname, number, username)
                    true
                }
                R.id.nav_ai_support -> {
                    goToAISupport(name, surname, number, username)
                    true
                }
                R.id.nav_logout -> {
                    goToLogin()
                    true
                }
                else -> false
            }
        }

    }

    private fun goToAISupport(name: String?, surname: String?, number: String?, username: String?) {
        val intent = Intent(this, AdminAISupportActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("number", number)
        intent.putExtra("username", username)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            STORAGE_CODE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //savePDF()
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToDoctors(name: String?, surname: String?, number: String?, username: String?) {
        val intent = Intent(this, AdminDoctorActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("number", number)
        intent.putExtra("userName", username)
        startActivity(intent)
    }

    private fun goToPatients(name: String?, surname: String?, number: String?, username: String?) {
        val intent = Intent(this, AdminPatientsActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("number", number)
        intent.putExtra("userName", username)
        startActivity(intent)
    }

    private fun goToDoctorRegister() {
        val intent = Intent(this, DoctorRegisterActivity::class.java)
        startActivity(intent)
    }

    private fun goToAppointment(name: String?, surname: String?, number: String?, username: String?) {
        val intent = Intent(this, AdminAppointmentActivity::class.java)
        intent.putExtra("name", name)
        intent.putExtra("surname", surname)
        intent.putExtra("number", number)
        intent.putExtra("userName", username)
        startActivity(intent)
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}