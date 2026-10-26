package org.opendrive.launcher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF0B1017)
private val Panel = Color(0xFF151E29)
private val Panel2 = Color(0xFF1C2937)
private val Accent = Color(0xFF56D6C2)
private val Muted = Color(0xFF9AAABD)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OpenDriveApp() }
    }
}

data class LaunchableApp(val label: String, val packageName: String, val icon: android.graphics.drawable.Drawable)

@Composable
private fun OpenDriveApp() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    // Automotive displays may report 360–480dp height even at physical 1280x720.
    val compact = configuration.screenHeightDp < 430
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showApps by remember { mutableStateOf(false) }
    var lightMode by remember { mutableStateOf(false) }
    var apps by remember { mutableStateOf(emptyList<LaunchableApp>()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }
    LaunchedEffect(showApps) {
        if (showApps) {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
            @Suppress("DEPRECATION")
            apps = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
                .mapNotNull { info ->
                    val pkg = info.activityInfo?.packageName ?: return@mapNotNull null
                    if (pkg == context.packageName) return@mapNotNull null
                    LaunchableApp(info.loadLabel(pm).toString(), pkg, info.loadIcon(pm))
                }
                .distinctBy { it.packageName }
                .sortedBy { it.label.lowercase(Locale.getDefault()) }
        }
    }

    val bg = if (lightMode) Color(0xFFEAF0F4) else Ink
    val panel = if (lightMode) Color.White else Panel
    val primary = if (lightMode) Color(0xFF14212C) else Color.White
    val secondary = if (lightMode) Color(0xFF536474) else Muted

    MaterialTheme(colorScheme = darkColorScheme(primary = Accent, background = bg, surface = panel)) {
        Surface(modifier = Modifier.fillMaxSize(), color = bg) {
            if (showApps) {
                Column(Modifier.fillMaxSize().padding(if (compact) 14.dp else 22.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("APP LIBRARY", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            Text("Your apps", color = primary, fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Button(onClick = { showApps = false }) { Text("Dashboard") }
                    }
                    Spacer(Modifier.height(if (compact) 10.dp else 16.dp))
                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 130.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(apps, key = { it.packageName }) { app ->
                            Column(
                                Modifier.fillMaxWidth().background(panel, RoundedCornerShape(16.dp)).clickable {
                                    context.packageManager.getLaunchIntentForPackage(app.packageName)?.let(context::startActivity)
                                }.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(Modifier.size(44.dp).background(Panel2, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                    Text(app.label.take(1).uppercase(Locale.getDefault()), color = Accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(app.label, color = primary, fontSize = 14.sp, maxLines = 2)
                            }
                        }
                    }
                }
            } else {
                Row(Modifier.fillMaxSize().padding(if (compact) 10.dp else 18.dp), horizontalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 16.dp)) {
                    Column(Modifier.weight(1.05f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(Modifier.size(9.dp).background(Accent, RoundedCornerShape(50)))
                            Text("OPENDRIVE", color = secondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { lightMode = !lightMode }) { Icon(Icons.Default.Brightness4, "Toggle theme", tint = secondary) }
                        }
                        Column(Modifier.weight(1f).fillMaxWidth().background(panel, RoundedCornerShape(24.dp)).padding(if (compact) 14.dp else 22.dp), verticalArrangement = Arrangement.Center) {
                            Text(SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(Date(now)).uppercase(Locale.getDefault()), color = secondary, fontSize = 13.sp, letterSpacing = 1.5.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(now)), color = primary, fontSize = if (compact) 48.sp else 62.sp, fontWeight = FontWeight.Light, letterSpacing = (-2).sp)
                            Spacer(Modifier.height(if (compact) 10.dp else 18.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MetricChip(
                                    "GPS SPEED",
                                    "-- km/h",
                                    Icons.Default.Speed,
                                    Panel2,
                                    primary,
                                    secondary
                                )

                                MetricChip(
                                    "COMPASS",
                                    "--°",
                                    Icons.Default.Explore,
                                    Panel2,
                                    primary,
                                    secondary
                                )
                         }
                        }
                        Row(Modifier.fillMaxWidth().height(if (compact) 62.dp else 78.dp), horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)) {
                            ActionTile("All apps", Icons.Default.Apps, panel, primary, secondary, Modifier.weight(1f)) { showApps = true }
                            ActionTile("Music", Icons.Default.MusicNote, panel, primary, secondary, Modifier.weight(1f)) { launchByIntent(context, "android.intent.action.MUSIC_PLAYER") }
                            ActionTile("Settings", Icons.Default.Settings, panel, primary, secondary, Modifier.weight(1f)) { context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) }
                        }
                    }
                    Column(Modifier.weight(0.9f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 14.dp)) {
                        Column(Modifier.weight(1f).fillMaxWidth().background(Panel2, RoundedCornerShape(24.dp)).padding(if (compact) 14.dp else 22.dp), verticalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("NOW PLAYING", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                                    Spacer(Modifier.height(6.dp))
                                    Text("Ready for your drive", color = primary, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                                    Text("Connect a media app to show track details.", color = secondary, fontSize = 13.sp)
                                }
                                Icon(Icons.Default.MusicNote, null, tint = Accent, modifier = Modifier.size(38.dp))
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(onClick = { launchByIntent(context, "android.intent.action.MUSIC_PLAYER") }, colors = ButtonDefaults.buttonColors(containerColor = Accent, contentColor = Ink)) { Text("Open music") }
                                OutlinedButton(onClick = { showApps = true }) { Text("Browse apps") }
                            }
                        }
                        Row(Modifier.fillMaxWidth().background(panel, RoundedCornerShape(18.dp)).padding(if (compact) 10.dp else 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("QUICK NOTE", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                                Text("Widgets and shortcuts are coming next.", color = primary, fontSize = 14.sp)
                            }
                            Text("v0.1", color = secondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricChip(label: String, value: String, icon: ImageVector, panel: Color, primary: Color, secondary: Color) {
    Column(Modifier.background(panel, RoundedCornerShape(14.dp)).padding(horizontal = 12.dp, vertical = 10.dp).widthIn(min = 105.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(icon, null, tint = Accent, modifier = Modifier.size(15.dp))
            Text(label, color = secondary, fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Text(value, color = primary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ActionTile(title: String, icon: ImageVector, panel: Color, primary: Color, secondary: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(modifier.background(panel, RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        Icon(icon, null, tint = Accent, modifier = Modifier.size(24.dp))
        Text(title, color = primary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

private fun launchByIntent(context: android.content.Context, action: String) {
    val intent = Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
        .onFailure { context.startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) }
}
