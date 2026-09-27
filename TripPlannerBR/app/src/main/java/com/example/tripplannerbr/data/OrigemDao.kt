package com.example.tripplannerbr.data

class OrigemDao {
    @Upsert
    suspend fun inserir(origiem: Origem)

}