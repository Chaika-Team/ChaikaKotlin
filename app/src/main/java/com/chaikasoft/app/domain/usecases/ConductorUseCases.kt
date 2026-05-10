package com.chaikasoft.app.domain.usecases

import android.util.Log
import com.chaikasoft.app.data.datasource.repo.IAMApiServiceRepositoryInterface
import com.chaikasoft.app.data.local.ImageSubDir
import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomConductorRepositoryInterface
import com.chaikasoft.app.di.IoDispatcher
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.util.normalizedRemoteImageUrlOrNull
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Use case для получения данных проводника по токену.
 *
 * Он обращается к репозиторию, который использует Retrofit для запроса userinfo.
 * Здесь репозиторий возвращает Result<ConductorDomain>, который мы преобразуем в ConductorDomain
 * (или выбрасываем ошибку, если запрос неуспешен).
 */
class FetchConductorByTokenUseCase @Inject constructor(
    private val conductorApiRepository: IAMApiServiceRepositoryInterface,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(accessToken: String): ConductorDomain = withContext(ioDispatcher) {
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
    private val imageRepository: LocalImageRepositoryInterface,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Сохраняет данные проводника локально.
     */
    suspend operator fun invoke(conductor: ConductorDomain, imageUrl: String): ConductorDomain =
        withContext(ioDispatcher) {
            val normalizedImageUrl = imageUrl.normalizedRemoteImageUrlOrNull()
            val imageToStore = if (normalizedImageUrl != null) {
                val imagePath = imageRepository.saveImageFromUrl(
                    imageUrl = normalizedImageUrl,
                    fileName = "${conductor.employeeID}.jpg",
                    subDir = ImageSubDir.CONDUCTORS.folder
                )
                if (imagePath != null) {
                    Log.i(CONDUCTOR_IMAGE_SYNC_LOG_TAG, "Conductor image saved locally")
                } else {
                    Log.w(
                        CONDUCTOR_IMAGE_SYNC_LOG_TAG,
                        "Conductor image save failed, keeping remote url"
                    )
                }
                imagePath ?: normalizedImageUrl
            } else {
                Log.i(CONDUCTOR_IMAGE_SYNC_LOG_TAG, "Conductor image is missing from API")
                ""
            }

            val toInsert = conductor.copy(image = imageToStore)
            conductorRepository.insertConductor(toInsert)

            // Получаем уже сохранённого проводника из БД
            conductorRepository.getConductorByEmployeeID(toInsert.employeeID)
                ?: throw IllegalStateException("Вставленный проводник не найден")
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
    operator fun invoke(): Flow<List<ConductorDomain>> = conductorRepository.getAllConductors()
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

private const val CONDUCTOR_IMAGE_SYNC_LOG_TAG = "ConductorImageSync"
