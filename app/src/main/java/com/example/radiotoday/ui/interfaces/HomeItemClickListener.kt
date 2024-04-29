package com.example.radiotoday.ui.interfaces

import com.example.radiotoday.data.models.SubContent

interface HomeItemClickListener {
    fun onHomeItemClickListener(position: Int, currentItem: SubContent, currentSection: String)

}