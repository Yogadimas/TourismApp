package com.yogadimas.tourismapp.core.domain.repository

import com.yogadimas.tourismapp.core.data.Resource
import com.yogadimas.tourismapp.core.domain.model.Tourism
import kotlinx.coroutines.flow.Flow

interface ITourismRepository {
    fun getAllTourism(): Flow<Resource<List<Tourism>>>
    fun getFavoriteTourism(): Flow<List<Tourism>>
    fun searchTourismByName(keyword: String): Flow<List<Tourism>>
    fun setFavoriteTourism(tourism: Tourism, state: Boolean)
}