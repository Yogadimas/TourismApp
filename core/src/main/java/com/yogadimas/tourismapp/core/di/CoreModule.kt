package com.yogadimas.tourismapp.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.yogadimas.tourismapp.core.BuildConfig.*
import com.yogadimas.tourismapp.core.data.TourismRepository
import com.yogadimas.tourismapp.core.data.auth.AuthRepository
import com.yogadimas.tourismapp.core.data.auth.biometric.BiometricAuthManager
import com.yogadimas.tourismapp.core.data.auth.datastore.AuthPreferences
import com.yogadimas.tourismapp.core.data.auth.datastore.authDataStore
import com.yogadimas.tourismapp.core.data.source.local.LocalDataSource
import com.yogadimas.tourismapp.core.data.source.local.room.TourismDatabase
import com.yogadimas.tourismapp.core.data.source.remote.RemoteDataSource
import com.yogadimas.tourismapp.core.data.source.remote.network.ApiService
import com.yogadimas.tourismapp.core.domain.repository.ITourismRepository
import com.yogadimas.tourismapp.core.domain.repository.auth.IAuthRepository
import com.yogadimas.tourismapp.core.utils.AppExecutors
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val databaseModule = module {
    factory { get<TourismDatabase>().tourismDao() }
    single {
        val passphrase: ByteArray = SQLiteDatabase.getBytes("dicoding".toCharArray())
        val factory = SupportFactory(passphrase)
        Room.databaseBuilder(
            androidContext(),
            TourismDatabase::class.java, "Tourism.db"
        ).fallbackToDestructiveMigration()
            .openHelperFactory(factory)
            .build()
    }
}

val networkModule = module {
    single {
        val hostname = BASE_URL_HOST
        val certificatePinning = CertificatePinner.Builder()
            .add(hostname, CERT_SHA_256_1)
            .add(hostname, CERT_SHA_256_2)
            .add(hostname, CERT_SHA_256_3)
            .build()
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .certificatePinner(certificatePinning)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val authModule = module {
    single { BiometricAuthManager(get()) }
    single<DataStore<Preferences>> { get<Context>().authDataStore }
    single { AuthPreferences(get()) }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    single { RemoteDataSource(get()) }
    factory { AppExecutors() }
    single<ITourismRepository> { TourismRepository(get(), get(), get()) }
    single<IAuthRepository> { AuthRepository(get(), get(), get()) }
}



