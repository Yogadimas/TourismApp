package com.yogadimas.tourismapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.yogadimas.tourismapp.core.domain.usecase.TourismUseCase
import com.yogadimas.tourismapp.dummy.DataDummy
import com.yogadimas.tourismapp.search.SearchViewModel
import com.yogadimas.tourismapp.utils.MainDispatcherRule
import com.yogadimas.tourismapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
class SearchViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private lateinit var searchViewModel: SearchViewModel

    @Mock
    private lateinit var tourismUseCase: TourismUseCase

    @Before
    fun setUp() {
        searchViewModel = SearchViewModel(tourismUseCase)
    }

    @Test
    fun `when search query is updated, searchResult should return data`() = runTest {
        val keyword = "Toba"
        val dummyTourism = DataDummy.generateDummyTourismUiModel()
        val expectedFlow = flowOf(dummyTourism)

        Mockito.`when`(tourismUseCase.searchTourismByName(keyword)).thenReturn(expectedFlow)

        searchViewModel.queryChannel.value = keyword

        val actualResult = searchViewModel.searchResult.getOrAwaitValue()

        Mockito.verify(tourismUseCase).searchTourismByName(keyword)

        assertNotNull(actualResult)
        assertEquals(dummyTourism.size, actualResult.size)
        assertEquals(dummyTourism[0], actualResult[0])
    }

}