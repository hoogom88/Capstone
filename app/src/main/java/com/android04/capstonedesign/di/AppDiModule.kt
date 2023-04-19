package com.android04.capstonedesign.di

import android.content.Context
import com.android04.capstonedesign.data.dataSource.logDataSource.local.LogLocalDataSourceImpl
import com.android04.capstonedesign.data.dataSource.logDataSource.remote.LogRemoteDataSourceImpl
import com.android04.capstonedesign.data.dataSource.loginDataSource.local.LoginLocalDataSourceImpl
import com.android04.capstonedesign.data.dataSource.loginDataSource.remote.LoginRemoteDataSourceImpl
import com.android04.capstonedesign.data.dataSource.pointDataSource.PointRemoteDataSourceImpl
import com.android04.capstonedesign.data.dataSource.productDataSource.ProductDataSource
import com.android04.capstonedesign.data.dataSource.productDataSource.local.ProductLocalDataSourceImpl
import com.android04.capstonedesign.data.dataSource.productDataSource.remote.ProductRemoteDataSourceImpl
import com.android04.capstonedesign.data.repository.LogRepository
import com.android04.capstonedesign.data.repository.LoginRepository
import com.android04.capstonedesign.data.repository.PointRepository
import com.android04.capstonedesign.data.repository.ProductRepository
import com.android04.capstonedesign.util.AppInfoHelper
import com.android04.capstonedesign.util.LocationHelper
import com.android04.capstonedesign.util.SharedPreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

// 앱 각종 의존성 주입

@Module
@InstallIn(SingletonComponent::class)
object AppDiModule {

    @Provides
    fun provideLoginRepository(loginLocalDataSourceImpl: LoginLocalDataSourceImpl, loginRemoteDataSourceImpl: LoginRemoteDataSourceImpl, logLocalDataSourceImpl: LogLocalDataSourceImpl, sharedPreferenceManager: SharedPreferenceManager): LoginRepository = LoginRepository(loginLocalDataSourceImpl, loginRemoteDataSourceImpl, logLocalDataSourceImpl, sharedPreferenceManager)

    @Provides
    fun provideLoginLocalDataSource(sharedPreferenceManager: SharedPreferenceManager): LoginLocalDataSourceImpl = LoginLocalDataSourceImpl(sharedPreferenceManager)

    @Provides
    fun provideLoginRemoteDataSource(): LoginRemoteDataSourceImpl = LoginRemoteDataSourceImpl()

    @Provides
    fun provideLocationRepository(logLocalDataSourceImpl: LogLocalDataSourceImpl, logRemoteDataSourceImpl: LogRemoteDataSourceImpl): LogRepository = LogRepository(logLocalDataSourceImpl, logRemoteDataSourceImpl)

    @Provides
    fun provideProductRepository(localDataSource: ProductDataSource.LocalDataSource, remoteDataSource: ProductDataSource.RemoteDataSource): ProductRepository = ProductRepository(localDataSource, remoteDataSource)

    @Provides
    fun provideProductRemoteDataSource(): ProductDataSource.RemoteDataSource = ProductRemoteDataSourceImpl()

    @Provides
    fun provideProductLocalDataSource(sharedPreferenceManager: SharedPreferenceManager): ProductDataSource.LocalDataSource = ProductLocalDataSourceImpl(sharedPreferenceManager)

    @Provides
    fun providePointRepository(remoteDataSourceImpl: PointRemoteDataSourceImpl): PointRepository = PointRepository(remoteDataSourceImpl)

    @Provides
    fun providePointRemoteDataSource(): PointRemoteDataSourceImpl = PointRemoteDataSourceImpl()

    @Provides
    fun provideLocationHelper(@ApplicationContext context: Context): LocationHelper = LocationHelper(context)

    @Provides
    fun provideAppInfoHelper(@ApplicationContext context: Context): AppInfoHelper = AppInfoHelper(context)

    @Singleton
    @Provides
    fun providesCoroutineScope(
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + ioDispatcher)
    }

}