package com.example.doctorschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DoctorViewModel : ViewModel() {
    private val _doctors = MutableLiveData<List<Doctor>>()
    val doctors: LiveData<List<Doctor>> = _doctors

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init { loadDoctors() }

    fun loadDoctors() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _doctors.value = DoctorRepository.getAllDoctors()
            } catch (e: Exception) {
                _doctors.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
