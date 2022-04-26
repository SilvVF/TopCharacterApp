package com.example.topcharacterlist.screens.top_chracter_screen

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.topcharacterlist.data.remote.responses.Data
import com.example.topcharacterlist.data.remote.responses.JikanTopCharResponse
import com.example.topcharacterlist.repository.CharacterRepository
import com.example.topcharacterlist.util.Constants.PAGE_SIZE
import com.example.topcharacterlist.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopCharacterViewModel @Inject constructor(
    private val repository: CharacterRepository
): ViewModel() {

    private var currPage = 0

    var characterList = mutableStateOf<List<Data>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        loadTopCharacterList()
    }

    fun loadTopCharacterList() {
        viewModelScope.launch {
            isLoading.value = true
            val response = repository.getTopCharList(currPage)
            handleTopCharacterResponse(response)
        }
    }


    private fun handleTopCharacterResponse(response: Resource<JikanTopCharResponse>){
        when (response){
            is Resource.Success -> {
                endReached.value = (response.`data`?.pagination?.has_next_page) == true
                currPage++
                loadError.value = ""
                isLoading.value = false
                characterList.value += response.`data`?.data!!
            }
            is Resource.Error -> {
                loadError.value = response.message!!
                isLoading.value = false
            }
        }
    }


    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }


}