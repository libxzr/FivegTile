package moe.xzr.fivegtile.ui.activity.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import moe.xzr.fivegtile.ui.activity.main.compose.MainScreen
import moe.xzr.fivegtile.ui.theme.FivegTileTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FivegTileTheme {
                val state by viewModel.checkCompatibility.collectAsState(
                    initial = MainViewModel.CompatibilityState.PENDING,
                )

                MainScreen(state = state)
            }
        }
    }
}
