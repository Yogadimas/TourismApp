package com.yogadimas.tourismapp.detail

import androidx.lifecycle.ViewModel
import com.yogadimas.tourismapp.core.domain.usecase.TourismUseCase
import com.yogadimas.tourismapp.core.ui.model.TourismUiModel

class DetailTourismViewModel(private val tourismUseCase: TourismUseCase) : ViewModel() {
    fun setFavoriteTourism(tourism: TourismUiModel, newStatus: Boolean) =
        tourismUseCase.setFavoriteTourism(tourism, newStatus)
}

