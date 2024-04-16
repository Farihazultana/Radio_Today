package com.example.radiotoday.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.seeAll.SeeAllResponse
import com.example.radiotoday.data.repositories.NewsRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val newsRepository: NewsRepository): ViewModel(){
    private val _newsData = MutableLiveData<ResultType<SeeAllResponse>>(ResultType.Loading)
    val newsData: LiveData<ResultType<SeeAllResponse>> = _newsData
    fun fetchNewsData(page: String){
        viewModelScope.launch {
            try {
                val result = newsRepository.getNewsData(page)
                _newsData.value = result
            }catch (e: Exception){
                _newsData.value = ResultType.Error(e)
            }
        }
    }
}