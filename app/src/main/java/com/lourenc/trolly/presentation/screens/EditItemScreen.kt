package com.lourenc.trolly.presentation.screens

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
import com.lourenc.trolly.data.local.entity.ListItem
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    navController: NavController,
    viewModel: ShoppingListViewModel,
    itemId: Int
) {
    val items by viewModel.currentListItems.observeAsState(emptyList())
    val item = items.find { it.id == itemId }
    var quantity by remember { mutableStateOf(item?.quantity?.toString() ?: "1") }
    var price by remember { mutableStateOf(item?.unitPrice?.toString() ?: "0.0") }
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
                    value = quantity,
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) quantity = it
                    },
                    label = { Text("Quantidade") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        if (it.isEmpty() || it.toDoubleOrNull() != null) price = it
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
                            viewModel.removeListItem(item)
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
                            val qtd = quantity.toIntOrNull() ?: item.quantity
                            val precoUnit = price.toDoubleOrNull() ?: item.unitPrice
                            val updatedItem = item.copy(quantity = qtd, unitPrice = precoUnit)
                            viewModel.updateListItem(updatedItem)
                            navController.popBackStack()
                        },
                        enabled = quantity.isNotEmpty() && price.isNotEmpty()
                    ) {
                        Text("Salvar")
                    }
                }
            }
        }
    }
} 