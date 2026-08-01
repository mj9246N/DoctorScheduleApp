package com.example.doctorschedule

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: DoctorViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_doctors)
        progressBar = findViewById(R.id.progress_bar)
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
    }
}
