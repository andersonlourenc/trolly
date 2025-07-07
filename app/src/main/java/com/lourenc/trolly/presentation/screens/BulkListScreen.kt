package com.lourenc.trolly.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lourenc.trolly.domain.usecase.BulkListUseCase
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkListScreen(
    navController: NavController,
    bulkListUseCase: BulkListUseCase
) {
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inserção Programática") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Criar Listas Programaticamente",
                style = MaterialTheme.typography.headlineSmall
            )

            // Lista para ontem
            Button(
                onClick = {
                    isLoading = true
                    message = "Criando lista para ontem..."
                    coroutineScope.launch {
                        try {
                            val result = bulkListUseCase.createListForYesterday("Compras de ontem")
                            message = when (result) {
                                is com.lourenc.trolly.domain.result.ShoppingListResult.ShoppingListSuccess -> 
                                    "Lista criada com sucesso: ${result.shoppingList.name}"
                                is com.lourenc.trolly.domain.result.ShoppingListResult.Error -> 
                                    "Erro: ${result.message}"
                                else -> "Operação concluída"
                            }
                        } catch (e: Exception) {
                            message = "Erro: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Criar Lista para Ontem")
            }

            // Lista para semana passada
            Button(
                onClick = {
                    isLoading = true
                    message = "Criando lista para semana passada..."
                    coroutineScope.launch {
                        try {
                            val result = bulkListUseCase.createListForLastWeek("Compras da semana passada")
                            message = when (result) {
                                is com.lourenc.trolly.domain.result.ShoppingListResult.ShoppingListSuccess -> 
                                    "Lista criada com sucesso: ${result.shoppingList.name}"
                                is com.lourenc.trolly.domain.result.ShoppingListResult.Error -> 
                                    "Erro: ${result.message}"
                                else -> "Operação concluída"
                            }
                        } catch (e: Exception) {
                            message = "Erro: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Criar Lista para Semana Passada")
            }

            // Lista para mês passado
            Button(
                onClick = {
                    isLoading = true
                    message = "Criando lista para mês passado..."
                    coroutineScope.launch {
                        try {
                            val result = bulkListUseCase.createListForLastMonth("Compras do mês passado")
                            message = when (result) {
                                is com.lourenc.trolly.domain.result.ShoppingListResult.ShoppingListSuccess -> 
                                    "Lista criada com sucesso: ${result.shoppingList.name}"
                                is com.lourenc.trolly.domain.result.ShoppingListResult.Error -> 
                                    "Erro: ${result.message}"
                                else -> "Operação concluída"
                            }
                        } catch (e: Exception) {
                            message = "Erro: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Criar Lista para Mês Passado")
            }

            // Lista para data específica
            Button(
                onClick = {
                    isLoading = true
                    message = "Criando lista para 15/03/2024..."
                    coroutineScope.launch {
                        try {
                            val result = bulkListUseCase.createListForDate("Compras do mês", 2024, Calendar.MARCH, 15)
                            message = when (result) {
                                is com.lourenc.trolly.domain.result.ShoppingListResult.ShoppingListSuccess -> 
                                    "Lista criada com sucesso: ${result.shoppingList.name}"
                                is com.lourenc.trolly.domain.result.ShoppingListResult.Error -> 
                                    "Erro: ${result.message}"
                                else -> "Operação concluída"
                            }
                        } catch (e: Exception) {
                            message = "Erro: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Criar Lista para 15/03/2024")
            }

            // Lista de template
            Button(
                onClick = {
                    isLoading = true
                    message = "Criando lista de churrasco..."
                    coroutineScope.launch {
                        try {
                            val targetDate = Calendar.getInstance()
                            val result = bulkListUseCase.createListFromTemplate("churrasco", targetDate)
                            message = when (result) {
                                is com.lourenc.trolly.domain.result.ShoppingListResult.ShoppingListSuccess -> 
                                    "Lista criada com sucesso: ${result.shoppingList.name}"
                                is com.lourenc.trolly.domain.result.ShoppingListResult.Error -> 
                                    "Erro: ${result.message}"
                                else -> "Operação concluída"
                            }
                        } catch (e: Exception) {
                            message = "Erro: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Criar Lista de Churrasco")
            }

            // Listas semanais
            Button(
                onClick = {
                    isLoading = true
                    message = "Criando 4 listas semanais..."
                    coroutineScope.launch {
                        try {
                            val startDate = Calendar.getInstance()
                            startDate.set(2024, Calendar.MARCH, 1)
                            val result = bulkListUseCase.createWeeklyLists(startDate, 4, "Compras Semanais")
                            message = when (result) {
                                is com.lourenc.trolly.domain.result.ShoppingListResult.ShoppingListsSuccess -> 
                                    "${result.shoppingLists.size} listas criadas com sucesso"
                                is com.lourenc.trolly.domain.result.ShoppingListResult.Error -> 
                                    "Erro: ${result.message}"
                                else -> "Operação concluída"
                            }
                        } catch (e: Exception) {
                            message = "Erro: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Criar 4 Listas Semanais")
            }

            // Listas mensais
            Button(
                onClick = {
                    isLoading = true
                    message = "Criando 12 listas mensais..."
                    coroutineScope.launch {
                        try {
                            val startDate = Calendar.getInstance()
                            startDate.set(2024, Calendar.JANUARY, 1)
                            val result = bulkListUseCase.createMonthlyLists(startDate, 12, "Compras Mensais")
                            message = when (result) {
                                is com.lourenc.trolly.domain.result.ShoppingListResult.ShoppingListsSuccess -> 
                                    "${result.shoppingLists.size} listas criadas com sucesso"
                                is com.lourenc.trolly.domain.result.ShoppingListResult.Error -> 
                                    "Erro: ${result.message}"
                                else -> "Operação concluída"
                            }
                        } catch (e: Exception) {
                            message = "Erro: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Criar 12 Listas Mensais")
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            if (message.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
} 