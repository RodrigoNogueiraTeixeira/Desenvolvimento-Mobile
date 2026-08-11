import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

// Guarda a sessao do cliente logado
var clienteLogado: Cliente? = null

fun main() {
    println("==========================================")
    println("        SISTEMA DE DELIVERY - CLIENTE     ")
    println("==========================================")

    var rodando = true
    while (rodando) {
        println("\n--- MENU DE ACESSO ---")
        println("[1] Entrar")
        println("[2] Novo Cadastro")
        println("[0] Sair")
        print("Opcao: ")

        val opcao = (readlnOrNull() ?: "").trim()
        if (opcao == "1") {
            entrarCliente()
            if (clienteLogado != null) {
                exibirMenuPrincipalCliente()
            }
        } else if (opcao == "2") {
            cadastrarCliente()
            if (clienteLogado != null) {
                exibirMenuPrincipalCliente()
            }
        } else if (opcao == "0") {
            println("Saindo do aplicativo do Cliente... Ate mais!")
            rodando = false
        } else {
            println("Opcao invalida!")
        }
    }
}

// ---------------------------------------------------------
// [4.1] ACESSO E AUTENTICACAO DO CLIENTE
// ---------------------------------------------------------

fun entrarCliente() {
    print("\nDigite seu Telefone: ")
    val telefoneBusca = (readlnOrNull() ?: "").trim()

    if (telefoneBusca.isEmpty()) {
        println("Erro: Telefone nao pode ser vazio!")
        return
    }

    val listaClientes = carregarListaClientes()
    var encontrado: Cliente? = null

    for (c in listaClientes) {
        if (c.telefone == telefoneBusca) {
            encontrado = c
            break
        }
    }

    if (encontrado != null) {
        clienteLogado = encontrado
        println("\n>>> Login realizado com sucesso! Ola, ${encontrado.nome}! <<<")
    } else {
        println("\nErro: Cliente com telefone '$telefoneBusca' nao foi encontrado em 'clientes.json'!")
    }
}

fun cadastrarCliente() {
    println("\n--- NOVO CADASTRO DE CLIENTE ---")
    print("Nome completo: ")
    val nome = (readlnOrNull() ?: "").trim()

    print("Telefone: ")
    val telefone = (readlnOrNull() ?: "").trim()

    print("Endereco: ")
    val endereco = (readlnOrNull() ?: "").trim()

    if (nome.isEmpty() || telefone.isEmpty() || endereco.isEmpty()) {
        println("Erro: Todos os campos sao obrigatorios!")
        return
    }

    val listaClientes = carregarListaClientes()

    // Validacao de Unicidade: impede o registro se o Telefone ja existir em clientes.json
    for (c in listaClientes) {
        if (c.telefone == telefone) {
            println("Erro: Ja existe um cliente cadastrado com o telefone '$telefone'!")
            return
        }
    }

    val novoCliente = Cliente(nome = nome, telefone = telefone, endereco = endereco)
    listaClientes.add(novoCliente)

    salvarListaClientes(listaClientes)
    clienteLogado = novoCliente
    println("\n>>> Cadastro realizado com sucesso em 'clientes.json'! <<<")
}

// ---------------------------------------------------------
// [4.2] MENU PRINCIPAL DO CLIENTE
// ---------------------------------------------------------

fun exibirMenuPrincipalCliente() {
    var emSessao = true
    while (emSessao) {
        val cli = clienteLogado ?: break
        println("\n==========================================")
        println(" MENU CLIENTE: ${cli.nome}")
        println("==========================================")
        println("[1] Realizar Novo Pedido")
        println("[2] Ver Pedidos em Andamento")
        println("[3] Ver Pedidos Finalizados")
        println("[0] Sair (Logout)")
        print("Opcao: ")

        val opcao = (readlnOrNull() ?: "").trim()
        if (opcao == "1") {
            realizarNovoPedido()
        } else if (opcao == "2") {
            verPedidosEmAndamento()
        } else if (opcao == "3") {
            verPedidosFinalizados()
        } else if (opcao == "0") {
            clienteLogado = null
            emSessao = false
            println("Logout efetuado.")
        } else {
            println("Opcao invalida!")
        }
    }
}

// Opcao 1: Realizar Novo Pedido
fun realizarNovoPedido() {
    val cli = clienteLogado ?: return

    val restaurantes = carregarTodosRestaurantes()

    if (restaurantes.isEmpty()) {
        println("\nNenhum restaurante cadastrado no momento!")
        return
    }

    println("\n=== RESTAURANTES DISPONIVEIS ===")
    for (i in 0 until restaurantes.size) {
        val r = restaurantes[i]
        println("[${i + 1}] ${r.nome} - Endereco: ${r.endereco}")
    }

    print("\nEscolha o numero do restaurante desejado: ")
    val strOpcao = (readlnOrNull() ?: "").trim()
    val idx = strOpcao.toIntOrNull()

    if (idx == null || idx < 1 || idx > restaurantes.size) {
        println("Selecao invalida!")
        return
    }

    val restauranteEscolhido = restaurantes[idx - 1]

    if (restauranteEscolhido.menu.isEmpty()) {
        println("\nO restaurante '${restauranteEscolhido.nome}' nao possui itens no cardapio!")
        return
    }

    println("\n=== CARDAPIO DE: ${restauranteEscolhido.nome} ===")
    for (item in restauranteEscolhido.menu) {
        println("Item #${item.numero_item} | ${item.descricao} - R$ %.2f".format(item.preco))
    }

    val itensSelecionados = ArrayList<LinhaPedido>()
    println("\n--- SELECAO DE ITENS ---")
    println("(Pressione Enter no 'Numero do Item' para finalizar a selecao)")

    var selecionando = true
    while (selecionando) {
        print("\nNumero do Item (ou Enter para encerrar): ")
        val strNum = (readlnOrNull() ?: "").trim()

        if (strNum.isEmpty()) {
            selecionando = false
        } else {
            val numItem = strNum.toIntOrNull()
            if (numItem == null) {
                println("Numero de item invalido!")
            } else {
                var itemEncontrado: ItemMenu? = null
                for (im in restauranteEscolhido.menu) {
                    if (im.numero_item == numItem) {
                        itemEncontrado = im
                        break
                    }
                }

                if (itemEncontrado == null) {
                    println("Item #$numItem nao pertence ao cardapio deste restaurante!")
                } else {
                    print("Quantidade: ")
                    val strQtd = (readlnOrNull() ?: "").trim()
                    val qtd = strQtd.toIntOrNull()

                    if (qtd == null || qtd <= 0) {
                        println("Quantidade invalida!")
                    } else {
                        val totalItem = itemEncontrado.preco * qtd
                        val lp = LinhaPedido(
                            emailRestaurante = restauranteEscolhido.email,
                            nomeRestaurante = restauranteEscolhido.nome,
                            telefoneCliente = cli.telefone,
                            nomeCliente = cli.nome,
                            enderecoCliente = cli.endereco,
                            numeroItem = itemEncontrado.numero_item,
                            quantidade = qtd,
                            descricaoItem = itemEncontrado.descricao,
                            valorUnitario = itemEncontrado.preco,
                            valorTotalItem = totalItem,
                            status = 0
                        )
                        itensSelecionados.add(lp)
                        println("Adicionado: ${qtd}x '${itemEncontrado.descricao}' (Total: R$ %.2f)".format(totalItem))
                    }
                }
            }
        }
    }

    if (itensSelecionados.isEmpty()) {
        println("\nNenhum item foi selecionado. Pedido cancelado.")
        return
    }

    var valorTotalPedido = 0.0
    println("\n==========================================")
    println("          RESUMO DO SEU PEDIDO            ")
    println("==========================================")
    println("Restaurante: ${restauranteEscolhido.nome}")
    println("Cliente: ${cli.nome} (${cli.telefone})")
    println("Endereco de Entrega: ${cli.endereco}")
    println("------------------------------------------")

    for (ip in itensSelecionados) {
        println("${ip.quantidade}x [Item #${ip.numeroItem}] ${ip.descricaoItem} (R$ %.2f un) = R$ %.2f".format(ip.valorUnitario, ip.valorTotalItem))
        valorTotalPedido += ip.valorTotalItem
    }
    println("------------------------------------------")
    println("VALOR TOTAL DO PEDIDO: R$ %.2f".format(valorTotalPedido))
    println("==========================================")

    print("\nConfirmar pedido? [S/N]: ")
    val respConfirma = (readlnOrNull() ?: "").trim().uppercase()

    if (respConfirma == "S") {
        val arquivoCsv = File("pedidos.csv")
        val proximoIdPedido = calcularProximoIdPedido(arquivoCsv)
        val dataHoraAtual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

        for (lp in itensSelecionados) {
            lp.idPedido = proximoIdPedido
            lp.dataHora = dataHoraAtual
        }

        salvarPedidosNoCsv(arquivoCsv, itensSelecionados)
        println("\n>>> PEDIDO #${proximoIdPedido} REALIZADO COM SUCESSO! <<<")
        println("Status inicial: 0 - SOLICITADO")
    } else {
        println("\nPedido cancelado pelo cliente.")
    }
}

// Opcao 2: Ver Pedidos em Andamento (status 0, 1, 2 ou 3)
fun verPedidosEmAndamento() {
    val cli = clienteLogado ?: return
    val arquivoCsv = File("pedidos.csv")

    if (!arquivoCsv.exists()) {
        println("\nNenhum pedido realizado ainda.")
        return
    }

    val todosPedidos = LinhaPedido.lerTodosPedidosDoCsv(arquivoCsv)
    val pedidosEmAndamento = ArrayList<LinhaPedido>()

    for (p in todosPedidos) {
        if (p.telefoneCliente == cli.telefone && p.status < 4) {
            pedidosEmAndamento.add(p)
        }
    }

    if (pedidosEmAndamento.isEmpty()) {
        println("\nVoce nao possui pedidos em andamento.")
        return
    }

    println("\n==========================================")
    println("          PEDIDOS EM ANDAMENTO            ")
    println("==========================================")

    for (p in pedidosEmAndamento) {
        println("\nPedido #${p.idPedido} | Data: ${p.dataHora} | Restaurante: ${p.nomeRestaurante}")
        println("Item: [${p.numeroItem}] ${p.descricaoItem} | Qtd: ${p.quantidade} | Total: R$ %.2f".format(p.valorTotalItem))
        println("Status Atual: ${LinhaPedido.obterNomeStatus(p.status)}")
        println("------------------------------------------")
    }
}

// Opcao 3: Ver Pedidos Finalizados (status 4)
fun verPedidosFinalizados() {
    val cli = clienteLogado ?: return
    val arquivoCsv = File("pedidos.csv")

    if (!arquivoCsv.exists()) {
        println("\nNenhum pedido realizado ainda.")
        return
    }

    val todosPedidos = LinhaPedido.lerTodosPedidosDoCsv(arquivoCsv)
    val pedidosFinalizados = ArrayList<LinhaPedido>()

    for (p in todosPedidos) {
        if (p.telefoneCliente == cli.telefone && p.status == 4) {
            pedidosFinalizados.add(p)
        }
    }

    if (pedidosFinalizados.isEmpty()) {
        println("\nVoce nao possui pedidos finalizados.")
        return
    }

    println("\n==========================================")
    println("          PEDIDOS FINALIZADOS (ENTREGUES) ")
    println("==========================================")

    for (p in pedidosFinalizados) {
        println("\nPedido #${p.idPedido} | Data: ${p.dataHora} | Restaurante: ${p.nomeRestaurante}")
        println("Item: [${p.numeroItem}] ${p.descricaoItem} | Qtd: ${p.quantidade} | Total: R$ %.2f".format(p.valorTotalItem))
        println("Status: ${LinhaPedido.obterNomeStatus(p.status)}")
        println("------------------------------------------")
    }
}

// ---------------------------------------------------------
// FUNCOES AUXILIARES DO CLIENTE
// ---------------------------------------------------------

fun carregarListaClientes(): ArrayList<Cliente> {
    val arquivo = File("clientes.json")
    if (!arquivo.exists()) return ArrayList()
    try {
        val json = arquivo.readText()
        val tipoLista = object : TypeToken<ArrayList<Cliente>>() {}.type
        val lista: ArrayList<Cliente>? = gson.fromJson(json, tipoLista)
        return lista ?: ArrayList()
    } catch (e: Exception) {
        return ArrayList()
    }
}

fun salvarListaClientes(lista: ArrayList<Cliente>) {
    val json = gson.toJson(lista)
    File("clientes.json").writeText(json)
}

fun carregarTodosRestaurantes(): ArrayList<Restaurante> {
    val lista = ArrayList<Restaurante>()
    val arquivos = File(".").listFiles { _, nome ->
        nome.startsWith("restaurante_") && nome.endsWith(".json")
    }
    if (arquivos != null) {
        for (f in arquivos) {
            try {
                val json = f.readText()
                val r = gson.fromJson(json, Restaurante::class.java)
                if (r != null) {
                    val idStr = f.name.replace("restaurante_", "").replace(".json", "")
                    r.id = idStr.toIntOrNull() ?: 0
                    lista.add(r)
                }
            } catch (e: Exception) {
                // ignora
            }
        }
    }
    return lista
}

fun calcularProximoIdPedido(arquivoCsv: File): Int {
    if (!arquivoCsv.exists()) return 1
    val pedidos = LinhaPedido.lerTodosPedidosDoCsv(arquivoCsv)
    var maxId = 0
    for (p in pedidos) {
        if (p.idPedido > maxId) {
            maxId = p.idPedido
        }
    }
    return maxId + 1
}

fun salvarPedidosNoCsv(arquivoCsv: File, novosPedidos: ArrayList<LinhaPedido>) {
    val precisaCabecalho = !arquivoCsv.exists() || arquivoCsv.length() == 0L
    val sb = StringBuilder()

    if (precisaCabecalho) {
        sb.append(LinhaPedido.CABECALHO_CSV).append("\n")
    }

    for (p in novosPedidos) {
        sb.append(p.paraLinhaCsv()).append("\n")
    }

    arquivoCsv.appendText(sb.toString())
}
