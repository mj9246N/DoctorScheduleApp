package com.example.doctorschedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DoctorAdapter(private val doctorList: List<Doctor>) :
    RecyclerView.Adapter<DoctorAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById(R.id.tv_doctor_name)
        val specialtyTv: TextView = itemView.findViewById(R.id.tv_doctor_specialty)
        val dayTv: TextView = itemView.findViewById(R.id.tv_doctor_day)
        val timeTv: TextView = itemView.findViewById(R.id.tv_doctor_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.nameTv.text = doctor.name
        holder.specialtyTv.text = doctor.specialty
        holder.dayTv.text = doctor.day
        holder.timeTv.text = doctor.time
    }

    override fun getItemCount() = doctorList.size
    }
