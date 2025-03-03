package com.yogadimas.tourismapp.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.yogadimas.tourismapp.core.domain.usecase.TourismUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest

class SearchViewModel(tourismUseCase: TourismUseCase) : ViewModel() {
    val queryChannel = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResult = queryChannel
        .filter { it.trim().isNotEmpty() }
        .flatMapLatest { keyword ->
            tourismUseCase.searchTourismByName(keyword)
        }
        .asLiveData()
}
