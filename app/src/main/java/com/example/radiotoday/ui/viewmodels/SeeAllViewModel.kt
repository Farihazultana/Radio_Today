package com.example.radiotoday.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiotoday.data.models.seeAll.SeeAllResponseX
import com.example.radiotoday.data.repositories.SeeAllRepository
import com.example.radiotoday.utils.ResultType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeeAllViewModel @Inject constructor(private val repository: SeeAllRepository) : ViewModel() {
    private val _seeAllData = MutableLiveData<ResultType<SeeAllResponseX>>(ResultType.Loading)
    val seeAllData : LiveData<ResultType<SeeAllResponseX>> = _seeAllData
    fun fetchSeeAllData(section_code: String, page: String){
        viewModelScope.launch {
            try {
                val result = repository.getSeeAllData(section_code, page)
                _seeAllData.value=result
            } catch (e: Exception) {
                _seeAllData.value = ResultType.Error(e)
            }
        }
    }

}