package com.chaikasoft.app.auth

import javax.inject.Inject

interface AuthSessionBootstrap {
    suspend fun bootstrapIfNeeded()
}

class NoOpAuthSessionBootstrap @Inject constructor() : AuthSessionBootstrap {
    override suspend fun bootstrapIfNeeded() = Unit
}
