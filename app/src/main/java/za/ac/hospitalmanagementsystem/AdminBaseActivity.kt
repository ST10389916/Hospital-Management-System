package za.ac.hospitalmanagementsystem.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import za.ac.hospitalmanagementsystem.*

abstract class AdminBaseActivity : AppCompatActivity() {
    protected lateinit var username: String
    protected lateinit var name: String
    protected lateinit var surname: String
    protected lateinit var number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = intent.getStringExtra("username").orEmpty()
        name = intent.getStringExtra("name").orEmpty()
        surname = intent.getStringExtra("surname").orEmpty()
        number = intent.getStringExtra("number").orEmpty()
    }

    protected fun setupBottomNavigation(selectedItemId: Int) {
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
        bottomNav?.selectedItemId = selectedItemId

        bottomNav?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_doctors -> {
                    if (this.javaClass != AdminDoctorActivity::class.java) {
                        navigateTo(AdminDoctorActivity::class.java)
                    }
                    true
                }
                R.id.nav_patients -> {
                    if (this.javaClass != AdminPatientsActivity::class.java) {
                        navigateTo(AdminPatientsActivity::class.java)
                    }
                    true
                }
                R.id.nav_appointments -> {
                    if (this.javaClass != AdminAppointmentActivity::class.java) {
                        navigateTo(AdminAppointmentActivity::class.java)
                    }
                    true
                }
                R.id.nav_ai_support -> {
                    if (this.javaClass != AdminAISupportActivity::class.java) {
                        navigateTo(AdminAISupportActivity::class.java)
                    }
                    true
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                    true
                }
                else -> false
            }
        }
    }

    private fun navigateTo(targetActivity: Class<*>) {
        startActivity(Intent(this, targetActivity).apply {
            putExtra("username", username)
            putExtra("name", name)
            putExtra("surname", surname)
            putExtra("number", number)
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        })
        overridePendingTransition(0, 0)
    }
}