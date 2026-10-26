
package org.opendrive.launcher

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF10151C)
private val Panel = Color(0xFF1B232D)
private val Accent = Color(0xFF5CE1E6)
private val Muted = Color(0xFF9AA8B8)

data class LaunchableApp(
    val label: String,
    val packageName: String,
    val icon: Drawable
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OpenDriveApp()
        }
    }
}

@Composable
fun OpenDriveApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var lightMode by remember { mutableStateOf(false) }
    var showAllApps by remember { mutableStateOf(false) }

    val apps = remember { getLaunchableApps(context) }

    val background = if (lightMode) {
        Color(0xFFF0F3F7)
    } else {
        Ink
    }

    val cardColor = if (lightMode) {
        Color.White
    } else {
        Panel
    }

    val primaryText = if (lightMode) {
        Color(0xFF17202A)
    } else {
        Color.White
    }

    val secondaryText = if (lightMode) {
        Color(0xFF657384)
    } else {
        Muted
    }

    val time = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    val date = remember {
        SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(Date())
    }

    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = background
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Left navigation panel
                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "OPENDRIVE",
                        color = Accent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )

                    Text(
                        text = time,
                        color = primaryText,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Light
                    )

                    Text(
                        text = date,
                        color = secondaryText,
                        fontSize = 16.sp
                    )

                    Spacer(Modifier.height(4.dp))

                    MetricChip(
                        icon = Icons.Default.Speed,
                        title = "VEHICLE",
                        value = "Ready",
                        cardColor = cardColor,
                        primaryText = primaryText,
                        secondaryText = secondaryText
                    )

                    ActionTile(
                        icon = Icons.Default.Apps,
                        title = if (showAllApps) "Dashboard" else "All apps",
                        subtitle = if (showAllApps) {
                            "Return to home"
                        } else {
                            "Browse installed apps"
                        },
                        cardColor = cardColor,
                        primaryText = primaryText,
                        secondaryText = secondaryText
                    ) {
                        showAllApps = !showAllApps
                    }

                    ActionTile(
                        icon = Icons.Default.MusicNote,
                        title = "Music",
                        subtitle = "Open your music app",
                        cardColor = cardColor,
                        primaryText = primaryText,
                        secondaryText = secondaryText
                    ) {
                        launchByIntent(
                            context,
                            listOf(
                                "com.spotify.music",
                                "com.google.android.youtube.music",
                                "com.android.music"
                            )
                        )
                    }

                    ActionTile(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        subtitle = "System settings",
                        cardColor = cardColor,
                        primaryText = primaryText,
                        secondaryText = secondaryText
                    ) {
                        runCatching {
                            context.startActivity(
                                Intent(Settings.ACTION_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            )
                        }.onFailure {
                            Toast.makeText(
                                context,
                                "Could not open settings",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = { lightMode = !lightMode },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cardColor,
                            contentColor = primaryText
                        )
                    ) {
                        Icon(
                            Icons.Default.Brightness4,
                            contentDescription = "Toggle appearance"
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (lightMode) "Light mode" else "Dark mode")
                    }
                }

                // Main content panel
                Column(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (showAllApps) {
                        Text(
                            text = "Your apps",
                            color = primaryText,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 115.dp),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = apps,
                                key = { it.packageName }
                            ) { app ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clickable {
                                            launchByIntent(
                                                context,
                                                listOf(app.packageName)
                                            )
                                        },
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = cardColor
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp),
                                        horizontalAlignment =
                                            Alignment.CenterHorizontally,
                                        verticalArrangement =
                                            Arrangement.Center
                                    ) {
                                        // Compatibility fix: convert Drawable
                                        // to Bitmap and display using BitmapPainter.
                                        val appBitmap = remember(app.icon) {
                                            app.icon.toBitmap(
                                                width = 96,
                                                height = 96
                                            ).asImageBitmap()
                                        }

                                        Image(
                                            painter = BitmapPainter(appBitmap),
                                            contentDescription = app.label,
                                            modifier = Modifier.size(42.dp)
                                        )

                                        Spacer(Modifier.height(8.dp))

                                        Text(
                                            text = app.label,
                                            color = primaryText,
                                            fontSize = 13.sp,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Good to see you",
                            color = primaryText,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Your drive. Your apps. Your control.",
                            color = secondaryText,
                            fontSize = 15.sp
                        )

                        // OpenDrive update card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardColor
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            Accent.copy(alpha = 0.15f),
                                            RoundedCornerShape(14.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Uses Apps to avoid requiring extended icons.
                                    Icon(
                                        imageVector = Icons.Default.Apps,
                                        contentDescription = "OpenDrive updates",
                                        tint = Accent,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(Modifier.width(14.dp))

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "OpenDrive updates",
                                        color = primaryText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = "Check GitHub for a new release",
                                        color = secondaryText,
                                        fontSize = 12.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            `OpenDriveUpdater.kt`.checkForUpdate(
                                                context
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Accent,
                                        contentColor = Ink
                                    )
                                ) {
                                    Text("Check")
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            InfoCard(
                                title = "APP LIBRARY",
                                value = "${apps.size} apps",
                                cardColor = cardColor,
                                primaryText = primaryText,
                                secondaryText = secondaryText,
                                modifier = Modifier.weight(1f)
                            )

                            InfoCard(
                                title = "LAUNCHER",
                                value = "Active",
                                cardColor = cardColor,
                                primaryText = primaryText,
                                secondaryText = secondaryText,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardColor
                            )
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text(
                                    text = "Quick note",
                                    color = primaryText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = "Drive safely. Keep your focus on the road.",
                                    color = secondaryText,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(Modifier.weight(1f))

                        Text(
                            text = "OpenDrive • Open-source car launcher",
                            color = secondaryText,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    cardColor: Color,
    primaryText: Color,
    secondaryText: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(26.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = secondaryText,
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp
                )

                Text(
                    text = value,
                    color = primaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ActionTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    cardColor: Color,
    primaryText: Color,
    secondaryText: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Accent,
                modifier = Modifier.size(27.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = primaryText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = subtitle,
                    color = secondaryText,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    value: String,
    cardColor: Color,
    primaryText: Color,
    secondaryText: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = secondaryText,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                color = primaryText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun getLaunchableApps(context: Context): List<LaunchableApp> {
    val packageManager = context.packageManager

    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    return packageManager.queryIntentActivities(intent, 0)
        .mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo?.packageName
                ?: return@mapNotNull null

            LaunchableApp(
                label = resolveInfo.loadLabel(packageManager).toString(),
                packageName = packageName,
                icon = resolveInfo.loadIcon(packageManager)
            )
        }
        .filter { it.packageName != context.packageName }
        .distinctBy { it.packageName }
        .sortedBy { it.label.lowercase(Locale.getDefault()) }
}

private fun launchByIntent(
    context: Context,
    packageNames: List<String>
) {
    val packageManager = context.packageManager

    for (packageName in packageNames) {
        val launchIntent =
            packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            return
        }
    }

    Toast.makeText(
        context,
        "No compatible app found",
        Toast.LENGTH_SHORT
    ).show()
}
