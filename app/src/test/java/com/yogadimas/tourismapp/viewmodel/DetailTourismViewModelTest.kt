package com.yogadimas.tourismapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.yogadimas.tourismapp.core.domain.usecase.TourismUseCase
import com.yogadimas.tourismapp.detail.DetailTourismViewModel
import com.yogadimas.tourismapp.dummy.DataDummy
import com.yogadimas.tourismapp.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailTourismViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private lateinit var detailTourismViewModel: DetailTourismViewModel

    @Mock
    private lateinit var tourismUseCase: TourismUseCase

    @Before
    fun setUp() {
        detailTourismViewModel = DetailTourismViewModel(tourismUseCase)
    }

    @Test
    fun `verify setFavoriteTourism is called`() {
        val dummyTourism = DataDummy.generateDummyTourismUiModel()[0]
        val newStatus = true

        detailTourismViewModel.setFavoriteTourism(dummyTourism, newStatus)

        Mockito.verify(tourismUseCase).setFavoriteTourism(dummyTourism, newStatus)
    }

}