package com.yogadimas.tourismapp.favorite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.yogadimas.tourismapp.core.domain.usecase.TourismUseCase

class FavoriteViewModel(tourismUseCase: TourismUseCase) : ViewModel() {

    private val _getFavoriteTourism = MutableLiveData<Unit>()
    fun getFavoriteTourism() {
        _getFavoriteTourism.value = Unit
    }

    val favoriteTourism = _getFavoriteTourism.switchMap {
        tourismUseCase.getFavoriteTourism().asLiveData()
    }

}

