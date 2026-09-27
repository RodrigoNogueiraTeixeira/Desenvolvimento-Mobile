package com.example.tripplannerbr.data;

@Entity(tablename = "tb_origens")
@Table
data class Origem(val ibgeId: Int, val uf: String, val cidade: String, val cep: ULongl);
