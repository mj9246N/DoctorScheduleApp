package com.example.doctorschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {
    private val _doctors = MutableLiveData<List<Doctor>>()
    val doctors: LiveData<List<Doctor>> = _doctors

    private val _todayDoctors = MutableLiveData<List<Doctor>>()
    val todayDoctors: LiveData<List<Doctor>> = _todayDoctors

    private val _dateRange = MutableLiveData<DoctorParser.DateRange>()
    val dateRange: LiveData<DoctorParser.DateRange> = _dateRange

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val doctors = DoctorRepository.getAllDoctors()
                val todayStr = PersianDateUtil.getTodayShamsi()
                // فیلتر پزشکانی که حداقل یک نوبت امروز دارند
                val todayList = doctors.mapNotNull { doctor ->
                    val todaySchedules = doctor.schedules.filter { it.day == todayStr }
                    if (todaySchedules.isNotEmpty()) doctor.copy(schedules = todaySchedules) else null
                }
                _doctors.value = doctors
                _todayDoctors.value = todayList
                _dateRange.value = DoctorRepository.getDateRange()
            } catch (e: Exception) {
                _doctors.value = emptyList()
                _todayDoctors.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
