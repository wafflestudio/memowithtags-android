package com.example.memowithtags.common.network.di

import android.content.Context
import android.content.SharedPreferences
import com.example.memowithtags.common.network.api.AuthApi
import com.example.memowithtags.common.network.api.MemoApi
import com.example.memowithtags.common.network.api.TagApi
import com.example.memowithtags.common.network.interceptor.AuthInterceptor
import com.example.memowithtags.common.network.interceptor.TokenAuthenticator
import com.example.memowithtags.common.network.token.SharedPrefsTokenProvider
import com.example.memowithtags.common.network.token.TokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private const val BASE_URL = "https://memowithtags.kro.kr/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideTokenInterceptor(tokenProvider: TokenProvider): AuthInterceptor {
        return AuthInterceptor(tokenProvider)
    }

    @Provides
    @Singleton
    fun provideTokenProvider(
        sharedPreferences: SharedPreferences
    ): TokenProvider {
        return SharedPrefsTokenProvider(sharedPreferences)
    }

    @Provides
    fun provideTokenAuthenticator(
        tokenProvider: TokenProvider,
        authApi: AuthApi,
        @ApplicationContext context: Context
    ): TokenAuthenticator {
        return TokenAuthenticator(tokenProvider, authApi, context)
    }

    @Provides
    @Singleton
    @Named("NoAuth")
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("WithAuth")
    fun provideOkHttpClientWithAuth(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("NoAuth")
    fun provideRetrofit(@Named("NoAuth") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("WithAuth")
    fun provideRetrofitWithAuth(@Named("WithAuth") okHttpClientWithAuth: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClientWithAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provides API
    @Provides
    @Singleton
    fun provideAuthApi(@Named("NoAuth") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideMemoApi(@Named("WithAuth") retrofitWithAuth: Retrofit): MemoApi =
        retrofitWithAuth.create(MemoApi::class.java)

    @Provides
    @Singleton
    fun provideTagApi(@Named("WithAuth") retrofitWithAuth: Retrofit): TagApi =
        retrofitWithAuth.create(TagApi::class.java)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("MemoWithTagsPrefs", Context.MODE_PRIVATE)
    }
}
