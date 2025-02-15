package com.example.chaika.data.data_source.dto

import com.google.gson.annotations.SerializedName

data class ConductorDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("family_name")
    val familyName: String,
    @SerializedName("given_name")
    val givenName: String,
    @SerializedName("nickname")
    val nickname: String,  // с сервера приходит nickname
    @SerializedName("image")
    val image: String?
)