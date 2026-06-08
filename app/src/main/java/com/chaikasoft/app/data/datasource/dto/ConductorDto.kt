package com.chaikasoft.app.data.datasource.dto

import com.google.gson.annotations.SerializedName

data class ConductorDto(
    @SerializedName("given_name")
    val firstName: String,
    @SerializedName("family_name")
    val familyName: String,
    @SerializedName("middle_name")
    val middleName: String? = null,
    @SerializedName("preferred_username")
    val preferredUsername: String,
    @SerializedName(value = "picture", alternate = ["image", "avatarUrl", "avatar_url"])
    val picture: String? = null
)
