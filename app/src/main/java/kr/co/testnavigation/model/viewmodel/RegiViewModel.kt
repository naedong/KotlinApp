package kr.co.testnavigation.model.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.testnavigation.model.datastore.RegiStore


class RegiViewModel (application: Application) : AndroidViewModel(application) {
    private val dataStore = RegiStore<String>(application)

    val getName = dataStore.getRead("NAME").asLiveData(Dispatchers.IO)
    val getAddress = dataStore.getRead("ADDRESS").asLiveData(Dispatchers.IO)
    val getSaveWord = dataStore.getRead("SAVEWORD").asLiveData(Dispatchers.IO)
    val getPhone = dataStore.getRead("PHONE").asLiveData(Dispatchers.IO)
    val getGender = dataStore.getRead("GENDER").asLiveData(Dispatchers.IO)
    val getTest = dataStore.getRead("TEST").asLiveData(Dispatchers.IO)


    fun setName(value : String, name : String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.writeSet(name, value)
        }
    }

    fun setTEST(value: String, test : String){
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.writeSet(test, value)
        }
    }

    fun setPhone(value: String, phone : String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.writeSet(phone, value)
        }
    }

    fun setSaveWord(value: String, word : String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.writeSet(word, value)
        }
    }


    fun  setAddress(value: String, address : String){
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.writeSet(address, value)
        }
    }


    override fun onCleared() {

    }

}
