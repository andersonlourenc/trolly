package com.lourenc.trolly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.viewmodel.ListaCompraViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    navController: NavController,
    viewModel: ListaCompraViewModel,
    itemId: Int
) {
    val itens by viewModel.itensLista.observeAsState(emptyList())
    val item = itens.find { it.id == itemId }
    var quantidade by remember { mutableStateOf(item?.quantidade?.toString() ?: "1") }
    var preco by remember { mutableStateOf(item?.precoUnitario?.toString() ?: "0.0") }
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Produto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        if (item != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(item.name, style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = quantidade,
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) quantidade = it
                    },
                    label = { Text("Quantidade") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = preco,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null) preco = it
                    },
                    label = { Text("Preço unitário") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    prefix = { Text("R$ ") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.removerItemLista(item)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Excluir")
                    }
                    Button(
                        onClick = {
                            val qtd = quantidade.toIntOrNull() ?: item.quantidade
                            val precoUnit = preco.toDoubleOrNull() ?: item.precoUnitario
                            val itemAtualizado = item.copy(quantidade = qtd, precoUnitario = precoUnit)
                            viewModel.atualizarItemLista(itemAtualizado)
                            navController.popBackStack()
                        },
                        enabled = quantidade.isNotEmpty() && preco.isNotEmpty()
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
} 