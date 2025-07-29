package com.pedro.solutions.mytravelplanning.di

import com.pedro.solutions.mytravelplanning.data.network.ChatGptApi
import com.pedro.solutions.mytravelplanning.data.repository.TravelsRepository
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelViewModel
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreenViewModel
import com.pedro.solutions.mytravelplanning.ui.screens.intro.IntroViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val appModules = module {
    viewModelOf(::CreateTravelViewModel)
    viewModelOf(::MainScreenViewModel)
    viewModelOf(::IntroViewModel)

    single {
        val BASE_URL = "https://api.openai.com/v1/"

        val httpClient = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        httpClient.addInterceptor(logging)

        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .build()
    }

    single<ChatGptApi> {
        get<Retrofit>().create(ChatGptApi::class.java)
    }

    single { TravelsRepository(get(), get()) }
}