package com.manish.wardrobe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manish.wardrobe.data.local.AppDatabase
import com.manish.wardrobe.data.local.Clothing
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClothingViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase
        .getDatabase(application)
        .clothingDao()

    val clothingList = dao
        .getAllClothes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun deleteClothing(clothing: Clothing) {
        viewModelScope.launch {
            dao.deleteClothing(clothing)
        }
    }
    fun decreaseWear(clothing: Clothing) {
        if (clothing.wearCount <= 0) return

        viewModelScope.launch {
            dao.updateClothing(
                clothing.copy(wearCount = clothing.wearCount - 1)
            )
        }
    }

    fun addClothing(
        name: String,
        category: String,
        maxWear: Int,
        imageUri: String?
    ) {
        viewModelScope.launch {
            val clothing = Clothing(
                name = name,
                category = category,
                maxWear = maxWear,
                imageUri = imageUri
            )
            dao.insertClothing(clothing)
        }
    }


    fun increaseWear(clothing: Clothing) {
        if (clothing.wearCount >= clothing.maxWear) return

        viewModelScope.launch {
            dao.updateClothing(
                clothing.copy(wearCount = clothing.wearCount + 1)
            )
        }
    }


    fun resetWear(clothing: Clothing) {
        viewModelScope.launch {
            dao.updateClothing(
                clothing.copy(wearCount = 0)
            )
        }
    }
}
