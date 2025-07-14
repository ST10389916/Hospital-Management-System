package za.ac.tuthospitalmanagementsystem

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*

class DoctorActivity : AppCompatActivity() {
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var database : DatabaseReference
    private var STORAGE_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Doctor"
        val nameTextView = findViewById<TextView>(R.id.textViewName)
        val usernameTextView = findViewById<TextView>(R.id.textViewUsername)
        val numberTextView = findViewById<TextView>(R.id.textViewUserNumber)
        val dateTextView = findViewById<TextView>(R.id.textViewTime)

        val date = Date()
        dateTextView.text = (date).toString()

        val name: String = intent.getStringExtra("name").toString()
        val surname: String= intent.getStringExtra("surname").toString()
        val number: String= intent.getStringExtra("number").toString()
        val username: String= intent.getStringExtra("username").toString()

        "$name $surname".also { nameTextView.text = it }
        usernameTextView.text = username
        numberTextView.text = number

        toggle  = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_navigation_drawer,R.string.close_navigation_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val nav_view = findViewById<NavigationView>(R.id.nav_view)
        nav_view.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.appointment->{
                    goToAppointment(username,name,surname,number)
                }
                R.id.patientRecord->{
                    goToPatientRecord(username,name,surname,number)
                }
                R.id.patientViewRecord->{
                    goToView(username,name,surname,number)
                }
                R.id.report->{
                    saveReport()
                }
                R.id.logout->{
                    goToLogin()
                }
            }
            true
        }
    }

    private fun saveReport() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission,STORAGE_CODE)
            }else{
                savePDF()
            }
        }else{
            savePDF()
        }
    }

    private fun savePDF() {
        val name= intent.getStringExtra("name")
        val surname= intent.getStringExtra("surname")
        val mDoc  = Document()
        val wordDoc = XWPFDocument()
        val excelDoc = HSSFWorkbook()
        val mFilename = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        val mFilePath = "Record-$mFilename.pdf"
        val wordFilePath = "Record-$mFilename.docx"
        val excelFilePath = "Record-$mFilename.xls"
        val txtFilePath = "Record-$mFilename.txt"
        var data: String
        try{
            //word
            val para = wordDoc.createParagraph()
            val paraRun = para.createRun()
            var wordCount = 1
            //pdf
            PdfWriter.getInstance(mDoc, FileOutputStream(File(applicationContext.getExternalFilesDir("data"),mFilePath)))
            mDoc.open()
            //excel
            var excelNumber = 1
            val excelSheet = excelDoc.createSheet("Doctor Report")
            val excelRow = excelSheet.createRow(0)
            val excelCell0 = excelRow.createCell(0)
            excelCell0.setCellValue("Record Number")

            val excelCell1 = excelRow.createCell(1)
            excelCell1.setCellValue("Patient ID")

            val excelCell2 = excelRow.createCell(2)
            excelCell2.setCellValue("Disease")

            val excelCell3 = excelRow.createCell(3)
            excelCell3.setCellValue("allergy")

            val excelCell4 = excelRow.createCell(4)
            excelCell4.setCellValue("treatment")

            val excelCell5 = excelRow.createCell(5)
            excelCell5.setCellValue("medical number")

            val excelCell6 = excelRow.createCell(6)
            excelCell6.setCellValue("medication")

            paraRun.setText("Patients' Records ",0)
            val sb = StringBuilder()
            database = FirebaseDatabase.getInstance().getReference("PatientRecord")
            database.get().addOnSuccessListener {
                val sb = StringBuilder()
                for(i in it.children){

                    val username = i.child("username").value.toString()
                    val illness = i.child("illness").value.toString()
                    val allergy = i.child("allergy").value.toString()
                    val treatment = i.child("treatment").value.toString()
                    val medication = i.child("medication").value.toString()
                    val medicalNo = i.child("medicalNo").value.toString()
                    val id = i.key.toString()

                    sb.append("Record No: $id\nUsername: $username\nIllness: $illness\nAllergy: $allergy\nTreatment: $treatment\nmedicalNo: $medicalNo\nmedication: $medication\n_____________________________\n")

                    paraRun.fontSize = 10
                    paraRun.setText("Record No: $id",wordCount++)
                    paraRun.setText("Username: $username",wordCount++)
                    paraRun.setText("Illness: $illness",wordCount++)
                    paraRun.setText("Allergy: $allergy",wordCount++)
                    paraRun.setText("Treatment: $treatment",wordCount++)
                    paraRun.setText("medicalNo: $medicalNo",wordCount++)
                    paraRun.setText("medication: $medication",wordCount++)
                    paraRun.setText("\n_____________________________",wordCount++)

                    val excelRow = excelSheet.createRow(excelNumber++)
                    val excelCell0 = excelRow.createCell(0)
                    excelCell0.setCellValue(id)

                    val excelCell1 = excelRow.createCell(1)
                    excelCell1.setCellValue(username)

                    val excelCell2 = excelRow.createCell(2)
                    excelCell2.setCellValue(illness)

                    val excelCell3 = excelRow.createCell(3)
                    excelCell3.setCellValue(allergy)

                    val excelCell4 = excelRow.createCell(4)
                    excelCell4.setCellValue(treatment)

                    val excelCell5 = excelRow.createCell(5)
                    excelCell5.setCellValue(medicalNo)

                    val excelCell6 = excelRow.createCell(6)
                    excelCell6.setCellValue(medication)
                }
                data = sb.toString()

                //create pdf
                mDoc.addAuthor(name + surname)
                mDoc.add(Paragraph("Patients' Records \n\n$data"))
                mDoc.close()

                //create word


                wordDoc.write(FileOutputStream(File(applicationContext.getExternalFilesDir("data"),wordFilePath)))
                wordDoc.close()

                //create txt file
                val writeIntoFile = FileOutputStream(File(applicationContext.getExternalFilesDir("data"),txtFilePath))
                writeIntoFile.write("Patients' Records \n\n$data".toByteArray())

                //create excel
                excelDoc.write(FileOutputStream(File(applicationContext.getExternalFilesDir("data"),excelFilePath)))
                excelDoc.close()
                Toast.makeText(this,"${applicationContext.getExternalFilesDir("data")} + $mFilename.pdf is successfully save",
                    Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
            }


        }catch (ex : Exception){
            Toast.makeText(this,ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            STORAGE_CODE -> {
                if(grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    savePDF()
                }else{
                    Toast.makeText(this,"permission denied!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToView(username: String, name: String, surname: String, number: String) {
        val intent = Intent(this,RecordActivity::class.java)
        intent.putExtra("username",username)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        startActivity(intent)
    }

    private fun goToPatientRecord(username: String, name: String, surname: String, number: String) {
        val intent = Intent(this,PatientRecordActivity::class.java)
        intent.putExtra("username",username)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        startActivity(intent)
    }
    private fun goToAppointment(username: String, name: String, surname: String, number: String) {
        val intent = Intent(this,DoctorAppointmentsActivity::class.java)
        intent.putExtra("username",username)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        startActivity(intent)
    }

    private fun goToLogin() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }
}