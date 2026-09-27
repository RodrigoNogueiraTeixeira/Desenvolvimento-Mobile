package com.example.locadora.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locadora.ui.screens.ClientesScreen
import com.example.locadora.ui.screens.LocacoesScreen
import com.example.locadora.ui.screens.NovaLocacaoScreen
import com.example.locadora.ui.screens.VeiculosScreen

enum class AbaNavegacao(val titulo: String, val icone: ImageVector) {
    VEICULOS("Veículos", Icons.Default.DirectionsCar),
    LOCACOES("Locações", Icons.AutoMirrored.Filled.Assignment),
    CLIENTES("Clientes", Icons.Default.People),
    NOVA_LOCACAO("Alugar", Icons.Default.Key)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: LocadoraViewModel = viewModel()) {
    var abaSelecionada by remember { mutableStateOf(AbaNavegacao.VEICULOS) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locadora de Veículos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                AbaNavegacao.entries.forEach { aba ->
                    NavigationBarItem(
                        selected = abaSelecionada == aba,
                        onClick = { abaSelecionada = aba },
                        icon = { Icon(aba.icone, contentDescription = aba.titulo) },
                        label = { Text(aba.titulo) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (abaSelecionada) {
                AbaNavegacao.VEICULOS -> VeiculosScreen(viewModel = viewModel)
                AbaNavegacao.LOCACOES -> LocacoesScreen(viewModel = viewModel)
                AbaNavegacao.CLIENTES -> ClientesScreen(viewModel = viewModel)
                AbaNavegacao.NOVA_LOCACAO -> NovaLocacaoScreen(
                    viewModel = viewModel,
                    onLocacaoRealizada = {
                        // Ao concluir uma locação, vai para a aba de acompanhamento
                        abaSelecionada = AbaNavegacao.LOCACOES
                    }
                )
            }
        }
    }
}
