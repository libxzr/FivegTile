package moe.xzr.fivegtile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import moe.xzr.fivegtile.ui.theme.FivegTileTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainComposable(viewModel.getCompatibilityState())
        }
    }

    @Composable
    private fun MainComposable(compatibilityState: MainViewModel.CompatibilityState) {
        FivegTileTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    TopBar()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        CompatibilityHint(compatibilityState = compatibilityState)
                        HintCard(compatibilityState)
                        AboutCard()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBar() {
        TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_5g_big),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.size(40.dp)
                )
                Text(text = "Tile")
            }
        })
    }

    @Composable
    private fun Item(title: String, subtitle: String, link: String?) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link))) }
            .padding(0.dp, 5.dp, 0.dp, 5.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.labelLarge)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall)
        }
    }

    @Composable
    private fun AboutCard() {
        CommonCard(title = stringResource(id = R.string.about)) {
            Column {
                Text(
                    text = stringResource(id = R.string.source_code),
                    style = MaterialTheme.typography.bodyMedium
                )
                Column(modifier = Modifier.padding(2.5.dp)) {
                    Item(
                        title = "FivegTile",
                        subtitle = "https://github.com/libxzr/FivegTile",
                        link = "https://github.com/libxzr/FivegTile"
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(id = R.string.open_source_licenses),
                    style = MaterialTheme.typography.bodyMedium
                )
                Column(modifier = Modifier.padding(2.5.dp)) {
                    Item(
                        title = "libsu",
                        subtitle = "Apache License 2.0\n" +
                                "https://github.com/topjohnwu/libsu",
                        link = "https://github.com/topjohnwu/libsu"
                    )
                    Item(
                        title = "Android Jetpack",
                        subtitle = "Apache License 2.0\n" +
                                "https://android.googlesource.com/platform/frameworks/support",
                        link = "https://android.googlesource.com/platform/frameworks/support"
                    )
                    Item(
                        title = "Material Icons",
                        subtitle = "Apache License 2.0\n" +
                                "https://material.io/tools/icons",
                        link = "https://material.io/tools/icons"
                    )
                    Item(
                        title = "kotlin",
                        subtitle = "Apache License 2.0\n" +
                                "https://github.com/JetBrains/kotlin",
                        link = "https://github.com/JetBrains/kotlin"
                    )
                }
            }
        }
    }

    @Composable
    private fun HintCard(compatibilityState: MainViewModel.CompatibilityState) {
        AnimatedVisibility(visible = compatibilityState == MainViewModel.CompatibilityState.COMPATIBLE) {
            CommonCard(title = stringResource(id = R.string.hint)) {
                Text(text = stringResource(id = R.string.hint_good))
            }
        }
        AnimatedVisibility(visible = compatibilityState == MainViewModel.CompatibilityState.ROOT_DENIED) {
            ErrorCard(title = stringResource(id = R.string.hint)) {
                Text(text = stringResource(id = R.string.hint_no_root))
            }
        }
        AnimatedVisibility(visible = compatibilityState == MainViewModel.CompatibilityState.NOT_COMPATIBLE) {
            ErrorCard(title = stringResource(id = R.string.hint)) {
                Text(text = stringResource(id = R.string.hint_not_compatible))
            }
        }
    }

    @Composable
    private fun ErrorCard(
        title: String,
        content: @Composable () -> Unit
    ) {
        CommonCard(
            title = title,
            cardColors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            iconTint = MaterialTheme.colorScheme.error,
            content = content
        )
    }

    @Composable
    private fun CommonCard(
        title: String,
        cardColors: CardColors = CardDefaults.cardColors(),
        iconTint: Color = MaterialTheme.colorScheme.primary,
        content: @Composable () -> Unit = {}
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 10.dp, 20.dp, 10.dp),
            colors = cardColors
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Row(
                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_baseline_push_pin_24),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(iconTint),
                        modifier = Modifier.size(30.dp, 30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Box(modifier = Modifier.padding(5.dp)) {
                    content()
                }
            }
        }
    }

    @Composable
    private fun CompatibilityHint(compatibilityState: MainViewModel.CompatibilityState) {
        Crossfade(targetState = compatibilityState) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Box(
                    modifier = Modifier.size(100.dp, 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (it == MainViewModel.CompatibilityState.PENDING) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp, 50.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Image(
                            painter = painterResource(
                                id = if (it == MainViewModel.CompatibilityState.COMPATIBLE)
                                    R.drawable.ic_baseline_check_24
                                else
                                    R.drawable.ic_baseline_error_24
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(70.dp, 70.dp),
                            colorFilter = ColorFilter.tint(
                                when (it) {
                                    MainViewModel.CompatibilityState.NOT_COMPATIBLE,
                                    MainViewModel.CompatibilityState.ROOT_DENIED
                                    -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        )
                    }
                }
                Text(
                    text = when (it) {
                        MainViewModel.CompatibilityState.PENDING -> stringResource(id = R.string.compatibility_pending)
                        MainViewModel.CompatibilityState.ROOT_DENIED -> stringResource(id = R.string.compatibility_root_not_granted)
                        MainViewModel.CompatibilityState.NOT_COMPATIBLE -> stringResource(id = R.string.compatibility_not_compatible)
                        MainViewModel.CompatibilityState.COMPATIBLE -> stringResource(id = R.string.compatibility_good)
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun DefaultPreview() {
        MainComposable(MainViewModel.CompatibilityState.NOT_COMPATIBLE)
    }
}