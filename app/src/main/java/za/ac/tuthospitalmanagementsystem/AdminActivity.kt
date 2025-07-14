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

class AdminActivity : AppCompatActivity() {
    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var database : DatabaseReference
    private var STORAGE_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val nameTextView = findViewById<TextView>(R.id.textViewName)
        val usernameTextView = findViewById<TextView>(R.id.textViewUsername)
        val numberTextView = findViewById<TextView>(R.id.textViewUserNumber)
        val dateTextView = findViewById<TextView>(R.id.textViewTime)

        val name= intent.getStringExtra("name").toString()
        val surname= intent.getStringExtra("surname").toString()
        val number= intent.getStringExtra("number").toString()
        val username= intent.getStringExtra("userName").toString()
        val date = Date()
        dateTextView.text = (date).toString()
        nameTextView.text = "$name $surname"
        usernameTextView.text = username
        numberTextView.text = number
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Admin"
        toggle  = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_navigation_drawer,R.string.close_navigation_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val nav_view = findViewById<NavigationView>(R.id.nav_view)
        nav_view.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.doctorRegister->{
                    goToDoctorRegister()
                }
                R.id.patients->{
                    goToPatients(name,surname,number,username)
                }
                R.id.doctors->{
                    goToDoctors(name, surname, number, username)
                }
                R.id.appointment->{
                    goToAppointment(name,surname,number,username)
                }
                R.id.logout->{
                    goToLogin()
                }
                R.id.report-> {
                    saveReport()
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
        val mFilePath = "Appointment-$mFilename.pdf"
        val wordFilePath = "Appointment-$mFilename.docx"
        val excelFilePath = "Appointment-$mFilename.xls"
        val txtFilePath = "Appointment-$mFilename.txt"
        var data: String
        try{
            //word
            val para = wordDoc.createParagraph()
            val paraRun = para.createRun()
            //pdf
            PdfWriter.getInstance(mDoc, FileOutputStream(File(applicationContext.getExternalFilesDir("data"),mFilePath)))
            mDoc.open()
            //excel
            var excelNumber = 1
            val excelSheet = excelDoc.createSheet("Hospital Report")
            val excelRow = excelSheet.createRow(0)
            val excelCell0 = excelRow.createCell(0)
            excelCell0.setCellValue("Appointment Number")

            val excelCell1 = excelRow.createCell(1)
            excelCell1.setCellValue("Patient ID")

            val excelCell2 = excelRow.createCell(2)
            excelCell2.setCellValue("Doctor ID")

            val excelCell3 = excelRow.createCell(3)
            excelCell3.setCellValue("Date")

            val excelCell4 = excelRow.createCell(4)
            excelCell4.setCellValue("Availability")

            val excelCell5 = excelRow.createCell(5)
            excelCell5.setCellValue("Appointment Description")


            val sb = StringBuilder()
            database = FirebaseDatabase.getInstance().getReference("Appointment")
            database.get().addOnSuccessListener {

                for(i in it.children){



                    val availability = i.child("availability").value.toString()
                    val date = i.child("date").value.toString()
                    val disease = i.child("disease").value.toString()
                    var doctor : String = i.child("doctor").value.toString()
                    if(doctor.contentEquals("null")){
                        doctor = "Not yet assigned"
                    }
                    val patient = i.child("patient").value.toString()
                    val id = i.key.toString()

                    sb.append("Appointment number: $id\nPatient: $patient\nDoctor: $doctor\nAppointment Time: $availability\nDate: $date\nAppointment Description: $disease\n_____________________________\n")
                    val excelRow = excelSheet.createRow(excelNumber++)
                    val excelCell0 = excelRow.createCell(0)
                    excelCell0.setCellValue(id)

                    val excelCell1 = excelRow.createCell(1)
                    excelCell1.setCellValue(patient)

                    val excelCell2 = excelRow.createCell(2)
                    excelCell2.setCellValue(doctor)

                    val excelCell3 = excelRow.createCell(3)
                    excelCell3.setCellValue(date)

                    val excelCell4 = excelRow.createCell(4)
                    excelCell4.setCellValue(availability)

                    val excelCell5 = excelRow.createCell(5)
                    excelCell5.setCellValue(disease)

                    //var appointment = Appointment(patient,doctor,disease,availability,date)
                }
                data = sb.toString()

                //create pdf
                mDoc.addAuthor(name + surname)
                mDoc.add(Paragraph(data))
                mDoc.close()

                //create word
                paraRun.setText(data)
                paraRun.fontSize = 16
                wordDoc.write(FileOutputStream(File(applicationContext.getExternalFilesDir("data"),wordFilePath)))
                wordDoc.close()

                //create txt file
                val writeIntoFile = FileOutputStream(File(applicationContext.getExternalFilesDir("data"),txtFilePath))
                writeIntoFile.write(data.toByteArray())

                //create excel
                excelDoc.write(FileOutputStream(File(applicationContext.getExternalFilesDir("data"),excelFilePath)))
                excelDoc.close()
                Toast.makeText(this,"${applicationContext.getExternalFilesDir("data")} + $mFilename.pdf is successfully save",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this,"failed", Toast.LENGTH_LONG).show()
            }


        }catch (ex : Exception){
            Toast.makeText(this,ex.toString(),Toast.LENGTH_SHORT).show()
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

    private fun goToDoctors(name: String?, surname: String?, number: String?, username: String?) {
        val intent = Intent(this,AdminDoctorActivity::class.java)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        intent.putExtra("userName",username)
        startActivity(intent)
    }

    private fun goToPatients(name: String?, surname: String?, number: String?, username: String?) {
        val intent = Intent(this,AdminPatientsActivity::class.java)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        intent.putExtra("userName",username)
        startActivity(intent)
    }

    private fun goToDoctorRegister() {
        val intent = Intent(this,DoctorRegisterActivity::class.java)
        startActivity(intent)
    }

    private fun goToAppointment(
        name: String?,
        surname: String?,
        number: String?,
        username: String?
    ) {
        val intent = Intent(this,AdminAppointmentActivity::class.java)
        intent.putExtra("name",name)
        intent.putExtra("surname",surname)
        intent.putExtra("number",number)
        intent.putExtra("userName",username)
        startActivity(intent)
    }

    private fun goToLogin() {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }
}