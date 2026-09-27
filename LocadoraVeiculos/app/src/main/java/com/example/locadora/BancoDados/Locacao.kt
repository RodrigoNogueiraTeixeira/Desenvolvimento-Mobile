package com.example.locadora.BancoDados

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_locacao")
data class Locacao(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clienteId: Int,
    val clienteNome: String,
    val veiculoId: Int,
    val veiculoModelo: String,
    val veiculoPlaca: String,
    val dataLocacao: String,
    val dias: Int,
    val valorTotal: Double,
    val ativa: Boolean = true
)
