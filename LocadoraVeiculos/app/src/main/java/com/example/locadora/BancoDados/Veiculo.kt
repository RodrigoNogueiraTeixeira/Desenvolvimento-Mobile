package com.example.locadora.BancoDados

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_veiculo")
data class Veiculo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val modelo: String,
    val placa: String,
    val ano: String,
    val valorDiaria: Double,
    val disponivel: Boolean = true
)
