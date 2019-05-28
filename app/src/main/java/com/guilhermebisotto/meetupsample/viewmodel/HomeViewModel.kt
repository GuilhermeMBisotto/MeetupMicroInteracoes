package com.guilhermebisotto.meetupsample.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.guilhermebisotto.meetupsample.model.EventModel

class HomeViewModel : ViewModel() {

    private val _homeItems = MutableLiveData<List<EventModel>>()
    private val _homeItemsCopy = MutableLiveData<List<EventModel>>()
    val events: LiveData<List<EventModel>> = Transformations.map(_homeItems) {
        it
    }

    fun filterEvents(textFilter: String) {
        if (textFilter.isNotEmpty()) {
            _homeItems.value = _homeItemsCopy.value?.filter {
                it.name.contains(textFilter, true)
            }
        } else {
            _homeItems.value = _homeItemsCopy.value
        }
    }

    fun getItems(quantity: Int) {
        _homeItems.value = createItems(quantity)
        _homeItemsCopy.value = _homeItems.value
    }

    private fun createItems(quantity: Int): List<EventModel> {
        val list = mutableListOf<EventModel>()
        for (index in 0 until quantity) {
            list.add(
                EventModel(
                    index,
                    "$index Disney on Ice - Em Busca dos Sonhos",
                    "Porto Alegre",
                    "RS",
                    "Ginásio Gigantinho",
                    "Terça",
                    14,
                    "Domingo",
                    19,
                    "Maio",
                    "https://www.jornalnopalco.com.br/wp-content/uploads/2019/01/49949086_1671435419624480_1854533403437694976_n.jpg"
                )
            )
        }
        return list
    }
}