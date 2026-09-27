package com.example.locadora.BancoDados

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VeiculoDao {
    @Query("SELECT * FROM tb_veiculo ORDER BY modelo ASC")
    fun getAll(): Flow<List<Veiculo>>

    @Query("SELECT * FROM tb_veiculo WHERE disponivel = 1 ORDER BY modelo ASC")
    fun getDisponiveis(): Flow<List<Veiculo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(veiculo: Veiculo): Long

    @Update
    suspend fun update(veiculo: Veiculo)

    @Delete
    suspend fun delete(veiculo: Veiculo)
}
