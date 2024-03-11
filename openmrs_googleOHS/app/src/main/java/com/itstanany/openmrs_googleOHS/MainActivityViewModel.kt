package com.itstanany.openmrs_googleOHS

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivityViewModel(
    application: Application
): AndroidViewModel(application) {
    val authState = MutableSharedFlow<String>()

    private val userAuthState: Flow<String>
        get() = authState

}