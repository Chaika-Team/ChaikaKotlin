package com.example.chaika.data.data_source.apiService

import com.example.chaika.data.data_source.dto.AuthRequestDto
import com.example.chaika.data.data_source.dto.AuthResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API-сервис для авторизации пользователей.
 */
interface AuthApiService {

    /**
     * Отправляет запрос на авторизацию.
     *
     * @param request Объект `AuthRequestDto` с данными для авторизации.
     * @return Объект `AuthResponseDto` с данными токена и профиля.
     */
    @POST("auth")
    suspend fun authorize(@Body request: AuthRequestDto): AuthResponseDto
}
