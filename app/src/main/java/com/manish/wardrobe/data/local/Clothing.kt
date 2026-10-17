package com.manish.wardrobe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing")
data class Clothing(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,
    val wearCount: Int = 0,
    val maxWear: Int,
    val imageUri: String? = null

)
