package moe.xzr.fivegtile.ui.activity.main.compose

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import moe.xzr.fivegtile.R
import moe.xzr.fivegtile.ui.activity.main.MainViewModel

@Composable
internal fun CompatibilityHint(compatibilityState: MainViewModel.CompatibilityState) {
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
                                MainViewModel.CompatibilityState.SHIZUKU_DENIED
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
                    MainViewModel.CompatibilityState.SHIZUKU_DENIED -> stringResource(id = R.string.compatibility_shizuku_not_granted)
                    MainViewModel.CompatibilityState.NOT_COMPATIBLE -> stringResource(id = R.string.compatibility_not_compatible)
                    MainViewModel.CompatibilityState.COMPATIBLE -> stringResource(id = R.string.compatibility_good)
                },
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
internal fun HintCard(compatibilityState: MainViewModel.CompatibilityState) {
    AnimatedVisibility(visible = compatibilityState == MainViewModel.CompatibilityState.COMPATIBLE) {
        CommonCard(title = stringResource(id = R.string.hint)) {
            Text(text = stringResource(id = R.string.hint_good))
        }
    }
    AnimatedVisibility(visible = compatibilityState == MainViewModel.CompatibilityState.SHIZUKU_DENIED) {
        ErrorCard(title = stringResource(id = R.string.hint)) {
            Text(text = stringResource(id = R.string.hint_no_shizuku))
        }
    }
    AnimatedVisibility(visible = compatibilityState == MainViewModel.CompatibilityState.NOT_COMPATIBLE) {
        ErrorCard(title = stringResource(id = R.string.hint)) {
            Text(text = stringResource(id = R.string.hint_not_compatible))
        }
    }
}

@Composable
internal fun AboutCard() {
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
                    title = "Shizuku",
                    subtitle = "Apache License 2.0\n" +
                            "https://github.com/RikkaApps/Shizuku",
                    link = "https://github.com/RikkaApps/Shizuku"
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
private fun Item(
    title: String,
    subtitle: String,
    link: String?,
) {
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link))) }
        .padding(0.dp, 5.dp, 0.dp, 5.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.labelLarge)
        Text(text = subtitle, style = MaterialTheme.typography.labelSmall)
    }
}
