import java.io.File

// Guarda o restaurante logado na sessao atual
var restauranteLogado: Restaurante? = null

fun main() {
    println("==========================================")
    println("      SISTEMA DE DELIVERY - RESTAURANTE   ")
    println("==========================================")

    var rodando = true
    while (rodando) {
        println("\n--- MENU DE ACESSO ---")
        println("[1] Entrar como Restaurante Existente")
        println("[2] Novo Cadastro")
        println("[0] Sair")
        print("Opcao: ")

        val entrada = (readlnOrNull() ?: "").trim()
        if (entrada == "1") {
            entrarRestaurante()
            if (restauranteLogado != null) {
                exibirMenuPrincipalRestaurante()
            }
        } else if (entrada == "2") {
            cadastrarRestaurante()
            if (restauranteLogado != null) {
                exibirMenuPrincipalRestaurante()
            }
        } else if (entrada == "0") {
            println("Saindo do aplicativo do Restaurante... Ate logo!")
            rodando = false
        } else {
            println("Opcao invalida! Tente novamente.")
        }
    }
}

// ---------------------------------------------------------
// [3.1] ACESSO E AUTENTICACAO DO RESTAURANTE
// ---------------------------------------------------------

fun entrarRestaurante() {
    print("\nDigite o E-mail do Restaurante: ")
    val emailBusca = (readlnOrNull() ?: "").trim()

    if (emailBusca.isEmpty()) {
        println("Erro: E-mail nao pode ser vazio!")
        return
    }

    val pastaAtual = File(".")
    val arquivosJson = pastaAtual.listFiles { _, nome ->
        nome.startsWith("restaurante_") && nome.endsWith(".json")
    }

    // Se nao houver nenhum arquivo de restaurante cadastrado
    if (arquivosJson == null || arquivosJson.isEmpty()) {
        println("\nErro: Nenhum restaurante cadastrado no sistema ainda! Cadastre um novo restaurante primeiro.")
        return
    }

    var encontrado: Restaurante? = null

    for (arquivo in arquivosJson) {
        try {
            val conteudoJson = arquivo.readText().trim()
            if (conteudoJson.isNotEmpty()) {
                val rest = gson.fromJson(conteudoJson, Restaurante::class.java)
                if (rest != null && rest.email.equals(emailBusca, ignoreCase = true)) {
                    val idString = arquivo.name.replace("restaurante_", "").replace(".json", "")
                    rest.id = idString.toIntOrNull() ?: 1
                    encontrado = rest
                    break
                }
            }
        } catch (e: Exception) {
            // ignora arquivo malformatado
        }
    }

    if (encontrado != null) {
        restauranteLogado = encontrado
        println("\n>>> Login realizado com sucesso! Bem-vindo(a), ${encontrado.nome}! <<<")
    } else {
        println("\nErro: Restaurante com o e-mail '$emailBusca' nao foi encontrado!")
    }
}

fun cadastrarRestaurante() {
    println("\n--- NOVO CADASTRO DE RESTAURANTE ---")
    print("Nome do Restaurante: ")
    val nome = (readlnOrNull() ?: "").trim()

    print("E-mail: ")
    val email = (readlnOrNull() ?: "").trim()

    print("Endereco: ")
    val endereco = (readlnOrNull() ?: "").trim()

    if (nome.isEmpty() || email.isEmpty() || endereco.isEmpty()) {
        println("Erro: Todos os campos (Nome, E-mail e Endereco) sao obrigatorios!")
        return
    }

    // Validacao de Unicidade: impede o registro se o e-mail ja existir
    if (verificarEmailExiste(email)) {
        println("Erro: Ja existe um restaurante cadastrado com o e-mail '$email'!")
        return
    }

    val novoMenu = ArrayList<ItemMenu>()
    println("\n--- CADASTRO DO CARDAPIO INICIAL ---")
    println("(Pressione Enter no 'Numero do Item' para finalizar)")

    var adicionandoItens = true
    while (adicionandoItens) {
        print("\nNumero do Item (ou Enter para encerrar): ")
        val strNumero = (readlnOrNull() ?: "").trim()

        if (strNumero.isEmpty()) {
            adicionandoItens = false
        } else {
            val numItem = strNumero.toIntOrNull()
            if (numItem == null || numItem <= 0) {
                println("Numero invalido!")
            } else {
                print("Descricao do Item: ")
                val desc = (readlnOrNull() ?: "").trim()

                print("Preco (ex: 45.00): ")
                val strPreco = (readlnOrNull() ?: "").trim().replace(',', '.')
                val preco = strPreco.toDoubleOrNull()

                if (desc.isEmpty() || preco == null || preco < 0) {
                    println("Erro nos dados do item! Tente novamente.")
                } else {
                    val item = ItemMenu(numero_item = numItem, descricao = desc, preco = preco)
                    novoMenu.add(item)
                    println("Item '$desc' adicionado ao cardapio!")
                }
            }
        }
    }

    val proximoId = calcularProximoIdRestaurante()
    val novoRestaurante = Restaurante(
        nome = nome,
        email = email,
        endereco = endereco,
        menu = novoMenu
    )
    novoRestaurante.id = proximoId

    salvarRestauranteEmArquivo(novoRestaurante)
    restauranteLogado = novoRestaurante
    println("\n>>> Cadastro realizado com sucesso! Arquivo 'restaurante_$proximoId.json' criado. <<<")
}

// ---------------------------------------------------------
// [3.2] MENU PRINCIPAL DO RESTAURANTE
// ---------------------------------------------------------

fun exibirMenuPrincipalRestaurante() {
    var emSessao = true
    while (emSessao) {
        val rest = restauranteLogado ?: break
        println("\n==========================================")
        println(" MENU RESTAURANTE: ${rest.nome}")
        println("==========================================")
        println("[1] Gerenciar Cardapio")
        println("[2] Visualizar Pedidos por Status")
        println("[3] Alterar Status do Pedido")
        println("[0] Sair (Logout)")
        print("Opcao: ")

        val opcao = (readlnOrNull() ?: "").trim()
        if (opcao == "1") {
            gerenciarCardapio()
        } else if (opcao == "2") {
            visualizarPedidosPorStatus()
        } else if (opcao == "3") {
            alterarStatusPedido()
        } else if (opcao == "0") {
            restauranteLogado = null
            emSessao = false
            println("Logout efetuado.")
        } else {
            println("Opcao invalida!")
        }
    }
}

// Submenu: Gerenciar Cardapio
fun gerenciarCardapio() {
    val rest = restauranteLogado ?: return

    var loopCardapio = true
    while (loopCardapio) {
        println("\n--- GERENCIAR CARDAPIO ---")
        println("[A] Ver Cardapio")
        println("[B] Adicionar Item")
        println("[C] Remover Item")
        println("[V] Voltar")
        print("Opcao: ")

        val subOpcao = (readlnOrNull() ?: "").trim().uppercase()
        if (subOpcao == "A") {
            exibirCardapio(rest)
        } else if (subOpcao == "B") {
            adicionarItemCardapio(rest)
        } else if (subOpcao == "C") {
            removerItemCardapio(rest)
        } else if (subOpcao == "V") {
            loopCardapio = false
        } else {
            println("Opcao invalida!")
        }
    }
}

fun exibirCardapio(rest: Restaurante) {
    println("\n=== CARDAPIO ATUAL ===")
    if (rest.menu.isEmpty()) {
        println("Nenhum item cadastrado no cardapio.")
    } else {
        for (item in rest.menu) {
            println("Item #${item.numero_item} | ${item.descricao} - R$ %.2f".format(item.preco))
        }
    }
}

fun adicionarItemCardapio(rest: Restaurante) {
    println("\n--- ADICIONAR ITEM AO CARDAPIO ---")
    print("Numero do Item: ")
    val strNumero = (readlnOrNull() ?: "").trim()
    val numItem = strNumero.toIntOrNull()

    if (numItem == null || numItem <= 0) {
        println("Numero de item invalido!")
        return
    }

    for (i in rest.menu) {
        if (i.numero_item == numItem) {
            println("Erro: Ja existe um item com o numero $numItem no cardapio!")
            return
        }
    }

    print("Descricao do Item: ")
    val desc = (readlnOrNull() ?: "").trim()

    print("Preco (ex: 25.50): ")
    val strPreco = (readlnOrNull() ?: "").trim().replace(',', '.')
    val preco = strPreco.toDoubleOrNull()

    if (desc.isEmpty() || preco == null || preco < 0) {
        println("Erro nos dados digitados!")
        return
    }

    val novoItem = ItemMenu(numero_item = numItem, descricao = desc, preco = preco)
    rest.menu.add(novoItem)

    salvarRestauranteEmArquivo(rest)
    println("Item adicionado e cardapio atualizado com sucesso!")
}

fun removerItemCardapio(rest: Restaurante) {
    println("\n--- REMOVER ITEM DO CARDAPIO ---")
    exibirCardapio(rest)
    print("Digite o Numero do Item que deseja remover: ")
    val strNumero = (readlnOrNull() ?: "").trim()
    val numItem = strNumero.toIntOrNull()

    if (numItem == null) {
        println("Numero invalido!")
        return
    }

    var removido = false
    val iterator = rest.menu.iterator()
    while (iterator.hasNext()) {
        val item = iterator.next()
        if (item.numero_item == numItem) {
            iterator.remove()
            removido = true
            break
        }
    }

    if (removido) {
        salvarRestauranteEmArquivo(rest)
        println("Item #$numItem removido com sucesso!")
    } else {
        println("Item com o numero #$numItem nao foi encontrado no cardapio!")
    }
}

// Opcao 2: Visualizar Pedidos por Status
fun visualizarPedidosPorStatus() {
    val rest = restauranteLogado ?: return
    val arquivoCsv = File("pedidos.csv")

    if (!arquivoCsv.exists()) {
        println("\nNenhum pedido cadastrado no sistema ainda (pedidos.csv nao existe).")
        return
    }

    val todosPedidos = LinhaPedido.lerTodosPedidosDoCsv(arquivoCsv)
    val meusPedidos = ArrayList<LinhaPedido>()
    for (p in todosPedidos) {
        if (p.emailRestaurante.equals(rest.email, ignoreCase = true)) {
            meusPedidos.add(p)
        }
    }

    if (meusPedidos.isEmpty()) {
        println("\nNenhum pedido encontrado para o seu restaurante (${rest.email}).")
        return
    }

    println("\n========================================================")
    println("          PEDIDOS DO RESTAURANTE POR STATUS             ")
    println("========================================================")

    for (status in 0..4) {
        val nomeStatus = LinhaPedido.obterNomeStatus(status)
        println("\n>>> STATUS: $nomeStatus <<<")

        var contador = 0
        for (ped in meusPedidos) {
            if (ped.status == status) {
                println("Pedido ID: #${ped.idPedido} | Data: ${ped.dataHora} | Cliente: ${ped.nomeCliente} (${ped.telefoneCliente}) | Endereco: ${ped.enderecoCliente}")
                println("  Item: [${ped.numeroItem}] ${ped.descricaoItem} | Qtd: ${ped.quantidade} | Unit: R$ %.2f | Total: R$ %.2f".format(ped.valorUnitario, ped.valorTotalItem))
                contador++
            }
        }

        if (contador == 0) {
            println("  (Nenhum pedido com este status)")
        }
    }
}

// Opcao 3: Alterar Status do Pedido
fun alterarStatusPedido() {
    val rest = restauranteLogado ?: return
    val arquivoCsv = File("pedidos.csv")

    if (!arquivoCsv.exists()) {
        println("\nNenhum pedido cadastrado no sistema!")
        return
    }

    print("\nDigite o ID do Pedido que deseja alterar: ")
    val strId = (readlnOrNull() ?: "").trim()
    val idPedido = strId.toIntOrNull()

    if (idPedido == null) {
        println("ID de pedido invalido!")
        return
    }

    println("\nCodigos de Status do Pedido:")
    println("0 - SOLICITADO")
    println("1 - EM PREPARACAO")
    println("2 - AGUARDANDO ENTREGADOR")
    println("3 - EM TRANSITO")
    println("4 - ENTREGUE")
    print("Digite o novo codigo de status (0 a 4): ")

    val strNovoStatus = (readlnOrNull() ?: "").trim()
    val novoStatus = strNovoStatus.toIntOrNull()

    if (novoStatus == null || novoStatus !in 0..4) {
        println("Codigo de status invalido! Deve ser um numero entre 0 e 4.")
        return
    }

    val todosPedidos = LinhaPedido.lerTodosPedidosDoCsv(arquivoCsv)
    var alterado = false

    for (p in todosPedidos) {
        if (p.idPedido == idPedido && p.emailRestaurante.equals(rest.email, ignoreCase = true)) {
            p.status = novoStatus
            alterado = true
        }
    }

    if (alterado) {
        reescreverArquivoCsv(arquivoCsv, todosPedidos)
        println("Status do pedido #$idPedido alterado para ${LinhaPedido.obterNomeStatus(novoStatus)} com sucesso!")
    } else {
        println("Pedido #$idPedido nao encontrado para o seu restaurante!")
    }
}

// ---------------------------------------------------------
// FUNCOES AUXILIARES DE ARQUIVO
// ---------------------------------------------------------

fun verificarEmailExiste(email: String): Boolean {
    val arquivos = File(".").listFiles { _, nome ->
        nome.startsWith("restaurante_") && nome.endsWith(".json")
    }
    if (arquivos != null) {
        for (f in arquivos) {
            try {
                val rest = gson.fromJson(f.readText(), Restaurante::class.java)
                if (rest != null && rest.email.equals(email, ignoreCase = true)) {
                    return true
                }
            } catch (e: Exception) {
                // ignora
            }
        }
    }
    return false
}

fun calcularProximoIdRestaurante(): Int {
    var maxId = 0
    val arquivos = File(".").listFiles { _, nome ->
        nome.startsWith("restaurante_") && nome.endsWith(".json")
    }
    if (arquivos != null) {
        for (f in arquivos) {
            val numStr = f.name.replace("restaurante_", "").replace(".json", "")
            val id = numStr.toIntOrNull()
            if (id != null && id > maxId) {
                maxId = id
            }
        }
    }
    return maxId + 1
}

fun salvarRestauranteEmArquivo(restaurante: Restaurante) {
    val nomeArquivo = "restaurante_${restaurante.id}.json"
    val jsonString = gson.toJson(restaurante)
    File(nomeArquivo).writeText(jsonString)
}

fun reescreverArquivoCsv(arquivo: File, pedidos: ArrayList<LinhaPedido>) {
    val sb = StringBuilder()
    sb.append(LinhaPedido.CABECALHO_CSV).append("\n")
    for (p in pedidos) {
        sb.append(p.paraLinhaCsv()).append("\n")
    }
    arquivo.writeText(sb.toString())
}
