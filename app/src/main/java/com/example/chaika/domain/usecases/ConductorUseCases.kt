package com.example.chaika.domain.usecases

import android.util.Base64
import com.example.chaika.data.crypto.KeyStoreCryptoManagerInterface
import com.example.chaika.data.data_source.apiService.AuthApiService
import com.example.chaika.data.data_source.dto.AuthRequestDto
import com.example.chaika.data.data_source.dto.AuthResponseDto
import com.example.chaika.data.local.LocalImageRepository
import com.example.chaika.data.room.repo.RoomConductorRepositoryInterface
import com.example.chaika.domain.models.ConductorDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Юзкейсы для работы с проводниками.
 */

/**
 * Юзкейс для получения данных проводника с сервера.
 *
 * @param authApiService API-сервис для авторизации.
 */
class FetchConductorFromServerUseCase @Inject constructor(
    private val authApiService: AuthApiService
) {

    /**
     * Отправляет запрос на сервер для проверки учетных данных.
     *
     * @param employeeID Табельный номер проводника.
     * @param password Пароль проводника.
     * @return Ответ с данными токена и профиля.
     */
    suspend operator fun invoke(employeeID: String, password: String): AuthResponseDto {
        return withContext(Dispatchers.IO) {
            authApiService.authorize(AuthRequestDto(employeeID, password))
        }
    }
}

/**
 * Юзкейс для шифрования токена проводника.
 *
 * @param cryptoManager Менеджер для работы с шифрованием.
 */
class EncryptConductorTokenUseCase @Inject constructor(
    private val cryptoManager: KeyStoreCryptoManagerInterface
) {

    /**
     * Шифрует токен с использованием табельного номера проводника.
     *
     * @param employeeID Табельный номер проводника.
     * @param token Токен, полученный с сервера.
     * @return Зашифрованный токен в формате Base64.
     */
    suspend operator fun invoke(employeeID: String, token: String): String =
        withContext(Dispatchers.IO) {
            val encryptedToken = cryptoManager.encrypt(token.toByteArray(), employeeID)
            Base64.encodeToString(encryptedToken, Base64.DEFAULT)
        }
}

/**
 * Юзкейс для локального сохранения данных проводника.
 *
 * @param imageRepository Репозиторий для работы с изображениями.
 * @param conductorRepository Репозиторий для работы с проводниками в локальной базе данных.
 */
class SaveConductorLocallyUseCase @Inject constructor(
    private val conductorRepository: RoomConductorRepositoryInterface,
    private val imageRepository: LocalImageRepository
) {

    /**
     * Сохраняет данные проводника в локальной базе данных.
     *
     * @param conductorDomain Доменная модель проводника.
     * @param imageUrl Ссылка на изображение проводника.
     * @return Локальный путь к сохранённому изображению.
     */
    suspend operator fun invoke(
        conductorDomain: ConductorDomain,
        imageUrl: String
    ): ConductorDomain {
        return withContext(Dispatchers.IO) {
            // Сохраняем изображение локально
            val imagePath = imageRepository.saveImageFromUrl(
                imageUrl = imageUrl,
                fileName = "${conductorDomain.employeeID}.jpg",
                subDir = "conductors"
            ) ?: throw IllegalArgumentException("Не удалось сохранить изображение проводника")

            // Обновляем доменную модель проводника с локальным путём к изображению
            val updatedConductor = conductorDomain.copy(image = imagePath)

            // Сохраняем проводника в локальной базе данных
            conductorRepository.insertConductor(updatedConductor)

            updatedConductor
        }
    }
}

/**
 * Юзкейс для авторизации проводника.
 *
 * @param fetchConductorFromServerUseCase Юзкейс для получения данных с сервера.
 * @param encryptConductorTokenUseCase Юзкейс для шифрования токена.
 * @param saveConductorLocallyUseCase Юзкейс для локального сохранения данных проводника.
 */
class AuthorizeConductorUseCase @Inject constructor(
    private val fetchConductorFromServerUseCase: FetchConductorFromServerUseCase,
    private val encryptConductorTokenUseCase: EncryptConductorTokenUseCase,
    private val saveConductorLocallyUseCase: SaveConductorLocallyUseCase
) {

    /**
     * Авторизует проводника, проверяя данные на сервере, шифруя токен и сохраняя данные локально.
     *
     * @param employeeID Табельный номер проводника.
     * @param password Пароль проводника.
     * @return Доменная модель проводника с сохранёнными данными.
     */
    suspend operator fun invoke(employeeID: String, password: String): ConductorDomain {
        // 1. Получаем данные проводника с сервера
        val serverResponse = fetchConductorFromServerUseCase(employeeID, password)

        // 2. Шифруем токен, полученный с сервера
        val encryptedToken = encryptConductorTokenUseCase(employeeID, serverResponse.token)

        // 3. Создаём доменную модель проводника
        val conductorDomain = ConductorDomain(
            id = 0, // ID назначается Room автоматически
            name = serverResponse.name,
            employeeID = employeeID,
            token = encryptedToken, // Сохраняем зашифрованный токен
            image = "" // Заполняется в следующем шаге
        )

        // 4. Сохраняем проводника локально, включая изображение
        return saveConductorLocallyUseCase(conductorDomain, serverResponse.image)
    }
}
