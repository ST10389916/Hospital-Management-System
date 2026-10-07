package za.ac.hospitalmanagementsystem

data class AppointmentDTO(
    var appointmentNo: String = "",
    val patient: String? = null,
    val doctor: String? = null,
    val date: String? = null,
    val disease: String? = null,
    val availability: String? = null
)