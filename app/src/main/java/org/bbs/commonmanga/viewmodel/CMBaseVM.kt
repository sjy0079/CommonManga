package org.bbs.commonmanga.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class CMBaseVM : ViewModel() {
    private val baseUIState: MutableLiveData<CMUIState> = MutableLiveData()

    fun getUIState() = baseUIState
}