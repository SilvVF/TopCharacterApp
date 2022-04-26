package com.example.topcharacterlist.screens.top_chracter_screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.palette.graphics.Palette
import coil.Coil
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.topcharacterlist.data.remote.responses.Data
import com.example.topcharacterlist.data.remote.responses.JikanTopCharResponse
import com.example.topcharacterlist.repository.CharacterRepository
import com.example.topcharacterlist.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TopCharacterViewModel @Inject constructor(
    private val repository: CharacterRepository
): ViewModel() {

    var currPage = 0

    var characterList = mutableStateOf<List<Data>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        loadTopCharacterList()
    }

    fun loadTopCharacterList() {
        viewModelScope.launch {
            currPage ++
            isLoading.value = true
            val response = repository.getTopCharList(currPage)
            handleTopCharacterResponse(response)
        }
    }


    private fun handleTopCharacterResponse(response: Resource<JikanTopCharResponse>) =
        viewModelScope.launch {
            when (response){
                is Resource.Success -> {
                    endReached.value = (currPage > response.data?.pagination?.last_visible_page ?: 0)
                    currPage++
                    loadError.value = ""
                    characterList.value += response.`data`?.data!!
                    isLoading.value = false
                }
                is Resource.Error -> {
                    loadError.value = response.message!!
                    isLoading.value = false
                }
            }
        }



    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap // .copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }

    fun fetchColors(url: String, context: Context, onCalculated: (Color) -> Unit) {
        viewModelScope.launch {
            // Requesting the image using coil's ImageRequest
            val req = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()

            val result = req.context.imageLoader.execute(req)

            if (result is SuccessResult) {
                // Save the drawable as a state in order to use it on the composable
                // Converting it to bitmap and using it to calculate the palette
                calcDominantColor(result.drawable) { color ->
                    onCalculated(color)
                }
            }
        }
    }


}