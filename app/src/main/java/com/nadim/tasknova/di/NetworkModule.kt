package com.nadim.tasknova.di

import com.nadim.tasknova.BuildConfig
import com.nadim.tasknova.data.remote.GmailApi
import com.nadim.tasknova.data.remote.OpenAIApi
import com.nadim.tasknova.data.remote.WhisperApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    @Singleton
    @Named("openai")
    fun provideOpenAIRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("gmail")
    fun provideGmailRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://gmail.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenAIApi(
        @Named("openai") retrofit: Retrofit
    ): OpenAIApi = retrofit.create(OpenAIApi::class.java)

    @Provides
    @Singleton
    fun provideWhisperApi(
        @Named("openai") retrofit: Retrofit
    ): WhisperApi = retrofit.create(WhisperApi::class.java)

    @Provides
    @Singleton
    fun provideGmailApi(
        @Named("gmail") retrofit: Retrofit
    ): GmailApi = retrofit.create(GmailApi::class.java)

    @Provides
    @Singleton
    fun provideGson(): com.google.gson.Gson = com.google.gson.Gson()
}