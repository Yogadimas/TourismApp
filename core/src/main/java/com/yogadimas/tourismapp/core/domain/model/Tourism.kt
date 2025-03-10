package com.yogadimas.tourismapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tourism(
    val tourismId: String,
    val name: String,
    val description: String,
    val address: String,
    val image: String,
    val isFavorite: Boolean,
) : Parcelable