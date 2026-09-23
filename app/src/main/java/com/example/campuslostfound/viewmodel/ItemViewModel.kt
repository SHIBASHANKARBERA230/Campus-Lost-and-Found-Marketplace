package com.example.campuslostfound.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuslostfound.database.ItemEntity
import com.example.campuslostfound.database.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _lostItems =
        MutableStateFlow<List<ItemEntity>>(emptyList())

    val lostItems: StateFlow<List<ItemEntity>> =
        _lostItems

    private val _foundItems =
        MutableStateFlow<List<ItemEntity>>(emptyList())

    val foundItems: StateFlow<List<ItemEntity>> =
        _foundItems

    fun loadLostItems() {

        viewModelScope.launch {

            _lostItems.value =
                repository.getLostItems()
        }
    }

    fun loadFoundItems() {

        viewModelScope.launch {

            _foundItems.value =
                repository.getFoundItems()
        }
    }

    fun insertItem(item: ItemEntity) {

        viewModelScope.launch {

            repository.insertItem(item)

            if (item.type == "LOST") {
                loadLostItems()
            } else {
                loadFoundItems()
            }
        }
    }

    fun updateItem(
        item: ItemEntity,
        onResult: (Boolean) -> Unit
    ) {

        viewModelScope.launch {

            val rowsUpdated =
                repository.updateItem(item)

            if (rowsUpdated > 0) {

                if (item.type == "LOST") {
                    loadLostItems()
                } else {
                    loadFoundItems()
                }

                onResult(true)

            } else {

                onResult(false)
            }
        }
    }

    fun deleteItemByOwner(
        itemId: Int,
        userId: Int,
        type: String,
        onResult: (Boolean) -> Unit
    ) {

        viewModelScope.launch {

            val rowsDeleted =
                repository.deleteItemByOwner(
                    itemId = itemId,
                    userId = userId
                )

            if (rowsDeleted > 0) {

                if (type == "LOST") {
                    loadLostItems()
                } else {
                    loadFoundItems()
                }

                onResult(true)

            } else {

                onResult(false)
            }
        }
    }
}