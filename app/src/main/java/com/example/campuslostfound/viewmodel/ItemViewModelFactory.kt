package com.example.campuslostfound.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.campuslostfound.database.ItemRepository

class ItemViewModelFactory(
    private val repository: ItemRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}