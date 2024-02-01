package com.example.radiotoday.data.models.login

import com.example.radiotoday.data.models.ContentMain

data class LoginResponse(
    val content: ContentMain,
    val message: String
)