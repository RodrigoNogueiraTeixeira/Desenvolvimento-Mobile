package com.example.locadora.BancoDados

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_cliente")
data class Cliente(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    val nome: String,
    val cpf: String,
    val telefone: String
)