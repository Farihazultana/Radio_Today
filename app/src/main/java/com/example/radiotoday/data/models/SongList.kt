package com.example.radiotoday.data.models

object SongList {
    var songList = ArrayList<SubContent>()
    var currentPlayPosition = 0
    fun setSongList(songs: ArrayList<SubContent>, position: Int){
        songList.clear()
        songList= songs
        currentPlayPosition = position
    }

    fun getSongsList(): ArrayList<SubContent>{
        return songList
    }
    fun getCurrentPosition():Int{
        return currentPlayPosition
    }

}