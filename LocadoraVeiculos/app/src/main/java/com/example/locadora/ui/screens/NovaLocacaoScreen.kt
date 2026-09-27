package com.example.locadora.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.locadora.BancoDados.Cliente
import com.example.locadora.BancoDados.Veiculo
import com.example.locadora.ui.LocadoraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NovaLocacaoScreen(
    viewModel: LocadoraViewModel,
    onLocacaoRealizada: () -> Unit
) {
    val clientes by viewModel.clientes.collectAsState()
    val veiculosDisponiveis by viewModel.veiculosDisponiveis.collectAsState()

    var clienteSelecionado by remember { mutableStateOf<Cliente?>(null) }
    var veiculoSelecionado by remember { mutableStateOf<Veiculo?>(null) }

    var mostrarDialogSelecionarCliente by remember { mutableStateOf(false) }
    var mostrarDialogSelecionarVeiculo by remember { mutableStateOf(false) }

    var mostrarDialogNovoCliente by remember { mutableStateOf(false) }
    var mostrarDialogNovoVeiculo by remember { mutableStateOf(false) }

    val dataAtual = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }
    var dataLocacao by remember { mutableStateOf(dataAtual) }
    var diasText by remember { mutableStateOf("1") }
    var mensagemSucesso by remember { mutableStateOf(false) }
    var mensagemErro by remember { mutableStateOf<String?>(null) }

    val dias = diasText.toIntOrNull() ?: 1
    val totalEstimado = (veiculoSelecionado?.valorDiaria ?: 0.0) * dias

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Registrar Nova Locação",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Seção 1: Cliente
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "1. Cliente:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                FilledTonalButton(
                    onClick = { mostrarDialogNovoCliente = true },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Cadastrar Cliente",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Novo Cliente", fontSize = 12.sp)
                }
            }

            CampoSelecao(
                label = "Cliente",
                tituloItem = clienteSelecionado?.nome,
                subtituloItem = clienteSelecionado?.let { "CPF: ${it.cpf} • Tel: ${it.telefone}" },
                icone = Icons.Default.Person,
                placeholder = "Toque para escolher o cliente...",
                onClick = { mostrarDialogSelecionarCliente = true }
            )
        }

        // Seção 2: Veículo Disponível
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "2. Veículo Disponível:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                FilledTonalButton(
                    onClick = { mostrarDialogNovoVeiculo = true },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Cadastrar Veículo",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Novo Veículo", fontSize = 12.sp)
                }
            }

            CampoSelecao(
                label = "Veículo",
                tituloItem = veiculoSelecionado?.modelo,
                subtituloItem = veiculoSelecionado?.let {
                    "Placa: ${it.placa} • Diária: R$ ${String.format(Locale.getDefault(), "%.2f", it.valorDiaria)}"
                },
                icone = Icons.Default.DirectionsCar,
                placeholder = "Toque para escolher o veículo...",
                onClick = { mostrarDialogSelecionarVeiculo = true }
            )
        }

        // Data da Locação
        OutlinedTextField(
            value = dataLocacao,
            onValueChange = { dataLocacao = it },
            label = { Text("Data de Início") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Quantidade de Dias
        OutlinedTextField(
            value = diasText,
            onValueChange = { if (it.all { char -> char.isDigit() }) diasText = it },
            label = { Text("Quantidade de Dias") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Resumo do Valor
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Resumo da Locação",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Diária: R$ ${String.format(Locale.getDefault(), "%.2f", veiculoSelecionado?.valorDiaria ?: 0.0)}"
                )
                Text(text = "Dias: $dias")
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text(
                    text = "Valor Total: R$ ${String.format(Locale.getDefault(), "%.2f", totalEstimado)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (mensagemErro != null) {
            Text(
                text = mensagemErro ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp
            )
        }

        Button(
            onClick = {
                val cliente = clienteSelecionado
                val veiculo = veiculoSelecionado
                if (cliente == null) {
                    mensagemErro = "Por favor, selecione um cliente."
                    return@Button
                }
                if (veiculo == null) {
                    mensagemErro = "Por favor, selecione um veículo disponível."
                    return@Button
                }
                if (dias <= 0) {
                    mensagemErro = "Informe uma quantidade válida de dias."
                    return@Button
                }

                mensagemErro = null
                viewModel.realizarLocacao(
                    cliente = cliente,
                    veiculo = veiculo,
                    data = dataLocacao,
                    dias = dias,
                    onSucesso = {
                        mensagemSucesso = true
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Confirmar Locação", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Dialog para selecionar cliente
    if (mostrarDialogSelecionarCliente) {
        DialogSelecionarCliente(
            clientes = clientes,
            clienteSelecionado = clienteSelecionado,
            onClienteSelecionado = { cliente ->
                clienteSelecionado = cliente
            },
            onNovoClienteClick = {
                mostrarDialogSelecionarCliente = false
                mostrarDialogNovoCliente = true
            },
            onDismiss = { mostrarDialogSelecionarCliente = false }
        )
    }

    // Dialog para selecionar veículo
    if (mostrarDialogSelecionarVeiculo) {
        DialogSelecionarVeiculo(
            veiculos = veiculosDisponiveis,
            veiculoSelecionado = veiculoSelecionado,
            onVeiculoSelecionado = { veiculo ->
                veiculoSelecionado = veiculo
            },
            onNovoVeiculoClick = {
                mostrarDialogSelecionarVeiculo = false
                mostrarDialogNovoVeiculo = true
            },
            onDismiss = { mostrarDialogSelecionarVeiculo = false }
        )
    }

    // Dialog para cadastrar novo cliente diretamente da tela de locação
    if (mostrarDialogNovoCliente) {
        DialogNovoCliente(
            onDismiss = { mostrarDialogNovoCliente = false },
            onSalvar = { nome, cpf, telefone ->
                viewModel.adicionarCliente(nome, cpf, telefone) { novoCliente ->
                    clienteSelecionado = novoCliente
                }
                mostrarDialogNovoCliente = false
            }
        )
    }

    // Dialog para cadastrar novo veículo diretamente da tela de locação
    if (mostrarDialogNovoVeiculo) {
        DialogNovoVeiculo(
            onDismiss = { mostrarDialogNovoVeiculo = false },
            onSalvar = { modelo, placa, ano, diaria ->
                viewModel.adicionarVeiculo(modelo, placa, ano, diaria) { novoVeiculo ->
                    veiculoSelecionado = novoVeiculo
                }
                mostrarDialogNovoVeiculo = false
            }
        )
    }

    // Dialog de sucesso após locação
    if (mensagemSucesso) {
        AlertDialog(
            onDismissRequest = {
                mensagemSucesso = false
                onLocacaoRealizada()
            },
            title = { Text("Sucesso!") },
            text = { Text("Locação realizada e veículo reservado com sucesso.") },
            confirmButton = {
                Button(onClick = {
                    mensagemSucesso = false
                    onLocacaoRealizada()
                }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun CampoSelecao(
    label: String,
    tituloItem: String?,
    subtituloItem: String?,
    icone: ImageVector,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icone,
                contentDescription = null,
                tint = if (tituloItem != null) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (tituloItem != null) {
                    Text(
                        text = tituloItem,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subtituloItem != null) {
                        Text(
                            text = subtituloItem,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                } else {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Selecionar",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DialogSelecionarCliente(
    clientes: List<Cliente>,
    clienteSelecionado: Cliente?,
    onClienteSelecionado: (Cliente) -> Unit,
    onNovoClienteClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Selecionar Cliente", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                FilledTonalButton(
                    onClick = onNovoClienteClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Novo", fontSize = 12.sp)
                }
            }
        },
        text = {
            if (clientes.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Nenhum cliente cadastrado no momento.", color = Color.Gray, fontSize = 14.sp)
                    Button(onClick = onNovoClienteClick) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cadastrar Primeiro Cliente")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(clientes, key = { it.uid }) { cliente ->
                        val isSelected = cliente.uid == clienteSelecionado?.uid
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClienteSelecionado(cliente)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cliente.nome,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "CPF: ${cliente.cpf}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        text = "Tel: ${cliente.telefone}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selecionado",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

@Composable
fun DialogSelecionarVeiculo(
    veiculos: List<Veiculo>,
    veiculoSelecionado: Veiculo?,
    onVeiculoSelecionado: (Veiculo) -> Unit,
    onNovoVeiculoClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Veículo Disponível", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                FilledTonalButton(
                    onClick = onNovoVeiculoClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Novo", fontSize = 12.sp)
                }
            }
        },
        text = {
            if (veiculos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Nenhum veículo disponível para locação no momento.", color = Color.Gray, fontSize = 14.sp)
                    Button(onClick = onNovoVeiculoClick) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cadastrar Novo Veículo")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(veiculos, key = { it.id }) { veiculo ->
                        val isSelected = veiculo.id == veiculoSelecionado?.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onVeiculoSelecionado(veiculo)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = veiculo.modelo,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Placa: ${veiculo.placa} • Ano: ${veiculo.ano}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                    Text(
                                        text = "Diária: R$ ${String.format(Locale.getDefault(), "%.2f", veiculo.valorDiaria)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selecionado",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}
