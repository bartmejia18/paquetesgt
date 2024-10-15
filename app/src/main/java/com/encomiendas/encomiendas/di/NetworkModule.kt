package com.encomiendas.encomiendas.di

import com.encomiendas.encomiendas.BuildConfig
import com.encomiendas.encomiendas.data.api.ApiTrackingHelper
import com.encomiendas.encomiendas.data.api.ApiTrackingHelperImpl
import com.encomiendas.encomiendas.data.services.TrackingService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        )
        .client(okHttpClient)
        .build()

    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        client.addNetworkInterceptor(interceptor)
        return client.build()
    }

    @Provides
    fun providesTrackingService(retrofit: Retrofit): TrackingService =
        retrofit.create(TrackingService::class.java)

    @Provides
    fun providesApiTrackingHelper(apiTrackingHelperImpl: ApiTrackingHelperImpl): ApiTrackingHelper =
        apiTrackingHelperImpl

}