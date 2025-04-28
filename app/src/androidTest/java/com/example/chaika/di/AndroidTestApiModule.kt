package com.example.chaika.di

import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.HttpUrl
import testUtils.TestServerHolder

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApiModule::class],
)
open class AndroidTestApiModule : ApiModule() {
    override fun baseUrl(): HttpUrl = TestServerHolder.testMockServer.server.url("/")
}
