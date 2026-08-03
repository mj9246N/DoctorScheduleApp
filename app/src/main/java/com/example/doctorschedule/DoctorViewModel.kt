package com.example.doctorschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {
    private val _allDoctors = MutableLiveData<List<Doctor>>()
    val allDoctors: LiveData<List<Doctor>> = _allDoctors

    private val _todayDoctors = MutableLiveData<List<Doctor>>()
    val todayDoctors: LiveData<List<Doctor>> = _todayDoctors

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doctors = DoctorRepository.getAllDoctors()
                _allDoctors.value = doctors

                val todayStr = PersianDateUtil.getTodayShamsi()
                val todayList = doctors.mapNotNull { doctor ->
                    val todaySchedules = doctor.schedules.filter { it.day == todayStr }
                    if (todaySchedules.isNotEmpty()) doctor.copy(schedules = todaySchedules) else null
                }
                _todayDoctors.value = todayList
            } catch (e: Exception) {
                _allDoctors.value = emptyList()
                _todayDoctors.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
