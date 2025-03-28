package com.example.chaika.di

import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.HttpUrl
import testUtils.MockServer

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApiModule::class]
)
class AndroidTestApiModule : ApiModule() {
    override fun baseUrl(): HttpUrl = MockServer.server.url("/")
}