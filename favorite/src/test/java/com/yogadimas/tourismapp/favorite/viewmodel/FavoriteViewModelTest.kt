package com.yogadimas.tourismapp.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.yogadimas.tourismapp.core.domain.usecase.TourismUseCase
import com.yogadimas.tourismapp.favorite.FavoriteViewModel
import com.yogadimas.tourismapp.favorite.dummy.DataDummy
import com.yogadimas.tourismapp.favorite.utils.MainDispatcherRule
import com.yogadimas.tourismapp.favorite.utils.getOrAwaitValue
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FavoriteViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private lateinit var favoriteViewModel: FavoriteViewModel

    @Mock
    private lateinit var tourismUseCase: TourismUseCase

    @Before
    fun setUp() {
        favoriteViewModel = FavoriteViewModel(tourismUseCase)
    }

    @Test
    fun `when getFavoriteTourism is called, favoriteTourism should return data`() = runTest {
        val dummyTourism = DataDummy.generateDummyTourismUiModelFavorite()
        val expectedFlow = flowOf(dummyTourism)

        Mockito.`when`(tourismUseCase.getFavoriteTourism()).thenReturn(expectedFlow)

        favoriteViewModel.getFavoriteTourism()

        val actualResult = favoriteViewModel.favoriteTourism.getOrAwaitValue()

        Mockito.verify(tourismUseCase).getFavoriteTourism()

        // Pastikan hasil sesuai dengan dummy data
        assertNotNull(actualResult)
        assertEquals(dummyTourism.size, actualResult.size)
        assertEquals(dummyTourism[0], actualResult[0])
    }

}