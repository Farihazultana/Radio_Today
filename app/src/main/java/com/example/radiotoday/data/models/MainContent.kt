package com.example.radiotoday.data.models

data class MainContent(
    val current_page: Int,
    val first: String,
    val from: Int,
    val lastPageUrl: String,
    val last_page: Int,
    val nextPageUrl: String,
    val prevPageUrl: String,
    val contents: ArrayList<SubContent>,
    val total: Int,
    val android: String,
    val content: ArrayList<SubContent>,
    val contenttype: Int,
    val contentviewtype: Int,
    val ios: String,
    val name: String,
    val page_slug: String,
    val position: Int,
    val section_code: String,

    val catCode: String,
    val catName: String,

    val token: String,

    val type: String,
    val value: String
)
