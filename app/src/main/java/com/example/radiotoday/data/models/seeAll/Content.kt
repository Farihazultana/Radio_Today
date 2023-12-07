package com.example.radiotoday.data.models.seeAll

data class Content(
    val albumcode: String,
    val albumname: String,
    val artistname: String,
    val catname: String,
    val cp: String,
    val image_location: String,
    val release: String,
    val songs: Int
)