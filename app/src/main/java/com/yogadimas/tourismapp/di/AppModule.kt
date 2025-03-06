package com.yogadimas.tourismapp.di


import com.yogadimas.tourismapp.auth.AuthViewModel
import com.yogadimas.tourismapp.core.domain.usecase.TourismInteractor
import com.yogadimas.tourismapp.core.domain.usecase.TourismUseCase
import com.yogadimas.tourismapp.core.domain.usecase.auth.AuthInteractor
import com.yogadimas.tourismapp.core.domain.usecase.auth.AuthUseCase
import com.yogadimas.tourismapp.detail.DetailTourismViewModel
import com.yogadimas.tourismapp.home.HomeViewModel
import com.yogadimas.tourismapp.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<TourismUseCase> { TourismInteractor(get()) }
    factory<AuthUseCase> { AuthInteractor(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { DetailTourismViewModel(get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { AuthViewModel(get()) }
}