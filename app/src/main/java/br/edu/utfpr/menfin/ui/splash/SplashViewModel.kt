package br.edu.utfpr.menfin.ui.splash

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SplashUIState(
    val visible: Boolean = true,
    val userLogged: Boolean = false
)

class SplashViewModel : ViewModel() {

    var uiState: SplashUIState by mutableStateOf(SplashUIState())

    init {
        checkUserLogged()
    }

    private fun checkUserLogged() {
        viewModelScope.launch {
            delay(1000)

            uiState = uiState.copy(
                visible = false,
                userLogged = false
            )
        }
    }
}