package br.edu.utfpr.menfin.ui.drawer

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class DrawerUiState(
    val isProcessingLogout: Boolean = false,
    val hasErrorLogout: Boolean = false,
    val logoutSuccess: Boolean = false,
    val userLogged: String = ""
)

class DrawerViewModel : ViewModel() {

    private val tag: String = "DrawerViewModel"
    var uiState: DrawerUiState by mutableStateOf(DrawerUiState())

    init {
        loadUserLogged()
    }

    private fun loadUserLogged() {
        viewModelScope.launch {
            uiState = uiState.copy(
                userLogged = "User"
            )
        }
    }

    fun logout() {
        uiState = uiState.copy(
            isProcessingLogout = true,
            hasErrorLogout = false
        )

        viewModelScope.launch {
            uiState = try {
                uiState.copy(
                    isProcessingLogout = false,
                    logoutSuccess = true
                )
            } catch (ex: Exception) {
                Log.d(tag, "Erro ao efetuar o logout", ex)
                uiState.copy(
                    isProcessingLogout = false,
                    hasErrorLogout = true
                )
            }
        }
    }
}