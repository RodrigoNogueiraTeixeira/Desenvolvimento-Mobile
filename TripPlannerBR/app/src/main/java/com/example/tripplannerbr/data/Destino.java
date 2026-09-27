package com.example.tripplannerbr.data;

enum class StatusViagem {

    PENDENTE,
    EXECUTANDO,
    REALIZADA
}



@Entity(tableName = "tb-destinos")
data class Destino(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val ibgeId: Int,
        val uf: String,
        val cidade: String,
        val cep: ULong,
        val distanciaKm: ULong,
        val orcamento: Double,
        val dataPrevista: String,
        val dataRealizada: Date,
        val status: StatusViagem

        );
)
