package com.example.radiotoday.data.models.seeAll

import com.example.radiotoday.data.models.SubContent

data class SeeAllMainContent(
    val current_page: Int,
    val first: String,
    val from: Int,
    val lastPageUrl: String,
    val last_page: Int,
    val nextPageUrl: String,
    val prevPageUrl: String,
    val content: ArrayList<SubContent>,
    val total: Int,
)
