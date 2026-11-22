package com.abryan.cinetrail

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.os.LocaleListCompat
import com.abryan.cinetrail.navigation.AppNavGraph
import com.abryan.cinetrail.ui.theme.CinetrailTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CinetrailTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(
                        onLanguageChange = { languageCode ->
                            val appLocale = LocaleListCompat.forLanguageTags(languageCode)
                            AppCompatDelegate.setApplicationLocales(appLocale)
                        }
                    )
                }
            }
        }
    }
}
