package com.example.mediaapp.di

import com.google.android.exoplayer2.ExoPlayer
import javax.inject.Inject

class ExoPlayerWrapper @Inject constructor(private val exoPlayer: ExoPlayer) {


    fun getPlayer(): ExoPlayer {
        return exoPlayer
    }

    fun releasePlayer() {
        exoPlayer.release()
    }

}
