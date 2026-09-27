package com.example.locadora.ui.screens

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import com.example.locadora.BancoDados.Cliente
import com.example.locadora.ui.LocadoraViewModel

@Composable
fun ClientesScreen(viewModel: LocadoraViewModel) {
    val clientes by viewModel.clientes.collectAsState()
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
                    text = "Gestão de Clientes",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${clientes.size} clientes cadastrados",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botão grande e evidente para cadastrar novo cliente
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
                Text("Cadastrar Novo Cliente", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (clientes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhum cliente cadastrado no momento.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(clientes, key = { it.uid }) { cliente ->
                        ClienteCard(
                            cliente = cliente,
                            onRemover = { viewModel.removerCliente(cliente) }
                        )
                    }
                }
            }
        }

        // Botão flutuante (+) no canto inferior direito
        FloatingActionButton(
            onClick = { mostrarDialogNovo = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Novo Cliente", tint = Color.White)
        }
    }

    if (mostrarDialogNovo) {
        DialogNovoCliente(
            onDismiss = { mostrarDialogNovo = false },
            onSalvar = { nome, cpf, telefone ->
                viewModel.adicionarCliente(nome, cpf, telefone)
                mostrarDialogNovo = false
            }
        )
    }
}

@Composable
fun ClienteCard(cliente: Cliente, onRemover: () -> Unit) {
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
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(36.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cliente.nome,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "CPF: ${cliente.cpf}",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = cliente.telefone,
                        fontSize = 13.sp,
                        color = Color.DarkGray
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
fun DialogNovoCliente(
    onDismiss: () -> Unit,
    onSalvar: (nome: String, cpf: String, telefone: String) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Cliente", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = {
                        nome = it
                        if (erro && it.isNotBlank()) erro = false
                    },
                    label = { Text("Nome Completo *") },
                    placeholder = { Text("Ex: Carlos Drummond") },
                    singleLine = true,
                    isError = erro && nome.isBlank(),
                    supportingText = if (erro && nome.isBlank()) {
                        { Text("Informe o nome completo", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cpf,
                    onValueChange = {
                        cpf = it
                        if (erro && it.isNotBlank()) erro = false
                    },
                    label = { Text("CPF *") },
                    placeholder = { Text("Ex: 123.456.789-00") },
                    singleLine = true,
                    isError = erro && cpf.isBlank(),
                    supportingText = if (erro && cpf.isBlank()) {
                        { Text("Informe o CPF", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = telefone,
                    onValueChange = {
                        telefone = it
                        if (erro && it.isNotBlank()) erro = false
                    },
                    label = { Text("Telefone *") },
                    placeholder = { Text("Ex: (11) 98888-7777") },
                    singleLine = true,
                    isError = erro && telefone.isBlank(),
                    supportingText = if (erro && telefone.isBlank()) {
                        { Text("Informe o telefone", color = MaterialTheme.colorScheme.error) }
                    } else null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (erro) {
                    Text(
                        text = "Por favor, preencha todos os campos obrigatórios.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (nome.isNotBlank() && cpf.isNotBlank() && telefone.isNotBlank()) {
                    onSalvar(nome.trim(), cpf.trim(), telefone.trim())
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
