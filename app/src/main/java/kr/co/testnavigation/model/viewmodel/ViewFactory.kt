package kr.co.testnavigation.model.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewFactory( application: Application?)
    : ViewModelProvider.Factory {

    private var application: Application? = application


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return application?.let { RegiViewModel(it) } as T
    }
}