package com.example.radiotoday.data.models.home

import com.example.radiotoday.data.models.MainContent

data class HomeResponse(
    val content: ArrayList<MainContent>,
    val message: String
)