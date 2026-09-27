package com.example.locadora.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.locadora.BancoDados.BancoDados
import com.example.locadora.BancoDados.Cliente
import com.example.locadora.BancoDados.Locacao
import com.example.locadora.BancoDados.Veiculo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LocadoraViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BancoDados.getDatabase(application)
    private val clienteDao = db.clienteDao()
    private val veiculoDao = db.veiculoDao()
    private val locacaoDao = db.locacaoDao()

    val clientes: StateFlow<List<Cliente>> = clienteDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val veiculos: StateFlow<List<Veiculo>> = veiculoDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val veiculosDisponiveis: StateFlow<List<Veiculo>> = veiculoDao.getDisponiveis()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val locacoes: StateFlow<List<Locacao>> = locacaoDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        popularDadosIniciaisSeNecessario()
    }

    private fun popularDadosIniciaisSeNecessario() {
        viewModelScope.launch {
            val listaVeiculos = veiculoDao.getAll().first()
            if (listaVeiculos.isEmpty()) {
                veiculoDao.insert(Veiculo(modelo = "Fiat Uno Attractive 1.0", placa = "BRA-2E19", ano = "2021", valorDiaria = 95.0, disponivel = true))
                veiculoDao.insert(Veiculo(modelo = "Hyundai HB20 Comfort", placa = "KLT-8910", ano = "2023", valorDiaria = 130.0, disponivel = true))
                veiculoDao.insert(Veiculo(modelo = "Toyota Corolla XEi 2.0", placa = "XYZ-4F32", ano = "2024", valorDiaria = 220.0, disponivel = true))
                veiculoDao.insert(Veiculo(modelo = "Jeep Renegade Longitude", placa = "JEP-7788", ano = "2022", valorDiaria = 180.0, disponivel = true))
            }

            val listaClientes = clienteDao.getAll().first()
            if (listaClientes.isEmpty()) {
                clienteDao.insert(Cliente(nome = "João da Silva", cpf = "123.456.789-00", telefone = "(11) 98765-4321"))
                clienteDao.insert(Cliente(nome = "Maria Oliveira", cpf = "987.654.321-99", telefone = "(21) 99123-4567"))
            }
        }
    }

    fun adicionarCliente(nome: String, cpf: String, telefone: String, onCriado: ((Cliente) -> Unit)? = null) {
        viewModelScope.launch {
            val cliente = Cliente(nome = nome.trim(), cpf = cpf.trim(), telefone = telefone.trim())
            val id = clienteDao.insert(cliente)
            onCriado?.invoke(cliente.copy(uid = id.toInt()))
        }
    }

    fun removerCliente(cliente: Cliente) {
        viewModelScope.launch {
            clienteDao.delete(cliente)
        }
    }

    fun adicionarVeiculo(
        modelo: String,
        placa: String,
        ano: String,
        valorDiaria: Double,
        onCriado: ((Veiculo) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val veiculo = Veiculo(
                modelo = modelo.trim(),
                placa = placa.trim().uppercase(),
                ano = ano.trim(),
                valorDiaria = valorDiaria,
                disponivel = true
            )
            val id = veiculoDao.insert(veiculo)
            onCriado?.invoke(veiculo.copy(id = id.toInt()))
        }
    }

    fun removerVeiculo(veiculo: Veiculo) {
        viewModelScope.launch {
            veiculoDao.delete(veiculo)
        }
    }

    fun realizarLocacao(cliente: Cliente, veiculo: Veiculo, data: String, dias: Int, onSucesso: () -> Unit) {
        viewModelScope.launch {
            val total = dias * veiculo.valorDiaria
            val locacao = Locacao(
                clienteId = cliente.uid,
                clienteNome = cliente.nome,
                veiculoId = veiculo.id,
                veiculoModelo = veiculo.modelo,
                veiculoPlaca = veiculo.placa,
                dataLocacao = data,
                dias = dias,
                valorTotal = total,
                ativa = true
            )
            locacaoDao.insert(locacao)
            veiculoDao.update(veiculo.copy(disponivel = false))
            onSucesso()
        }
    }

    fun devolverVeiculo(locacao: Locacao) {
        viewModelScope.launch {
            locacaoDao.update(locacao.copy(ativa = false))
            val veiculosAtuais = veiculoDao.getAll().first()
            val veiculo = veiculosAtuais.find { it.id == locacao.veiculoId }
            if (veiculo != null) {
                veiculoDao.update(veiculo.copy(disponivel = true))
            }
        }
    }
}
