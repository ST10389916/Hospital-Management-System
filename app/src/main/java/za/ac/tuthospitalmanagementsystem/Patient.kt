package za.ac.tuthospitalmanagementsystem

data class Patient(val patientId: String? = null,val name : String? = null,val surname : String? = null,val phoneNumber : String? =null,var address : String? = null,val gender : String? = null,val password : String? = null,val role : String? = "patient")
