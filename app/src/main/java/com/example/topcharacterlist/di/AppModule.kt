package com.example.topcharacterlist.di

import com.example.topcharacterlist.data.remote.JikanApi
import com.example.topcharacterlist.repository.CharacterRepository
import com.example.topcharacterlist.util.Constants.BASE_URL
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

//contains the api and repository
@Module
@InstallIn(SingletonComponent::class) //application scope
object AppModule {

    @Singleton
    @Provides
    fun provideJikanRepository(
        api: JikanApi
    ) = CharacterRepository(api)


    @Singleton
    @Provides //builds the retrofit instance
    fun provideJikanApi(): JikanApi {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(JikanApi::class.java)
    }
}