package br.edu.utfpr.menfin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.edu.utfpr.menfin.ui.MenFinApp
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MenfinTheme(darkTheme = false) {
                MenFinApp()
            }
        }
    }
}