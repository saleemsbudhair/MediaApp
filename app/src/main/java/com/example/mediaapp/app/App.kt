package com.example.mediaapp.app
import android.app.Application
import com.google.android.exoplayer2.ExoPlayer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var exoPlayer: ExoPlayer
}