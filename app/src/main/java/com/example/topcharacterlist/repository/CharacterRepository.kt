package com.example.topcharacterlist.repository

import com.example.topcharacterlist.data.remote.JikanApi
import com.example.topcharacterlist.data.remote.responses.JikanTopCharResponse
import com.example.topcharacterlist.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class CharacterRepository @Inject constructor(
    private val api: JikanApi
){

    suspend fun getTopCharList(page: Int): Resource<JikanTopCharResponse> {
        val response = try {
            api.getTopCharacters(page)
        } catch (e : Exception) {
            return Resource.Error("Error")
        }
        return Resource.Success(response)
    }

    suspend fun getCharByName(page: Int, name: String): Resource<JikanTopCharResponse> {
        val response = try {
            api.searchCharacterByName(page, name)
        } catch (e : Exception) {
            return Resource.Error("Error")
        }
        return Resource.Success(response)
    }
}