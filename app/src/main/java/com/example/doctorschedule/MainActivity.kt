package com.example.doctorschedule

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Button
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
    private lateinit var btnAll: Button
    private lateinit var btnToday: Button

    private var currentTab: String = "all" // or "today"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_doctors)
        progressBar = findViewById(R.id.progress_bar)
        fabRefresh = findViewById(R.id.fab_refresh)
        fabSpeaker = findViewById(R.id.fab_speaker)
        btnAll = findViewById(R.id.btn_all)
        btnToday = findViewById(R.id.btn_today)

        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this)[DoctorViewModel::class.java]

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.allDoctors.observe(this) { all ->
            if (currentTab == "all") {
                recyclerView.adapter = DoctorAdapter(all)
            }
        }

        viewModel.todayDoctors.observe(this) { today ->
            if (currentTab == "today") {
                recyclerView.adapter = DoctorAdapter(today)
            }
        }

        btnAll.setOnClickListener {
            currentTab = "all"
            viewModel.allDoctors.value?.let {
                recyclerView.adapter = DoctorAdapter(it)
            }
        }

        btnToday.setOnClickListener {
            currentTab = "today"
            viewModel.todayDoctors.value?.let {
                recyclerView.adapter = DoctorAdapter(it)
            }
        }

        fabRefresh.setOnClickListener {
            viewModel.loadAll()
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
        val btnRefreshMessages = dialogView.findViewById<Button>(R.id.btn_refresh_messages)
        recyclerMessages.layoutManager = LinearLayoutManager(this)
        recyclerMessages.adapter = MessageAdapter(emptyList())
        btnRefreshMessages.setOnClickListener {
            Toast.makeText(this, "در حال بروزرسانی پیام‌ها...", Toast.LENGTH_SHORT).show()
        }
        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("بستن", null)
            .show()
    }
}
