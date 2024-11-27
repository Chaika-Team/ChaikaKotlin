package com.example.chaika.domain.models

data class ConductorDomain(
    val id: Int,
    val name: String,
    val employeeID: String, // Новое поле
    val image: String
)
