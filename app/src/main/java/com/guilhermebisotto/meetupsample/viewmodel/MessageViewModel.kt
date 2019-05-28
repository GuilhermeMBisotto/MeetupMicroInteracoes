package com.guilhermebisotto.meetupsample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class MessageViewModel : ViewModel() {

    private val _messageItems = MutableLiveData<List<String>>()
    val messageItems: LiveData<List<String>> = Transformations.map(_messageItems) {
        it
    }

    fun getItems() {
        _messageItems.value = createItems()
    }

    private fun createItems(): List<String> {
        val list = mutableListOf<String>()
        for (index in 0 until 10) {
            list.add(
                "$index Disney on Ice - Em Busca dos Sonhos"
            )
        }
        return list
    }
}