package com.yogadimas.tourismapp.favorite.di

import com.yogadimas.tourismapp.favorite.FavoriteViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favoriteModule = module {
    viewModel { FavoriteViewModel(get()) }
}