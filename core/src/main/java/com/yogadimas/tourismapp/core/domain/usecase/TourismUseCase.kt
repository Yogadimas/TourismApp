package com.yogadimas.tourismapp.core.domain.usecase

import com.yogadimas.tourismapp.core.data.Resource
import com.yogadimas.tourismapp.core.ui.model.TourismUiModel
import kotlinx.coroutines.flow.Flow

interface TourismUseCase {
    fun getAllTourism(): Flow<Resource<List<TourismUiModel>>>
    fun getFavoriteTourism(): Flow<List<TourismUiModel>>
    fun searchTourismByName(keyword: String): Flow<List<TourismUiModel>>
    fun setFavoriteTourism(tourism: TourismUiModel, state: Boolean)
}