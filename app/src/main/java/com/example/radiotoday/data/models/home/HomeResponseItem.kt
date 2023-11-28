package com.example.radiotoday.data.models.home

data class HomeResponseItem(
    val catcode: String,
    val catname: String,
    val contents: List<Content>,
    val contenttype: String,
    val contentviewtype: String,
    val itemtype: String
)