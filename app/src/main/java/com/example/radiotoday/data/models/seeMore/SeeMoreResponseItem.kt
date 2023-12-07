package com.example.radiotoday.data.models.seeMore

data class SeeMoreResponseItem(
    val albumcat: Any?,
    val albumname: Any?,
    val artistname: Any?,
    val image_location: String,
    val labelname: Any?,
    val release: Any?,
    val similarartist: ArrayList<Similarartist>,
    val songs: Int
)