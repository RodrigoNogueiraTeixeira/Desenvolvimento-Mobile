package com.example.locadora.BancoDados

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LocacaoDao {
    @Query("SELECT * FROM tb_locacao ORDER BY id DESC")
    fun getAll(): Flow<List<Locacao>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(locacao: Locacao)

    @Update
    suspend fun update(locacao: Locacao)

    @Delete
    suspend fun delete(locacao: Locacao)
}
