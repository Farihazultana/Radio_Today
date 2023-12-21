package com.example.radiotoday.data.models.showDetails

data class ShowDetailsResponseItem(
    val albumcat: Any?,
    val albumname: Any?,
    val artistname: Any?,
    val image_location: String,
    val labelname: Any?,
    val release: Any?,
    val similarartist: ArrayList<SimilarArtist>,
    val songs: Int
)