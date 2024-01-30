package com.example.radiotoday.data.models.showDetails

import com.example.radiotoday.data.models.SubContent

data class ShowDetailsResponse(
    val content: SubContent,
    val message: String
)