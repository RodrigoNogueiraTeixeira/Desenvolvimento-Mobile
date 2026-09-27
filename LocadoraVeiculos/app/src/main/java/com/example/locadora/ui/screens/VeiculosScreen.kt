package com.example.locadora.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.locadora.BancoDados.Veiculo
import com.example.locadora.ui.LocadoraViewModel

@Composable
fun VeiculosScreen(viewModel: LocadoraViewModel) {
    val veiculos by viewModel.veiculos.collectAsState()
    var mostrarDialogNovo by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Cabeçalho com título
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Catálogo de Veículos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${veiculos.size} cadastrados • ${veiculos.count { it.disponivel }} disponíveis",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botão grande e evidente para cadastrar novo veículo
            Button(
                onClick = { mostrarDialogNovo = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cadastrar Novo Veículo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (veiculos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum veículo cadastrado no momento.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(veiculos, key = { it.id }) { veiculo ->
                        VeiculoCard(
                            veiculo = veiculo,
                            onRemover = { viewModel.removerVeiculo(veiculo) }
                        )
                    }
                }
            }
        }

        // Botão flutuante (+) posicionado no canto inferior direito
        FloatingActionButton(
            onClick = { mostrarDialogNovo = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Adicionar Veículo", tint = Color.White)
        }
    }

    if (mostrarDialogNovo) {
        DialogNovoVeiculo(
            onDismiss = { mostrarDialogNovo = false },
            onSalvar = { modelo, placa, ano, diaria ->
                viewModel.adicionarVeiculo(modelo, placa, ano, diaria)
                mostrarDialogNovo = false
            }
        )
    }
}

@Composable
fun VeiculoCard(veiculo: Veiculo, onRemover: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = veiculo.modelo,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Placa: ${veiculo.placa} • Ano: ${veiculo.ano}",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "Diária: R$ ${String.format("%.2f", veiculo.valorDiaria)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))
                // Tag de status
                Box(
                    modifier = Modifier
                        .background(
                            if (veiculo.disponivel) Color(0xFF2E7D32) else Color(0xFFC62828),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (veiculo.disponivel) "Disponível" else "Alugado",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            IconButton(onClick = onRemover) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun DialogNovoVeiculo(
    onDismiss: () -> Unit,
    onSalvar: (modelo: String, placa: String, ano: String, diaria: Double) -> Unit
) {
    var modelo by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var diariaText by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }

    val diariaValida = diariaText.replace(",", ".").trim().toDoubleOrNull()?.let { it > 0 } ?: false

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Veículo", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = modelo,
                    onValueChange = {
                        modelo = it
                        if (erro && it.isNotBlank()) erro = false
                    },
                    label = { Text("Modelo do Veículo *") },
                    placeholder = { Text("Ex: Fiat Argo 1.0") },
                    singleLine = true,
                    isError = erro && modelo.isBlank(),
                    supportingText = if (erro && modelo.isBlank()) {
                        { Text("Informe o modelo", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = placa,
                    onValueChange = {
                        placa = it.uppercase()
                        if (erro && it.isNotBlank()) erro = false
                    },
                    label = { Text("Placa *") },
                    placeholder = { Text("Ex: ABC1D23") },
                    singleLine = true,
                    isError = erro && placa.isBlank(),
                    supportingText = if (erro && placa.isBlank()) {
                        { Text("Informe a placa", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ano,
                    onValueChange = {
                        ano = it
                        if (erro && it.isNotBlank()) erro = false
                    },
                    label = { Text("Ano *") },
                    placeholder = { Text("Ex: 2024") },
                    singleLine = true,
                    isError = erro && ano.isBlank(),
                    supportingText = if (erro && ano.isBlank()) {
                        { Text("Informe o ano", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = diariaText,
                    onValueChange = {
                        diariaText = it
                        if (erro && diariaValida) erro = false
                    },
                    label = { Text("Valor Diária (R$) *") },
                    placeholder = { Text("Ex: 150.00") },
                    singleLine = true,
                    isError = erro && !diariaValida,
                    supportingText = if (erro && !diariaValida) {
                        { Text("Informe um valor numérico válido maior que zero", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val diaria = diariaText.replace(",", ".").trim().toDoubleOrNull()
                val modeloTrim = modelo.trim()
                val placaTrim = placa.trim().uppercase()
                val anoTrim = ano.trim()

                if (modeloTrim.isNotBlank() && placaTrim.isNotBlank() && anoTrim.isNotBlank() && diaria != null && diaria > 0) {
                    onSalvar(modeloTrim, placaTrim, anoTrim, diaria)
                } else {
                    erro = true
                }
            }) {
                Text("Cadastrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
