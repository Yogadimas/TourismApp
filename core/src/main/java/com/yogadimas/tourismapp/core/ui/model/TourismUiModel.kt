package com.yogadimas.tourismapp.core.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TourismUiModel(
    val tourismId: String,
    val name: String,
    val description: String,
    val address: String,
    val image: String,
    val isFavorite: Boolean,
) : Parcelable