package com.yogadimas.tourismapp.core.domain.usecase

import com.yogadimas.tourismapp.core.data.Resource
import com.yogadimas.tourismapp.core.domain.repository.ITourismRepository
import com.yogadimas.tourismapp.core.ui.model.TourismUiModel
import com.yogadimas.tourismapp.core.utils.DataMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map


class TourismInteractor(private val tourismRepository: ITourismRepository) : TourismUseCase {

    override fun getAllTourism() =
        tourismRepository.getAllTourism().map { resource ->
            when (resource) {
                is Resource.Success -> {
                    Resource.Success(
                        DataMapper.mapDomainsToUiModel(
                            resource.data ?: emptyList()
                        )
                    )
                }

                is Resource.Loading -> Resource.Loading()
                is Resource.Error -> Resource.Error(resource.message)
            }
        }


    override fun getFavoriteTourism() =
        tourismRepository.getFavoriteTourism().map { DataMapper.mapDomainsToUiModel(it) }


    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun searchTourismByName(keyword: String): Flow<List<TourismUiModel>> {
        return flowOf(keyword)
            .debounce(300)
            .distinctUntilChanged()
            .filter { it.trim().isNotEmpty() }
            .flatMapLatest {
                tourismRepository.searchTourismByName(it).map { DataMapper.mapDomainsToUiModel(it) }
            }
    }

    override fun setFavoriteTourism(tourism: TourismUiModel, state: Boolean) {
        val tourism = DataMapper.mapUiModelToDomain(tourism)
        tourismRepository.setFavoriteTourism(tourism, state)
    }
}