package com.example.radiotoday.data.models.seeAll

import com.example.radiotoday.data.models.ContentMain

data class SeeAllPodcastResponse(
    val content: ArrayList<ContentMain>,
    val message: String
)