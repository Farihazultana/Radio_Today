package com.example.radiotoday.data.models.seeAll

data class ContentX(
    val current_page: Int,
    val first: String,
    val from: Int,
    val lastPageUrl: String,
    val last_page: Int,
    val nextPageUrl: String,
    val prevPageUrl: String,
    val contents: List<ContentXX>,
    val podcasts: List<Podcast>,
    val total: Int,
    val android: String,
    val content: List<com.example.radiotoday.data.models.home.ContentXX>,
    val contenttype: Int,
    val contentviewtype: Int,
    val ios: String,
    val name: String,
    val page_slug: String,
    val position: Int,
    val section_code: String
)
