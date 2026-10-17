package com.manish.wardrobe.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothing(clothing: Clothing)

    @Update
    suspend fun updateClothing(clothing: Clothing)

    @Delete
    suspend fun deleteClothing(clothing: Clothing)

    @Query("SELECT * FROM clothing ORDER BY id DESC")
    fun getAllClothes(): Flow<List<Clothing>>
}
