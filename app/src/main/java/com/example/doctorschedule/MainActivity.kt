package com.example.doctorschedule

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_doctors)
        progressBar = findViewById(R.id.progress_bar)
        fabRefresh = findViewById(R.id.fab_refresh)
        fabSpeaker = findViewById(R.id.fab_speaker)

        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this)[DoctorViewModel::class.java]

        val adapter = DoctorAdapter(emptyList(), emptyList())
        recyclerView.adapter = adapter

        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.dateRange.observe(this) { range ->
            // آپدیت هدر تاریخ (اولین آیتم)
            val dateHeaderHolder = (recyclerView.findViewHolderForAdapterPosition(0)
                as? DoctorAdapter.DateHeaderHolder)
            dateHeaderHolder?.tvDateRange?.text = "از ${range.start} تا ${range.end}"
        }

        viewModel.todayDoctors.observe(this) { today ->
            viewModel.doctors.observe(this) { all ->
                val newAdapter = DoctorAdapter(today, all)
                recyclerView.adapter = newAdapter
                // دوباره dateRange را ست کن
                viewModel.dateRange.observe(this) { range ->
                    val dateHeaderHolder = (recyclerView.findViewHolderForAdapterPosition(0)
                        as? DoctorAdapter.DateHeaderHolder)
                    dateHeaderHolder?.tvDateRange?.text = "از ${range.start} تا ${range.end}"
                }
            }
        }

        // انیمیشن تپش برای دکمه بلندگو
        val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse)
        fabSpeaker.startAnimation(pulse)

        // مخفی‌سازی دکمه‌ها هنگام اسکرول به انتها
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val cannotScrollDown = !recyclerView.canScrollVertically(1)
                if (cannotScrollDown) {
                    fabRefresh.hide()
                    fabSpeaker.hide()
                } else {
                    fabRefresh.show()
                    fabSpeaker.show()
                }
            }
        })

        fabRefresh.setOnClickListener {
            viewModel.loadAll()
        }

        fabSpeaker.setOnClickListener {
            showMessagesDialog()
        }
    }

    private fun showMessagesDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_messages, null)
        val recyclerMessages = dialogView.findViewById<RecyclerView>(R.id.recycler_messages)
        val btnRefreshMessages = dialogView.findViewById<android.widget.Button>(R.id.btn_refresh_messages)

        // در آینده می‌توانید اینجا پیام‌ها را از Cloudflare Worker بگیرید
        recyclerMessages.layoutManager = LinearLayoutManager(this)
        recyclerMessages.adapter = MessageAdapter(emptyList())

        btnRefreshMessages.setOnClickListener {
            // درخواست به سرور پیام‌ها
            Toast.makeText(this, "در حال بروزرسانی پیام‌ها...", Toast.LENGTH_SHORT).show()
        }

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("بستن", null)
            .show()
    }
}
