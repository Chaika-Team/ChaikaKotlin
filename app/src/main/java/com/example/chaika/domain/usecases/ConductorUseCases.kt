package com.example.chaika.domain.usecases

import android.util.Log
import com.example.chaika.data.data_source.repo.ApiServiceRepositoryInterface
import com.example.chaika.data.local.LocalImageRepository
import com.example.chaika.data.room.repo.RoomConductorRepositoryInterface
import com.example.chaika.domain.models.ConductorDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case для получения данных проводника по токену.
 *
 * Он обращается к репозиторию, который использует Retrofit для запроса userinfo.
 * Здесь репозиторий возвращает Result<ConductorDomain>, который мы преобразуем в ConductorDomain
 * (или выбрасываем ошибку, если запрос неуспешен).
 */
class FetchConductorByTokenUseCase @Inject constructor(
    private val conductorApiRepository: ApiServiceRepositoryInterface
) {
    suspend operator fun invoke(accessToken: String): ConductorDomain =
        withContext(Dispatchers.IO) {
            // Если репозиторий возвращает Result, то можно использовать getOrElse или getOrThrow:
            conductorApiRepository.fetchUserInfo(accessToken).getOrElse { throw it }
        }
}

/**
 * Use case для локального сохранения данных проводника.
 *
 * Сохраняет изображение через LocalImageRepository, затем обновлённого проводника – через RoomConductorRepositoryInterface.
 */
class SaveConductorLocallyUseCase @Inject constructor(
    private val conductorRepository: RoomConductorRepositoryInterface,
    private val imageRepository: LocalImageRepository
) {
    private val TAG = "SaveConductorLocallyUseCase"

    /**
     * Сохраняет данные проводника локально.
     */
    suspend operator fun invoke(conductor: ConductorDomain, imageUrl: String): ConductorDomain =
        withContext(Dispatchers.IO) {
            // Лог входящих данных
            Log.d(TAG, "Получен ConductorDomain: $conductor, imageUrl: $imageUrl")

            // Сохраняем изображение и логируем результат
            val imagePath = imageRepository.saveImageFromUrl(
                imageUrl = imageUrl,
                fileName = "${conductor.employeeID}.jpg",
                subDir = "conductors"
            ) ?: throw IllegalArgumentException("Не удалось сохранить изображение проводника")

            Log.d(TAG, "Изображение сохранено по пути: $imagePath")

            // Обновляем модель проводника с локальным путем к изображению
            val updatedConductor = conductor.copy(image = imagePath)
            Log.d(TAG, "Обновленный проводник для сохранения: $updatedConductor")

            // Сохраняем в базу
            conductorRepository.insertConductor(updatedConductor)
            Log.d(TAG, "Данные проводника успешно сохранены в базе: $updatedConductor")

            updatedConductor
        }
}


/**
 * Use case вызывает юзкейсы для получения с сервера и сохранения данных проводника.
 */
class AuthorizeAndSaveConductorUseCase @Inject constructor(
    private val fetchConductorByTokenUseCase: FetchConductorByTokenUseCase,
    private val saveConductorLocallyUseCase: SaveConductorLocallyUseCase
) {
    /**
     * Получает данные проводника по токену, затем сохраняет их локально (в том числе сохраняет изображение)
     * и возвращает обновлённую доменную модель.
     */
    suspend operator fun invoke(accessToken: String): ConductorDomain {
        val conductor = fetchConductorByTokenUseCase(accessToken)
        return saveConductorLocallyUseCase(conductor, conductor.image)
    }
}


/**
 * Use Case для получения данных проводника из базы данных SQLite.
 * Использует RoomConductorRepositoryInterface.
 **/
class GetAllConductorsUseCase @Inject constructor(
    private val conductorRepository: RoomConductorRepositoryInterface
) {
    operator fun invoke(): Flow<List<ConductorDomain>> {
        return conductorRepository.getAllConductors()
    }
}

/**
 * Use Case для удаления данных проводников из базы данных SQLite.
 **/
class DeleteAllConductorsUseCase @Inject constructor(
    private val conductorRepository: RoomConductorRepositoryInterface
) {
    suspend operator fun invoke() {
        conductorRepository.deleteAllConductors()
    }
}