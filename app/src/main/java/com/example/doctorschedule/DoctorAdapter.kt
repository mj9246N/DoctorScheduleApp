package com.example.doctorschedule

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DoctorAdapter(private val doctorList: List<Doctor>) :
    RecyclerView.Adapter<DoctorAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clinicTv: TextView = itemView.findViewById(R.id.tv_clinic)
        val nameTv: TextView = itemView.findViewById(R.id.tv_doctor_name)
        val specialtyTv: TextView = itemView.findViewById(R.id.tv_doctor_specialty)
        val schedulesContainer: ViewGroup = itemView.findViewById(R.id.schedules_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.clinicTv.text = doctor.clinic
        holder.nameTv.text = doctor.name
        holder.specialtyTv.text = doctor.specialty

        holder.schedulesContainer.removeAllViews()

        for (schedule in doctor.schedules) {
            val scheduleView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_schedule, holder.schedulesContainer, false)
            val text = "${schedule.day} (${schedule.time})"
            scheduleView.findViewById<TextView>(R.id.tv_schedule_text).text = text

            scheduleView.setOnClickListener {
                fetchAndShowTurnInfo(holder.itemView.context, schedule)
            }
            holder.schedulesContainer.addView(scheduleView)
        }
    }

    override fun getItemCount(): Int = doctorList.size

    private fun fetchAndShowTurnInfo(context: android.content.Context, schedule: Schedule) {
        val progressDialog = AlertDialog.Builder(context)
            .setMessage("در حال دریافت اطلاعات...")
            .setCancelable(false)
            .create()
        progressDialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            val info = TurnInfoService.getTurnInfo(schedule.showId)
            withContext(Dispatchers.Main) {
                progressDialog.dismiss()
                if (info != null) {
                    val message = "روز: ${schedule.day}\nساعت: ${schedule.time}\n\n" +
                            "حضوری: ${info.appoint}\n" +
                            "تلفنی: ${info.telAppoint}\n" +
                            "اینترنتی: ${info.webAppoint}"
                    AlertDialog.Builder(context)
                        .setTitle("جزئیات نوبت")
                        .setMessage(message)
                        .setPositiveButton("بسیار خُب") { dialog, _ -> dialog.dismiss() }
                        .show()
                } else {
                    AlertDialog.Builder(context)
                        .setMessage("خطا در دریافت اطلاعات")
                        .setPositiveButton("بستن", null)
                        .show()
                }
            }
        }
    }
}
