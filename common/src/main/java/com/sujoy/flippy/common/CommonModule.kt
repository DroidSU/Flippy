package com.sujoy.flippy.common

import com.sujoy.flippy.common.repository.ProfileRepository
import com.sujoy.flippy.common.repository.ProfileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(impl: NetworkRepositoryImpl): NetworkRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository
}
