package com.example.chaika.domain.usecases

import com.example.chaika.data.data_source.apiService.ApiServiceRepositoryInterface
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
    /**
     * Сохраняет данные проводника в локальной базе данных.
     *
     * @param conductorDomain Доменная модель проводника.
     * @param imageUrl URL изображения проводника, которое нужно сохранить локально.
     * @return Обновлённую доменную модель проводника с локальным путем к изображению.
     */
    suspend operator fun invoke(
        conductorDomain: ConductorDomain,
        imageUrl: String
    ): ConductorDomain = withContext(Dispatchers.IO) {
        // Сохраняем изображение локально
        val imagePath = imageRepository.saveImageFromUrl(
            imageUrl = imageUrl,
            fileName = "${conductorDomain.employeeID}.jpg",
            subDir = "conductors"
        ) ?: throw IllegalArgumentException("Не удалось сохранить изображение проводника")

        // Обновляем модель проводника с локальным путём к изображению
        val updatedConductor = conductorDomain.copy(image = imagePath)
        // Сохраняем проводника в локальной базе данных
        conductorRepository.insertConductor(updatedConductor)
        updatedConductor
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
