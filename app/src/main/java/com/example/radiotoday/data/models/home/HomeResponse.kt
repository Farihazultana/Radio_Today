package com.example.radiotoday.data.models.home

import com.example.radiotoday.data.models.ContentMain

data class HomeResponse(
    val content: List<ContentMain>,
    val message: String
)