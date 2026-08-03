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

class DoctorAdapter(
    private val todayDoctors: List<Doctor>,
    private val allDoctors: List<Doctor>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_TODAY_HEADER = 1
        private const val TYPE_DOCTOR = 2
        private const val TYPE_ALL_HEADER = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> TYPE_DATE_HEADER
            position == 1 -> TYPE_TODAY_HEADER
            position < 2 + todayDoctors.size -> TYPE_DOCTOR
            position == 2 + todayDoctors.size -> TYPE_ALL_HEADER
            else -> TYPE_DOCTOR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_header_date, parent, false)
                DateHeaderHolder(view)
            }
            TYPE_TODAY_HEADER, TYPE_ALL_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_section_header, parent, false)
                SectionHeaderHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_doctor, parent, false)
                DoctorHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DateHeaderHolder -> {
                // مقداردهی در Activity انجام می‌شود
            }
            is SectionHeaderHolder -> {
                if (position == 1) holder.bind("امروز")
                else holder.bind("همه پزشکان")
            }
            is DoctorHolder -> {
                val doctor = if (position < 2 + todayDoctors.size) {
                    todayDoctors[position - 2]
                } else {
                    allDoctors[position - (3 + todayDoctors.size)]
                }
                holder.bind(doctor)
            }
        }
    }

    override fun getItemCount(): Int {
        return 2 + todayDoctors.size + 1 + allDoctors.size
    }

    fun updateDateHeader(dateRange: DoctorParser.DateRange) {
        // در صورت نیاز
    }

    class DateHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDateRange: TextView = itemView.findViewById(R.id.tv_date_range)
    }

    class SectionHeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tv_section_title)
        fun bind(title: String) {
            tvTitle.text = title
        }
    }

    class DoctorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clinicTv: TextView = itemView.findViewById(R.id.tv_clinic)
        val nameTv: TextView = itemView.findViewById(R.id.tv_doctor_name)
        val specialtyTv: TextView = itemView.findViewById(R.id.tv_doctor_specialty)
        val schedulesContainer: ViewGroup = itemView.findViewById(R.id.schedules_container)

        fun bind(doctor: Doctor) {
            clinicTv.text = doctor.clinic
            nameTv.text = doctor.name
            specialtyTv.text = doctor.specialty
            schedulesContainer.removeAllViews()
            for (schedule in doctor.schedules) {
                val scheduleView = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.item_schedule, schedulesContainer, false)
                val text = "${schedule.day} (${schedule.time})"
                scheduleView.findViewById<TextView>(R.id.tv_schedule_text).text = text
                scheduleView.setOnClickListener {
                    fetchAndShowTurnInfo(itemView.context, schedule)
                }
                schedulesContainer.addView(scheduleView)
            }
        }

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
                        showTurnInfoDialog(context, schedule, info)
                    } else {
                        AlertDialog.Builder(context)
                            .setMessage("خطا در دریافت اطلاعات")
                            .setPositiveButton("بستن", null)
                            .show()
                    }
                }
            }
        }

        private fun showTurnInfoDialog(context: android.content.Context, schedule: Schedule, info: TurnInfoService.TurnInfo) {
            val dialogView = LayoutInflater.from(context)
                .inflate(R.layout.dialog_turn_info, null)
            val titleView: TextView = dialogView.findViewById(R.id.tv_turn_title)
            val appointValue: TextView = dialogView.findViewById(R.id.tv_appoint_value)
            val telValue: TextView = dialogView.findViewById(R.id.tv_tel_value)
            val webValue: TextView = dialogView.findViewById(R.id.tv_web_value)
            titleView.text = "${schedule.day}، ${schedule.time}"
            appointValue.text = info.appoint.toString()
            telValue.text = info.telAppoint.toString()
            webValue.text = info.webAppoint.toString()
            AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("بسیار خُب") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}
