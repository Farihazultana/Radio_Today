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
    val total: Int
)
