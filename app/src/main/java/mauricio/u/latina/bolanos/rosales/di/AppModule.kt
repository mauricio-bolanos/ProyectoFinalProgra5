package mauricio.u.latina.bolanos.rosales.di

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mauricio.u.latina.bolanos.rosales.data.repository.AuthRepository
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.TorneosDao
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.PartidasDao
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.ListaJugadoresDao
import mauricio.u.latina.bolanos.rosales.data.repository.TorneosRepository
import mauricio.u.latina.bolanos.rosales.data.repository.PartidasRepository
import mauricio.u.latina.bolanos.rosales.data.repository.ListaJugadoresRepository
import mauricio.u.latina.bolanos.rosales.data.repository.CanalesRepository
import mauricio.u.latina.bolanos.rosales.data.repository.MensajesRepository
import mauricio.u.latina.bolanos.rosales.data.repository.UserRepository
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.CanalesDao
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.MensajesDao
import mauricio.u.latina.bolanos.rosales.data.database.interfaces.UsersDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return Firebase.database.apply {
            // Configuración adicional
            setPersistenceEnabled(true) // Habilita caché offline
        }
    }

    // dependencias relacionadas con Firebase
    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseDatabase
    ): AuthRepository {
        return AuthRepository(database)
    }

    // DAOs
    @Provides
    @Singleton
    fun provideCanalesDao(database: FirebaseDatabase): CanalesDao {
        return CanalesDao(database)
    }

    @Provides
    @Singleton
    fun provideListaJugadoresDao(database: FirebaseDatabase): ListaJugadoresDao {
        return ListaJugadoresDao(database)
    }

    @Provides
    @Singleton
    fun provideMensajesDao(database: FirebaseDatabase): MensajesDao {
        return MensajesDao(database)
    }

    @Provides
    @Singleton
    fun providePartidasDao(database: FirebaseDatabase): PartidasDao {
        return PartidasDao(database)
    }

    @Provides
    @Singleton
    fun provideTorneosDao(database: FirebaseDatabase): TorneosDao {
        return TorneosDao(database)
    }

    @Provides
    @Singleton
    fun provideUsersDao(database: FirebaseDatabase): UsersDao {
        return UsersDao(database)
    }

    // Repositories (opcional, si los estás usando)
    @Provides
    @Singleton
    fun provideCanalesRepository(dao: CanalesDao): CanalesRepository {
        return CanalesRepository(dao)
    }

    @Provides
    @Singleton
    fun provideListaJugadoresRepository(dao: ListaJugadoresDao): ListaJugadoresRepository {
        return ListaJugadoresRepository(dao)
    }

    @Provides
    @Singleton
    fun provideMensajesRepository(dao: MensajesDao): MensajesRepository {
        return MensajesRepository(dao)
    }

    @Provides
    @Singleton
    fun providePartidasRepository(dao: PartidasDao): PartidasRepository {
        return PartidasRepository(dao)
    }

    @Provides
    @Singleton
    fun provideTorneosRepository(dao: TorneosDao): TorneosRepository {
        return TorneosRepository(dao)
    }

    @Provides
    @Singleton
    fun provideUsersRepository(dao: UsersDao): UserRepository {
        return UserRepository(dao)
    }
}