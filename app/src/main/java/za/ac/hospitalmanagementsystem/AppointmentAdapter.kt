package za.ac.hospitalmanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppointmentAdapter(
    private val appointments: List<AppointmentDTO>,  // Changed here
    private val onPostponeClick: (String) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAppointmentNumber: TextView = itemView.findViewById(R.id.tvAppointmentNumber)
        val tvPatientId: TextView = itemView.findViewById(R.id.tvPatientId)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvDisease: TextView = itemView.findViewById(R.id.tvDisease)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val btnPostpone: Button = itemView.findViewById(R.id.btnPostpone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        holder.tvAppointmentNumber.text = "Appointment #${appointment.appointmentNo}"
        holder.tvPatientId.text = "Patient ID: ${appointment.patient}"
        holder.tvDate.text = "Date: ${appointment.date}"
        holder.tvDisease.text = "Disease: ${appointment.disease}"
        holder.tvTime.text = "Time: ${appointment.availability}"

        holder.btnPostpone.setOnClickListener {
            appointment.appointmentNo.let { appointmentNo ->
                onPostponeClick(appointmentNo)
            }
        }
    }

    override fun getItemCount(): Int = appointments.size
}