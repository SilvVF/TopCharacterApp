package com.example.topcharacterlist.data.remote.responses

data class JikanTopCharResponse(
    val `data`: List<Data>,
    val pagination: Pagination
)