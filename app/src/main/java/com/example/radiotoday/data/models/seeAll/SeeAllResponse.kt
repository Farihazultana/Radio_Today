package com.example.radiotoday.data.models.seeAll

import com.example.radiotoday.data.models.MainContent



data class SeeAllResponse (
    val content: MainContent,
    val message: String,
)