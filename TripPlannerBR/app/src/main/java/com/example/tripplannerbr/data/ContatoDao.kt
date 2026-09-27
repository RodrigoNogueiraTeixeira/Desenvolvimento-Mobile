package com.example.tripplannerbr.data

import retrofit2.http.DELETE

@Dao
interface ContatoDao {
    @Insert
    suspend fun inserir(contato: Contato)
    @Query("Select * from tb_contatos")
    suspend func listar(): list<Contato>
    @DELETEsuspend func excluir(contato : Contato)
    @Update
    suspend
}