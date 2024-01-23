package com.example.radiotoday.data.models.home

data class ContentX(
    val android: String,
    val content: List<ContentXX>,
    val contenttype: Int,
    val contentviewtype: Int,
    val ios: String,
    val name: String,
    val page_slug: String,
    val position: Int,
    val section_code: String
)