package com.example.topcharacterlist.data.remote

import com.example.topcharacterlist.data.remote.responses.Data
import com.example.topcharacterlist.data.remote.responses.JikanTopCharResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JikanApi {

    @GET("top/characters")
    suspend fun getTopCharacters(
        @Query("page")
        page: Int = 1
    ) : JikanTopCharResponse

    @GET("characters")
    suspend fun searchCharacterByName(
        @Query("page")
        page: Int = 1,
        @Query("q")
        query: String
    ) : JikanTopCharResponse

    @GET("random/character")
    suspend fun getRandomCharacter() : Data

}