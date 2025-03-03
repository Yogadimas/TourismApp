package com.yogadimas.tourismapp.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.yogadimas.tourismapp.core.domain.usecase.TourismUseCase

class HomeViewModel(tourismUseCase: TourismUseCase) : ViewModel() {

    private val _getTourism = MutableLiveData<Unit>()
    fun getTourism() {
        _getTourism.value = Unit
    }

    val tourism = _getTourism.switchMap {
        tourismUseCase.getAllTourism().asLiveData()
    }
}
