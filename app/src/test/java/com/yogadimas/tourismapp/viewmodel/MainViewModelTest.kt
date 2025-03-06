package com.yogadimas.tourismapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.yogadimas.tourismapp.core.data.Resource
import com.yogadimas.tourismapp.core.domain.usecase.TourismUseCase
import com.yogadimas.tourismapp.core.ui.model.TourismUiModel
import com.yogadimas.tourismapp.dummy.DataDummy
import com.yogadimas.tourismapp.home.HomeViewModel
import com.yogadimas.tourismapp.utils.MainDispatcherRule
import com.yogadimas.tourismapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private lateinit var homeViewModel: HomeViewModel

    @Mock
    private lateinit var tourismUseCase: TourismUseCase

    @Before
    fun setUp() {
        homeViewModel = HomeViewModel(tourismUseCase)
    }


    @Test
    fun `when Get Tourism Should Not Null and Return Data`() = runTest {
        val dummyTourism = DataDummy.generateDummyTourismUiModel()

        Mockito.`when`(tourismUseCase.getAllTourism())
            .thenReturn(flowOf(Resource.Success(dummyTourism)))

        homeViewModel.getTourism()
        val actualTourism: Resource<List<TourismUiModel>> = homeViewModel.tourism.getOrAwaitValue()

        assertNotNull(actualTourism)
        assertTrue(actualTourism is Resource.Success)
        assertEquals(dummyTourism.size, (actualTourism as Resource.Success).data?.size)
        assertEquals(dummyTourism[0], actualTourism.data?.get(0))
    }

    @Test
    fun `when Get Tourism Empty Should Return No Data`() = runTest {

        val emptyTourismList = emptyList<TourismUiModel>()

        val expectedTourism = MutableLiveData<Resource<List<TourismUiModel>>>()
        expectedTourism.value = Resource.Success(emptyTourismList)

        Mockito.`when`(tourismUseCase.getAllTourism())
            .thenReturn(flowOf(Resource.Success(emptyTourismList)))

        homeViewModel.getTourism()

        val actualTourism: Resource<List<TourismUiModel>> = homeViewModel.tourism.getOrAwaitValue()

        assertTrue(actualTourism is Resource.Success)
        assertEquals(0, (actualTourism as Resource.Success).data?.size)
    }


}