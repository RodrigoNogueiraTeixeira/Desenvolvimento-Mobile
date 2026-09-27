package com.example.tripplannerbr.data;


@Entity(tableName = "tb_Contatos")
data class Contato {

 val nome: String,
    val telefone: String,
    val email: String
}
