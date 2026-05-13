package com.chaikasoft.app.e2e.seams

import com.chaikasoft.app.auth.AuthSessionBootstrap
import com.chaikasoft.app.data.crypto.EncryptedTokenManagerInterface
import com.chaikasoft.app.data.room.repo.RoomConductorRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomStationRepositoryInterface
import com.chaikasoft.app.di.IoDispatcher
import com.chaikasoft.app.e2e.fixtures.E2EFixtures
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeterministicAuthSessionBootstrap @Inject constructor(
    private val tokenManager: EncryptedTokenManagerInterface,
    private val conductorRepository: RoomConductorRepositoryInterface,
    private val stationRepository: RoomStationRepositoryInterface,
    private val productRepository: RoomProductInfoRepositoryInterface,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : AuthSessionBootstrap {
    override suspend fun bootstrapIfNeeded() = withContext(ioDispatcher) {
        tokenManager.saveToken(E2EFixtures.AUTH_TOKEN)
        conductorRepository.deleteAllConductors()
        conductorRepository.insertConductor(E2EFixtures.conductor)
        stationRepository.upsertAll(E2EFixtures.stations)
        E2EFixtures.products.forEach { productRepository.insertProduct(it) }
    }
}
