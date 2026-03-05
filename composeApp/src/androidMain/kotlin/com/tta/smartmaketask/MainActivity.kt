package com.tta.smartmaketask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tta.smartmaketask.db.DatabaseDriverFactory
import com.tta.smartmaketask.screen.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val databaseDriverFactory = DatabaseDriverFactory(applicationContext)

        setContent {
            App(databaseDriverFactory)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(DatabaseDriverFactory(androidx.compose.ui.platform.LocalContext.current))
}