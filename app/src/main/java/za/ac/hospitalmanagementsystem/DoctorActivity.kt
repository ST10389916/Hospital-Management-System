package za.ac.hospitalmanagementsystem
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class DoctorActivity : DoctorBaseActivity() {
    private lateinit var database: DatabaseReference
    private var STORAGE_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = " "

        // Initialize views
        val nameTextView = findViewById<TextView>(R.id.textViewName)
        val usernameTextView = findViewById<TextView>(R.id.textViewUsername)
        val numberTextView = findViewById<TextView>(R.id.textViewUserNumber)
        val dateTextView = findViewById<TextView>(R.id.textViewTime)

        // Set UI data
        nameTextView.text = "$name $surname"
        usernameTextView.text = username
        numberTextView.text = number
        dateTextView.text = Date().toString()

        // Setup bottom navigation
        setupBottomNavigation(R.id.nav_home)
    }

    override fun onResume() {
        super.onResume()
        highlightTab(R.id.nav_home)
    }

    private fun saveReport() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, STORAGE_CODE)
            } else {
                savePDF()
            }
        } else {
            savePDF()
        }
    }

    private fun savePDF() {
        val mDoc = Document()
        val wordDoc = XWPFDocument()
        val excelDoc = HSSFWorkbook()
        val mFilename = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(System.currentTimeMillis())
        val mFilePath = "Record-$mFilename.pdf"

        try {
            PdfWriter.getInstance(mDoc, FileOutputStream(
                File(applicationContext.getExternalFilesDir("data"), mFilePath)))
            mDoc.open()

            database = FirebaseDatabase.getInstance().getReference("PatientRecord")
            database.get().addOnSuccessListener {
                val sb = StringBuilder()
                // ... (rest of your PDF generation code remains the same)

                mDoc.close()
                Toast.makeText(this, "Report saved successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to generate report", Toast.LENGTH_LONG).show()
            }
        } catch (ex: Exception) {
            Toast.makeText(this, "Error: ${ex.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            savePDF()
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
        }
    }
}