package com.pedro.solutions.mytravelplanning.di

import androidx.room.Room
import com.pedro.solutions.mytravelplanning.data.database.TravelDatabase
import com.pedro.solutions.mytravelplanning.data.network.ChatGptApi
import com.pedro.solutions.mytravelplanning.data.repository.TravelRepository
import com.pedro.solutions.mytravelplanning.ui.screens.create.CreateTravelViewModel
import com.pedro.solutions.mytravelplanning.ui.screens.detail.TravelDetailViewModel
import com.pedro.solutions.mytravelplanning.ui.screens.generate.GenerateTravelViewModel
import com.pedro.solutions.mytravelplanning.ui.screens.intro.IntroViewModel
import com.pedro.solutions.mytravelplanning.ui.screens.main.MainScreenViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val appModules = module {
    viewModelOf(::GenerateTravelViewModel)
    viewModel { MainScreenViewModel(get()) }
    viewModel { TravelDetailViewModel(get()) }
    viewModelOf(::IntroViewModel)
    viewModelOf(::CreateTravelViewModel)

    single {
        Room.databaseBuilder(androidApplication(), TravelDatabase::class.java, "travel_database")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build() // TODO: Remove in production
    }

    single {
        val travelDatabase = get<TravelDatabase>()
        travelDatabase.travelDao()
    }

    single {
        val BASE_URL = "https://api.openai.com/v1/"

        val httpClient = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        httpClient.addInterceptor(logging)
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
        httpClient.readTimeout(30, TimeUnit.SECONDS)
        httpClient.writeTimeout(30, TimeUnit.SECONDS)

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

    single { TravelRepository(get(), get(), get()) }
}