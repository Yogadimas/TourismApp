package com.yogadimas.tourismapp.favorite.dummy

import com.yogadimas.tourismapp.core.ui.model.TourismUiModel

object DataDummy {
    fun generateDummyTourismUiModelFavorite(): List<TourismUiModel> {
        return List(20) { i ->
            TourismUiModel(
                tourismId = i.toString(),
                name = "name $i",
                description = "description $i",
                address = "address $i",
                image = "https://upload.wikimedia.org/wikipedia/commons/thumb/$i.jpg",
                isFavorite = true,
            )
        }
    }
}