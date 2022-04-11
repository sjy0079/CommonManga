package org.bbs.commonmanga.viewmodel

sealed class CMUIState {
    object LOADING : CMUIState()
    object COMPLETE : CMUIState()
    class ERROR(val info: String) : CMUIState()
}