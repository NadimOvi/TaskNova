package com.nadim.tasknova.ui.screens.voice

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nadim.tasknova.ui.theme.*
import com.nadim.tasknova.viewmodel.VoiceState
import com.nadim.tasknova.viewmodel.VoiceViewModel
import java.util.Locale

@Composable
fun VoiceScreen(
    onNavigateBack: () -> Unit,
    viewModel: VoiceViewModel = hiltViewModel()
) {
    val context      = LocalContext.current
    val voiceState   by viewModel.voiceState.collectAsState()
    val transcript   by viewModel.transcript.collectAsState()
    val ariaResponse by viewModel.ariaResponse.collectAsState()

    var isListening by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }

    // Speech recognizer
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    // Setup recognition listener
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
            }
            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: ""
                isListening = false
                if (text.isNotEmpty()) {
                    viewModel.onUserSpoke(text)
                }
            }
            override fun onError(error: Int) {
                isListening = false
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }
            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull() ?: ""
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    DisposableEffect(Unit) {
        onDispose { speechRecognizer.destroy() }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechRecognizer.startListening(intent)
        isListening = true
    }

    fun stopListening() {
        speechRecognizer.stopListening()
        isListening = false
    }

    // Pulse animation when listening
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = if (isListening) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AriaDark, AriaSurface)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text       = "Aria",
                    style      = MaterialTheme.typography.titleLarge,
                    color      = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Aria response bubble
            if (ariaResponse.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(containerColor = AriaCard)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text       = "✦ Aria",
                            color      = AriaGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text  = ariaResponse,
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // User transcript
            if (transcript.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(20.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = AriaSurface
                    )
                ) {
                    Text(
                        text     = "You: $transcript",
                        color    = TextSecondary,
                        style    = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status text
            Text(
                text = when {
                    isListening                          -> "Listening..."
                    voiceState is VoiceState.Processing  -> "Aria is thinking..."
                    voiceState is VoiceState.Speaking    -> "Aria is speaking..."
                    voiceState is VoiceState.AwaitingConfirmation -> "Please confirm"
                    voiceState is VoiceState.ActionSaved -> "✓ Saved!"
                    else                                 -> "Tap mic to speak"
                },
                color = when {
                    isListening                          -> AriaGreen
                    voiceState is VoiceState.Processing  -> AriaAccent
                    voiceState is VoiceState.ActionSaved -> StatusDone
                    else                                 -> TextSecondary
                },
                fontWeight = FontWeight.Medium,
                fontSize   = 16.sp,
                textAlign  = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Confirmation buttons
            if (voiceState is VoiceState.AwaitingConfirmation) {
                val confirmText =
                    (voiceState as VoiceState.AwaitingConfirmation).confirmationText
                Text(
                    text      = confirmText,
                    color     = TextPrimary,
                    style     = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick  = { viewModel.onRejected() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape  = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, PriorityHigh
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PriorityHigh
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "No")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("No")
                    }
                    Button(
                        onClick  = { viewModel.onConfirmed() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape  = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AriaGreen,
                            contentColor   = Color.Black
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Yes")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Yes", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Mic button
            Box(contentAlignment = Alignment.Center) {
                // Pulse ring
                if (isListening) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .scale(scale)
                            .clip(CircleShape)
                            .background(AriaGreen.copy(alpha = 0.15f))
                    )
                }
                // Main mic FAB
                FloatingActionButton(
                    onClick = {
                        if (isListening) {
                            stopListening()
                        } else {
                            viewModel.resetToIdle()
                            startListening()
                        }
                    },
                    modifier       = Modifier.size(88.dp),
                    containerColor = if (isListening) AriaGreen else AriaSurface,
                    contentColor   = if (isListening) Color.Black else AriaGreen,
                    shape          = CircleShape
                ) {
                    Icon(
                        imageVector        = if (isListening)
                            Icons.Default.Mic
                        else
                            Icons.Default.MicOff,
                        contentDescription = "Mic",
                        modifier           = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text  = if (isListening) "Tap to stop" else "Tap to speak",
                color = TextHint,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}