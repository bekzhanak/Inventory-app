package com.example.inventory

import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {
    val allItems : LiveData<List<Item>> = itemDao.getItems().asLiveData()

    private fun getNewItemEntry(itemName : String,
    itemPrice : String,
    itemCount : String) : Item{
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }
    private fun insertItem(item : Item){
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    fun addNewItem(itemName : String, itemPrice: String, itemCount: String){
        val item = getNewItemEntry(itemName,itemPrice,itemCount)
        insertItem(item)
    }

    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

    fun retrieveItem(id : Int) : LiveData<Item>{
        return itemDao.getItem(id).asLiveData()
    }

    private fun updateItem(item : Item){
        viewModelScope.launch {
            itemDao.update(item)
        }
    }
    fun sellItem(item : Item){
        if (item.quantityInStock > 0){
            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
            updateItem(newItem)
        }
    }

    fun isStockAvailable(item: Item) : Boolean = item.quantityInStock > 0

    fun deleteItem(item: Item){
        viewModelScope.launch { itemDao.delete(item) }
    }

    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ): Item {
        return Item(
            itemId,
            itemName,
            itemPrice.toDouble(),
            itemCount.toInt()
        )
    }

    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount)
        updateItem(updatedItem)
    }
}

class InventoryViewModelFactory(private val itemDao: ItemDao) :
        ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)){
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}