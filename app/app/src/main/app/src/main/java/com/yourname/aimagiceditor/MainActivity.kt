package com.yourname.aimagiceditor

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File

// --- DATA MODELS ---
data class VideoProject(
    var inputUri: Uri? = null,
    var musicVibe: String = "None",
    var ratio: String = "Original",
    var isEnhanced: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainEditorScreen()
                }
            }
        }
    }
}

@Composable
fun MainEditorScreen() {
    val context = LocalContext.current
    var project by remember { mutableStateOf(VideoProject()) }
    var isProcessing by remember { mutableStateOf(false) }
    var progressMessage by remember { mutableStateOf("Ready to Edit") }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> project = project.copy(inputUri = uri) }

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("AI MAGIC EDITOR", style = MaterialTheme.typography.headlineLarge)
        
        Spacer(modifier = Modifier.height(20.dp))

        // 1. VIDEO PREVIEW AREA
        Card(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                if (project.inputUri != null) {
                    Text("Video Loaded: ${project.inputUri!!.lastPathSegment}")
                } else {
                    Button(onClick = { pickerLauncher.launch("video/*") }) {
                        Text("Upload Video")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. AI SETTINGS (The "According to Need" section)
        Text("Manual Tweaks (After AI Draft):", style = MaterialTheme.typography.titleMedium)
        
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { FilterChip(selected = project.ratio == "9:16", onClick = { project = project.copy(ratio = "9:16") }, label = { Text("TikTok (9:16)") }) }
            item { FilterChip(selected = project.musicVibe == "Lo-Fi", onClick = { project = project.copy(musicVibe = "Lo-Fi") }, label = { Text("Music: Lo-Fi") }) }
            item { FilterChip(selected = project.isEnhanced, onClick = { project = project.copy(isEnhanced = !project.isEnhanced) }, label = { Text("AI Enhance") }) }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 3. THE MAGIC EXPORT BUTTON
        if (isProcessing) {
            CircularProgressIndicator()
            Text(progressMessage)
        } else {
            Button(
                onClick = { 
                    if (project.inputUri != null) {
                        isProcessing = true
                        runUltimateAIProcess(context, project) { success, path ->
                            isProcessing = false
                            if (success) Toast.makeText(context, "Saved to: $path", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("RUN AI MAGIC & EXPORT")
            }
        }
    }
}

// --- THE ULTIMATE ENGINE ---
fun runUltimateAIProcess(context: android.content.Context, project: VideoProject, onComplete: (Boolean, String) -> Unit) {
    val inputPath = project.inputUri.toString() 
    val outputPath = File(context.externalCacheDir, "magic_output.mp4").absolutePath
    
    // BUILD THE ULTIMATE COMMAND
    val ratioFilter = if (project.ratio == "9:16") "crop=ih*(9/16):ih" else "scale=iw:ih"
    val enhanceFilter = if (project.isEnhanced) ",hqdn3d,unsharp,eq=contrast=1.1" else ""
    
    val ultimateCommand = """
        -i $inputPath -f lavfi -i anullsrc 
        -filter_complex "[0:v]$ratioFilter$enhanceFilter[v]; [0:a]afftdn,loudnorm[a]" 
        -map "[v]" -map "[a]" -c:v h264_mediacodec -preset fast -b:v 5M $outputPath
    """.trimIndent()

    FFmpegKit.executeAsync(ultimateCommand) { session ->
        val code = session.returnCode
        onComplete(ReturnCode.isSuccess(code), outputPath)
    }
}
