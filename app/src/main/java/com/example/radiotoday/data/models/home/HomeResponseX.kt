package com.example.radiotoday.data.models.home

import com.example.radiotoday.data.models.seeAll.ContentX

data class HomeResponseX(
    val content: List<ContentX>,
    val message: String
)