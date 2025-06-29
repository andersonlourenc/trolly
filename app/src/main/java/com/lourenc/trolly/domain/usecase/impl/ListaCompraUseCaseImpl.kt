package com.lourenc.trolly.domain.usecase.impl

import com.lourenc.trolly.data.local.entity.ListaCompra
import com.lourenc.trolly.data.repository.interfaces.ItemListaRepository
import com.lourenc.trolly.data.repository.interfaces.ListaCompraRepository
import com.lourenc.trolly.domain.builder.ListaCompraBuilder
import com.lourenc.trolly.domain.factory.ListaCompraFactory
import com.lourenc.trolly.domain.factory.ListaCompraFactoryImpl
import com.lourenc.trolly.domain.model.ListaCompraType
import com.lourenc.trolly.domain.result.ListaCompraResult
import com.lourenc.trolly.domain.usecase.ListaCompraUseCase
import kotlinx.coroutines.flow.first
import java.util.Calendar

class ListaCompraUseCaseImpl(
    private val listaRepository: ListaCompraRepository,
    private val itemRepository: ItemListaRepository,
    private val factory: ListaCompraFactory = ListaCompraFactoryImpl()
) : ListaCompraUseCase {

    override suspend fun createLista(lista: ListaCompra): ListaCompraResult {
        return try {
            println("DEBUG: UseCase - Iniciando criação da lista: ${lista.name}")
            listaRepository.insertLista(lista)
            println("DEBUG: UseCase - Lista inserida no repositório com sucesso")
            ListaCompraResult.ListaSuccess(lista)
        } catch (e: Exception) {
            println("DEBUG: UseCase - Erro ao criar lista: ${e.message}")
            e.printStackTrace()
            ListaCompraResult.Error("Erro ao criar lista: ${e.message}", e)
        }
    }

    override suspend fun createListaWithType(type: ListaCompraType, nome: String, descricao: String): ListaCompraResult {
        return try {
            val lista = factory.createLista(type, nome, descricao)
            listaRepository.insertLista(lista)
            ListaCompraResult.ListaSuccess(lista)
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao criar lista com tipo: ${e.message}", e)
        }
    }

    override suspend fun updateLista(lista: ListaCompra): ListaCompraResult {
        return try {
            listaRepository.updateLista(lista)
            ListaCompraResult.ListaSuccess(lista)
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao atualizar lista: ${e.message}", e)
        }
    }

    override suspend fun updateStatus(listaId: Int, status: String): ListaCompraResult {
        return try {
            listaRepository.updateStatus(listaId, status)
            ListaCompraResult.Success("Status atualizado com sucesso")
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao atualizar status: ${e.message}", e)
        }
    }

    override suspend fun deleteLista(lista: ListaCompra): ListaCompraResult {
        return try {
            listaRepository.deleteLista(lista)
            ListaCompraResult.Success("Lista deletada com sucesso")
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao deletar lista: ${e.message}", e)
        }
    }

    override suspend fun getListaById(id: Int): ListaCompraResult {
        return try {
            val lista = listaRepository.getListaById(id)
            if (lista != null) {
                ListaCompraResult.ListaSuccess(lista)
            } else {
                ListaCompraResult.Error("Lista não encontrada")
            }
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao buscar lista: ${e.message}", e)
        }
    }

    override suspend fun getAllListas(): ListaCompraResult {
        return try {
            val listas = listaRepository.getAllListas().first()
            ListaCompraResult.ListasSuccess(listas)
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao carregar listas: ${e.message}", e)
        }
    }

    override suspend fun getListasAtivas(): ListaCompraResult {
        return try {
            val listas = listaRepository.getListasAtivas().first()
            ListaCompraResult.ListasSuccess(listas)
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao carregar listas ativas: ${e.message}", e)
        }
    }

    override suspend fun getListasConcluidas(): ListaCompraResult {
        return try {
            val listas = listaRepository.getListasConcluidas().first()
            ListaCompraResult.ListasSuccess(listas)
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao carregar listas concluídas: ${e.message}", e)
        }
    }

    override suspend fun getListasByMonth(month: Int, year: Int): ListaCompraResult {
        return try {
            val listas = listaRepository.getListasByMonth(month, year)
            ListaCompraResult.ListasSuccess(listas)
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao buscar listas do mês: ${e.message}", e)
        }
    }

    override suspend fun getListasByDateRange(startDate: Long, endDate: Long): ListaCompraResult {
        return try {
            val listas = listaRepository.getListasByDateRange(startDate, endDate)
            ListaCompraResult.ListasSuccess(listas)
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao buscar listas por período: ${e.message}", e)
        }
    }

    override suspend fun getListasByType(type: ListaCompraType): ListaCompraResult {
        return try {
            // Por enquanto, vamos buscar todas as listas e filtrar por tipo
            // Em uma implementação real, isso seria feito no banco de dados
            val calendar = Calendar.getInstance()
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val listas = listaRepository.getListasByMonth(month, year)
            
            // Filtrar por tipo baseado no nome (simulação)
            val listasFiltradas = listas.filter { lista ->
                when (type) {
                    is ListaCompraType.Weekly -> lista.name.contains("Semanal", ignoreCase = true)
                    is ListaCompraType.Monthly -> lista.name.contains("Mensal", ignoreCase = true)
                    is ListaCompraType.Emergency -> lista.name.contains("Emergência", ignoreCase = true)
                    is ListaCompraType.Recurrent -> lista.name.contains("Recorrente", ignoreCase = true)
                    else -> true // Regular inclui todas
                }
            }
            
            ListaCompraResult.ListasSuccess(listasFiltradas)
        } catch (e: Exception) {
            ListaCompraResult.Error("Erro ao buscar listas por tipo: ${e.message}", e)
        }
    }

    override suspend fun calcularGastoMensal(month: Int, year: Int): ListaCompraResult {
        return try {
            println("DEBUG: Iniciando cálculo do gasto mensal para mês $month/$year")
            
            // Buscar apenas listas concluídas do mês
            val listas = listaRepository.getListasConcluidas().first()
            println("DEBUG: Total de listas concluídas encontradas: ${listas.size}")
            
            val calendar = Calendar.getInstance()
            calendar.set(year, month, 1, 0, 0, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startDate = calendar.timeInMillis
            
            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.MILLISECOND, -1)
            val endDate = calendar.timeInMillis
            
            println("DEBUG: Período de busca: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(startDate))} até ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(endDate))}")
            
            // Filtrar listas concluídas do mês específico
            val listasConcluidasDoMes = listas.filter { lista ->
                val dataCriacao = Calendar.getInstance().apply { timeInMillis = lista.dataCriacao }
                val mesCriacao = dataCriacao.get(Calendar.MONTH)
                val anoCriacao = dataCriacao.get(Calendar.YEAR)
                
                val estaNoMes = mesCriacao == month && anoCriacao == year
                println("DEBUG: Lista '${lista.name}' criada em ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(lista.dataCriacao))} (Mês: $mesCriacao, Ano: $anoCriacao) - Está no mês $month/$year: $estaNoMes")
                estaNoMes
            }
            
            println("DEBUG: Listas concluídas do mês: ${listasConcluidasDoMes.size}")
            
            var gastoTotal = 0.0
            
            for (lista in listasConcluidasDoMes) {
                val total = itemRepository.calcularTotalLista(lista.id)
                println("DEBUG: Lista '${lista.name}' (ID: ${lista.id}) - Total: R$ $total")
                gastoTotal += total
            }
            
            println("DEBUG: Gasto total mensal: R$ $gastoTotal")
            ListaCompraResult.GastoMensalSuccess(gastoTotal)
        } catch (e: Exception) {
            println("DEBUG: Erro ao calcular gasto mensal: ${e.message}")
            e.printStackTrace()
            ListaCompraResult.Error("Erro ao calcular gasto mensal: ${e.message}", e)
        }
    }

    override suspend fun calcularValorUltimaLista(): ListaCompraResult {
        return try {
            println("DEBUG: Iniciando cálculo do valor da última lista")
            
            // Buscar apenas listas concluídas
            val listasConcluidas = listaRepository.getListasConcluidas().first()
            println("DEBUG: Total de listas concluídas: ${listasConcluidas.size}")
            
            // Encontrar a lista concluída mais recente
            val listaRecente = if (listasConcluidas.isNotEmpty()) {
                listasConcluidas.maxBy { it.dataCriacao }
            } else null
            
            if (listaRecente != null) {
                println("DEBUG: Lista mais recente: '${listaRecente.name}' criada em ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(listaRecente.dataCriacao))}")
                val valor = itemRepository.calcularTotalLista(listaRecente.id)
                println("DEBUG: Valor da última lista: R$ $valor")
                ListaCompraResult.ValorUltimaListaSuccess(valor)
            } else {
                println("DEBUG: Nenhuma lista concluída encontrada")
                ListaCompraResult.ValorUltimaListaSuccess(0.0)
            }
        } catch (e: Exception) {
            println("DEBUG: Erro ao calcular valor da última lista: ${e.message}")
            e.printStackTrace()
            ListaCompraResult.Error("Erro ao calcular valor da última lista: ${e.message}", e)
        }
    }
} 