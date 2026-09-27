package com.example.locadora.BancoDados

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Cliente::class, Veiculo::class, Locacao::class],
    version = 1,
    exportSchema = false
)
abstract class BancoDados : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun veiculoDao(): VeiculoDao
    abstract fun locacaoDao(): LocacaoDao

    companion object {
        @Volatile
        private var INSTANCE: BancoDados? = null

        fun getDatabase(context: Context): BancoDados {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BancoDados::class.java,
                    "locadora_db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}