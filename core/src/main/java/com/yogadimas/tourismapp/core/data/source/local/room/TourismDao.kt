package com.yogadimas.tourismapp.core.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yogadimas.tourismapp.core.data.source.local.entity.TourismEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TourismDao {

    @Query("SELECT * FROM tourism")
    fun getAllTourism(): Flow<List<TourismEntity>>

    @Query("SELECT * FROM tourism where isFavorite = 1")
    fun getFavoriteTourism(): Flow<List<TourismEntity>>

    @Query("SELECT * FROM tourism WHERE name LIKE '%' || :keyword || '%'")
    fun searchTourismByName(keyword: String): Flow<List<TourismEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTourism(tourism: List<TourismEntity>)

    @Update
    fun updateFavoriteTourism(tourism: TourismEntity)
}
