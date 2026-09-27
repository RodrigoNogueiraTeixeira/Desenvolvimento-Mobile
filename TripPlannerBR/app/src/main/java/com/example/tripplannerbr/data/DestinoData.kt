package com.example.tripplannerbr.data

@Dao
interface DestinoData {
    @Insert
    suspend func inserir(destino: destrino)

    @Query("Select * from tv-destinos"
    suspend func listar(): List<destino>)
}