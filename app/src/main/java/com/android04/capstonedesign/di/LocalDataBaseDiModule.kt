package com.android04.capstonedesign.di

import android.content.Context
import androidx.room.Room
import com.android04.capstonedesign.data.dataSource.logDataSource.local.LogLocalDataSourceImpl
import com.android04.capstonedesign.data.dataSource.logDataSource.remote.LogRemoteDataSourceImpl
import com.android04.capstonedesign.data.room.LocalDatabase
import com.android04.capstonedesign.data.room.dao.AppStatsLogDao
import com.android04.capstonedesign.data.room.dao.LocationLogDao
import com.android04.capstonedesign.data.room.dao.PostLogDao
import com.android04.capstonedesign.util.SharedPreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Room DB 의존성 주입

@Module
@InstallIn(SingletonComponent::class)
object LocalDataBaseDiModule {

    @Provides
    fun provideLocationLogDao(localDatabase: LocalDatabase): LocationLogDao {
        return localDatabase.locationLogDao()
    }

    @Provides
    fun provideAppStatsLogDao(localDatabase: LocalDatabase): AppStatsLogDao {
        return localDatabase.appStatsLogDao()
    }

    @Provides
    fun providePostLogDao(localDatabase: LocalDatabase): PostLogDao {
        return localDatabase.postLogDao()
    }

    @Provides
    fun providesLogLocalDataSource(locationLogDao: LocationLogDao, appStatsLogDao: AppStatsLogDao, postLogDao: PostLogDao, sharedPreferenceManager: SharedPreferenceManager): LogLocalDataSourceImpl = LogLocalDataSourceImpl(locationLogDao, appStatsLogDao, postLogDao, sharedPreferenceManager)

    @Provides
    fun providesLogRemoteDataSource(): LogRemoteDataSourceImpl = LogRemoteDataSourceImpl()

    @Provides
    @Singleton
    fun providesLocalDatabase(@ApplicationContext context: Context): LocalDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LocalDatabase::class.java,
            "local_database"
        ).build()
    }
}