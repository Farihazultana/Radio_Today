package com.example.radiotoday.data.models

data class Errors(
    val email: List<String>,
    val name: List<String>,
    val password: List<String>,
    val password_confirmation: List<String>,
    val phone: List<String>

)