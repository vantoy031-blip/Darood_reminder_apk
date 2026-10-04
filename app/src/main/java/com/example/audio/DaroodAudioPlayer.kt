package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class DaroodAudioPlayer(private val context: Context) {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    val totalDurationSeconds: Int = 18 // Length of serene Darood recitation melody

    private var playbackJob: Job? = null
    private var audioTrack: AudioTrack? = null

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        if (_isPlaying.value) return
        _isPlaying.value = true

        playbackJob?.cancel()
        playbackJob = CoroutineScope(Dispatchers.Default).launch {
            try {
                // Generate a serene, harmonic calming overtone sequence simulating a peaceful Darood recitation tone
                val sampleRate = 44100
                val totalSamples = totalDurationSeconds * sampleRate
                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(sampleRate / 4)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack = track
                track.play()

                // Notes in peaceful Islamic spiritual scale (D minor / Bayati scale flavors: D4 293Hz, F4 349Hz, G4 392Hz, A4 440Hz, C5 523Hz)
                val baseFreqs = doubleArrayOf(293.66, 349.23, 392.00, 440.00, 392.00, 349.23, 293.66)
                val noteDurationSamples = totalSamples / baseFreqs.size

                val shortBuffer = ShortArray(1024)
                var currentSample = (_progress.value * totalSamples).toInt()

                while (isActive && _isPlaying.value && currentSample < totalSamples) {
                    val noteIdx = (currentSample / noteDurationSamples).coerceIn(0, baseFreqs.size - 1)
                    val freq = baseFreqs[noteIdx]

                    for (i in shortBuffer.indices) {
                        val sampleIndex = currentSample + i
                        if (sampleIndex >= totalSamples) break

                        val timeSec = sampleIndex.toDouble() / sampleRate
                        // Soft envelope
                        val noteLocalSample = sampleIndex % noteDurationSamples
                        val envelope = sin(Math.PI * (noteLocalSample.toDouble() / noteDurationSamples)).coerceIn(0.0, 1.0)
                        
                        // Fundamental + warm harmonic octaves
                        val wave = (sin(2.0 * Math.PI * freq * timeSec) * 0.6 +
                                    sin(2.0 * Math.PI * (freq * 2.0) * timeSec) * 0.25 +
                                    sin(2.0 * Math.PI * (freq * 3.0) * timeSec) * 0.15) * envelope * 0.35

                        shortBuffer[i] = (wave * Short.MAX_VALUE).toInt().toShort()
                    }

                    track.write(shortBuffer, 0, shortBuffer.size)
                    currentSample += shortBuffer.size

                    val currProgress = currentSample.toFloat() / totalSamples
                    _progress.value = currProgress.coerceIn(0f, 1f)
                    _elapsedSeconds.value = (currProgress * totalDurationSeconds).toInt()
                }

                // If playback finished naturally
                if (currentSample >= totalSamples) {
                    _progress.value = 0f
                    _elapsedSeconds.value = 0
                    _isPlaying.value = false
                }
            } catch (_: Exception) {
                _isPlaying.value = false
            } finally {
                try {
                    audioTrack?.stop()
                    audioTrack?.release()
                } catch (_: Exception) {}
                audioTrack = null
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.pause()
        } catch (_: Exception) {}
    }

    fun stop() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
        _progress.value = 0f
        _elapsedSeconds.value = 0
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
