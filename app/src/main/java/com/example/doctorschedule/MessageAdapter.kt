package com.example.doctorschedule

import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private var messages: List<String>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tv_message_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvMessage.text = messages[position]
        // فعال‌سازی لینک‌های خودکار (URL، شماره تلفن و...)
        Linkify.addLinks(holder.tvMessage, Linkify.ALL)
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<String>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
