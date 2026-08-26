package com.ziayzu.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ziayzu.launcher.ui.nav.ZiayzuNavGraph
import com.ziayzu.launcher.ui.theme.ZiayzuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZiayzuTheme {
                ZiayzuNavGraph()
            }
        }
    }
}
