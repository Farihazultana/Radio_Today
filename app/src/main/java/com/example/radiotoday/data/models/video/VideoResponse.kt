package com.example.radiotoday.data.models.video

import com.example.radiotoday.data.models.audio.Content

data class VideoResponse(
    val catname: String,
    val contents: ArrayList<Content>
)
