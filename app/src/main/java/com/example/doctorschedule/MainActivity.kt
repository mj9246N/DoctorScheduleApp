package com.example.doctorschedule

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: DoctorViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabRefresh: FloatingActionButton
    private lateinit var fabSpeaker: FloatingActionButton
    private lateinit var officialMessageView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_doctors)
        progressBar = findViewById(R.id.progress_bar)
        fabRefresh = findViewById(R.id.fab_refresh)
        fabSpeaker = findViewById(R.id.fab_speaker)
        officialMessageView = findViewById(R.id.tv_official_message)

        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this)[DoctorViewModel::class.java]

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.doctors.observe(this) { doctors ->
            recyclerView.adapter = DoctorAdapter(doctors)
            if (doctors.isEmpty()) {
                Toast.makeText(this, "اطلاعاتی یافت نشد", Toast.LENGTH_SHORT).show()
            }
        }

        // پیام رسمی موقت (بعد از ۳ دقیقه محو می‌شود)
        showOfficialMessage("لطفاً چند بار دکمهٔ بروزرسانی را بزنید")
        Handler(Looper.getMainLooper()).postDelayed({
            officialMessageView.visibility = View.GONE
        }, 180_000) // ۳ دقیقه

        fabRefresh.setOnClickListener {
            viewModel.loadDoctors()
            // هر بار رفرش، پیام رسمی را دوباره فعال کن (اختیاری)
            officialMessageView.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                officialMessageView.visibility = View.GONE
            }, 180_000)
        }

        fabSpeaker.setOnClickListener {
            showMessagesDialog()
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    fabRefresh.hide()
                    fabSpeaker.hide()
                } else {
                    fabRefresh.show()
                    fabSpeaker.show()
                }
            }
        })
    }

    private fun showMessagesDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_messages, null)
        val recyclerMessages = dialogView.findViewById<RecyclerView>(R.id.recycler_messages)
        val btnRefreshMessages = dialogView.findViewById<android.widget.Button>(R.id.btn_refresh_messages)

        recyclerMessages.layoutManager = LinearLayoutManager(this)
        val adapter = MessageAdapter(emptyList())
        recyclerMessages.adapter = adapter

        // بارگذاری پیام‌ها از Worker
        loadMessages(adapter)

        btnRefreshMessages.setOnClickListener {
            loadMessages(adapter)
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("بستن", null)
            .show()
    }

    private fun loadMessages(adapter: MessageAdapter) {
        MessageService.fetchMessages { messages ->
            runOnUiThread {
                adapter.updateMessages(messages)
            }
        }
    }

    private fun showOfficialMessage(text: String) {
        officialMessageView.text = text
        officialMessageView.visibility = View.VISIBLE
    }
}
