package com.nadim.tasknova.di

import com.nadim.tasknova.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
            install(Storage)
            install(Functions)
        }
    }

    @Provides
    @Singleton
    fun provideAuth(client: SupabaseClient): Auth =
        client.pluginManager.getPlugin(Auth)

    @Provides
    @Singleton
    fun providePostgrest(client: SupabaseClient): Postgrest =
        client.pluginManager.getPlugin(Postgrest)

    @Provides
    @Singleton
    fun provideStorage(client: SupabaseClient): Storage =
        client.pluginManager.getPlugin(Storage)

    @Provides
    @Singleton
    fun provideFunctions(client: SupabaseClient): Functions =
        client.pluginManager.getPlugin(Functions)
}