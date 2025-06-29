package com.lourenc.trolly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.data.local.entity.ListaCompra
import com.lourenc.trolly.data.repository.ProdutoMercado
import com.lourenc.trolly.viewmodel.ListaCompraViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import java.util.*
import com.lourenc.trolly.ui.theme.*
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCompraDetailScreen(navController: NavController, viewModel: ListaCompraViewModel, listaId: Int) {
    val coroutineScope = rememberCoroutineScope()
    var lista by remember { mutableStateOf<ListaCompra?>(null) }
    val itens by viewModel.itensLista.observeAsState(emptyList())
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Carregar a lista e seus itens pelo ID
    LaunchedEffect(listaId) {
        coroutineScope.launch {
            lista = viewModel.getListaById(listaId)
            viewModel.carregarItensLista(listaId)
        }
    }

    Scaffold(
        topBar = {
            TrollyTopBar(
                title = lista?.name ?: "Detalhes da Lista",
                subtitle = lista?.descricao,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            if (itens.isNotEmpty() && itens.all { it.comprado }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TrollySpacing.lg, vertical = TrollySpacing.md)
                        .padding(bottom = 64.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TrollyPrimaryButton(
                        text = "Concluir Lista",
                        onClick = {
                            viewModel.marcarListaComoConcluida(listaId)
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier.width(200.dp)
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = TrollySpacing.lg)
        ) {
            if (itens.isEmpty()) {
                TrollyCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(TrollySpacing.lg),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(TrollySpacing.md))
                        Text(
                            text = "Nenhum item na lista ainda",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Adicione produtos para começar suas compras",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(TrollySpacing.lg))
                        TrollyPrimaryButton(
                            text = "Adicionar Produto",
                            onClick = { navController.navigate("addProduct/$listaId") }
                        )
                    }
                }
            } else {
                // Calculando total da lista
                val total = itens.sumOf { it.quantidade * it.precoUnitario }
                val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = TrollySpacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total da Lista",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = currencyFormat.format(total),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    TrollyPrimaryButton(
                        text = "Adicionar Produto",
                        onClick = { navController.navigate("addProduct/$listaId") },
                        modifier = Modifier.width(180.dp)
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(TrollySpacing.sm)
                ) {
                    items(itens) { item ->
                        ItemCard(
                            item = item,
                            navController = navController,
                            viewModel = viewModel,
                            onToggleComprado = { viewModel.atualizarItemLista(item.copy(comprado = !item.comprado)) },
                            onDelete = { viewModel.removerItemLista(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCard(
    item: ItemLista,
    navController: NavController,
    viewModel: ListaCompraViewModel,
    onToggleComprado: () -> Unit,
    onDelete: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editedQuantidade by remember { mutableStateOf(item.quantidade.toString()) }
    var editedPreco by remember { mutableStateOf(item.precoUnitario.toString()) }
    
    TrollyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (item.comprado) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            supportingContent = {
                Column {
                    Text(
                        text = "${item.quantidade}x ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(item.precoUnitario)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (item.comprado) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        }
                    )
                    Text(
                        text = "Subtotal: ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(item.quantidade * item.precoUnitario)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            leadingContent = {
                Checkbox(
                    checked = item.comprado,
                    onCheckedChange = { onToggleComprado() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(TrollySpacing.xs)
                ) {
                    IconButton(
                        onClick = { showEditDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onDelete
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )
    }
    
    // Dialog de edição
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { 
                Text(
                    "Editar Item",
                    style = MaterialTheme.typography.headlineSmall
                ) 
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(TrollySpacing.md)
                ) {
                    OutlinedTextField(
                        value = editedQuantidade,
                        onValueChange = { editedQuantidade = it },
                        label = { Text("Quantidade") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        shape = TrollyShapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                    )
                    
                    OutlinedTextField(
                        value = editedPreco,
                        onValueChange = { editedPreco = it },
                        label = { Text("Preço Unitário") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        shape = TrollyShapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val quantidade = editedQuantidade.toIntOrNull() ?: item.quantidade
                        val preco = editedPreco.toDoubleOrNull() ?: item.precoUnitario
                        
                        if (quantidade > 0 && preco >= 0) {
                            val updatedItem = item.copy(
                                quantidade = quantidade,
                                precoUnitario = preco
                            )
                            viewModel.atualizarItemLista(updatedItem)
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
} 