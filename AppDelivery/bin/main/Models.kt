import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.File

// Instancia global do Gson para manipular os arquivos JSON do sistema
val gson: Gson = GsonBuilder().setPrettyPrinting().create()

// Modelo para cada item do cardapio do restaurante
data class ItemMenu(
    @SerializedName("numero_item")
    var numero_item: Int = 0,

    @SerializedName("descricao")
    var descricao: String = "",

    @SerializedName("preco")
    var preco: Double = 0.0
)

// Modelo para os dados do restaurante que ficam no arquivo restaurante_ID.json
data class Restaurante(
    var nome: String = "",
    var email: String = "",
    var endereco: String = "",
    var menu: ArrayList<ItemMenu> = ArrayList()
) {
    // Guarda o ID do arquivo (ex: restaurante_1.json -> id = 1) sem salvar no JSON
    @Transient
    var id: Int = 0
}

// Modelo para os dados do cliente que ficam no arquivo clientes.json
data class Cliente(
    var nome: String = "",
    var telefone: String = "",
    var endereco: String = ""
)

// Modelo para representar cada linha salva no arquivo pedidos.csv
data class LinhaPedido(
    var idPedido: Int = 0,
    var dataHora: String = "",
    var emailRestaurante: String = "",
    var nomeRestaurante: String = "",
    var telefoneCliente: String = "",
    var nomeCliente: String = "",
    var enderecoCliente: String = "",
    var numeroItem: Int = 0,
    var quantidade: Int = 0,
    var descricaoItem: String = "",
    var valorUnitario: Double = 0.0,
    var valorTotalItem: Double = 0.0,
    var status: Int = 0
) {
    // Converte os dados do pedido para a linha no formato CSV oficial
    fun paraLinhaCsv(): String {
        return "$idPedido;$dataHora;$emailRestaurante;$nomeRestaurante;$telefoneCliente;$nomeCliente;$enderecoCliente;$numeroItem;$quantidade;$descricaoItem;%.2f;%.2f;$status".format(
            valorUnitario,
            valorTotalItem
        ).replace(',', '.')
    }

    companion object {
        val CABECALHO_CSV: String = "id_pedido;data_hora;email_restaurante;nome_restaurante;telefone_cliente;nome_cliente;endereco_cliente;numero_item;quantidade;descricao_item;valor_unitario;valor_total_item;status"

        // Converte uma linha texto do CSV para objeto LinhaPedido
        fun criarDaLinhaCsv(linha: String): LinhaPedido? {
            val partes = linha.split(";").toTypedArray()
            if (partes.size < 13) return null
            try {
                val p = LinhaPedido()
                p.idPedido = partes[0].trim().toInt()
                p.dataHora = partes[1].trim()
                p.emailRestaurante = partes[2].trim()
                p.nomeRestaurante = partes[3].trim()
                p.telefoneCliente = partes[4].trim()
                p.nomeCliente = partes[5].trim()
                p.enderecoCliente = partes[6].trim()
                p.numeroItem = partes[7].trim().toInt()
                p.quantidade = partes[8].trim().toInt()
                p.descricaoItem = partes[9].trim()
                p.valorUnitario = partes[10].trim().replace(',', '.').toDouble()
                p.valorTotalItem = partes[11].trim().replace(',', '.').toDouble()
                p.status = partes[12].trim().toInt()
                return p
            } catch (e: Exception) {
                return null
            }
        }

        // Funcao auxiliar para ler todas as linhas do CSV pedidos.csv
        fun lerTodosPedidosDoCsv(arquivo: File): ArrayList<LinhaPedido> {
            val lista = ArrayList<LinhaPedido>()
            if (!arquivo.exists()) return lista

            val linhas = arquivo.readLines()
            for (i in linhas.indices) {
                if (i == 0 && linhas[i].startsWith("id_pedido")) continue
                val t = linhas[i].trim()
                if (t.isNotEmpty()) {
                    val p = criarDaLinhaCsv(t)
                    if (p != null) lista.add(p)
                }
            }
            return lista
        }

        // Retorna a descricao textual formatada do status (0 a 4)
        fun obterNomeStatus(codigoStatus: Int): String {
            return when (codigoStatus) {
                0 -> "0 - SOLICITADO"
                1 -> "1 - EM PREPARACAO"
                2 -> "2 - AGUARDANDO ENTREGADOR"
                3 -> "3 - EM TRANSITO"
                4 -> "4 - ENTREGUE"
                else -> "$codigoStatus - DESCONHECIDO"
            }
        }
    }
}
