package com.example.mediaapp.model

data class Album(
    val name: String,
    val imageUrl: String,
    val id: String,
    val albumInfo: String,
    val songs: List<Song>
)