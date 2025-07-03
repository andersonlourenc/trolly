package com.lourenc.trolly.presentation.screens

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
import com.lourenc.trolly.data.local.entity.ListItem
import com.lourenc.trolly.data.local.entity.ShoppingList
import com.lourenc.trolly.data.local.entity.MarketProduct
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import java.util.*
import com.lourenc.trolly.presentation.theme.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListDetailScreen(navController: NavController, viewModel: ShoppingListViewModel, listId: Int) {
    val coroutineScope = rememberCoroutineScope()
    var shoppingList by remember { mutableStateOf<ShoppingList?>(null) }
    val items by viewModel.currentListItems.observeAsState(emptyList())
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Carregar a lista e seus itens pelo ID
    LaunchedEffect(listId) {
        coroutineScope.launch {
            shoppingList = viewModel.getShoppingListById(listId)
            viewModel.loadListItems(listId)
        }
    }

    Scaffold(
        topBar = {
            TrollyTopBar(
                title = shoppingList?.name ?: "Detalhes da Lista",
                subtitle = shoppingList?.description,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            if (items.isNotEmpty() && items.all { it.purchased }) {
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
                            viewModel.markListAsCompleted(listId)
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
                .padding(bottom = TrollySpacing.xl)
        ) {
            if (items.isEmpty()) {
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
                            onClick = { navController.navigate("addProduct/$listId") }
                        )
                    }
                }
            } else {
                // Calculando total da lista
                val total = items.sumOf { it.quantity * it.unitPrice }
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
                        onClick = { navController.navigate("addProduct/$listId") },
                        modifier = Modifier.width(180.dp)
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(TrollySpacing.sm),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = TrollySpacing.xl)
                ) {
                    items(items) { item ->
                        ItemCard(
                            item = item,
                            navController = navController,
                            viewModel = viewModel,
                            onTogglePurchased = { viewModel.updateListItem(item.copy(purchased = !item.purchased)) },
                            onDelete = { viewModel.removeListItem(item) }
                        )
                    }
                    
                    // Espaço extra no final para evitar que o último item seja cortado
                    item {
                        Spacer(modifier = Modifier.height(TrollySpacing.xl))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCard(
    item: ListItem,
    navController: NavController,
    viewModel: ShoppingListViewModel,
    onTogglePurchased: () -> Unit,
    onDelete: () -> Unit
) {
    var showEditSheet by remember { mutableStateOf(false) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var editedPrice by remember { mutableStateOf(item.unitPrice.toString()) }
    val editSheetState = rememberModalBottomSheetState()
    
    TrollyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (item.purchased) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            },
            supportingContent = {
                Column {
                    Text(
                        text = "${item.quantity}x ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(item.unitPrice)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (item.purchased) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        },
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Subtotal: ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(item.quantity * item.unitPrice)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            },
            leadingContent = {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Checkbox(
                        checked = item.purchased,
                        onCheckedChange = { onTogglePurchased() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    )
                }
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(TrollySpacing.xs)
                ) {
                    IconButton(
                        onClick = { 
                            editedQuantity = item.quantity.toString()
                            editedPrice = item.unitPrice.toString()
                            showEditSheet = true 
                        }
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
    
    // Modal de edição
    if (showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            sheetState = editSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(TrollySpacing.lg)
            ) {
                Text(
                    text = "Editar Item",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(TrollySpacing.md))
                
                TrollyTextField(
                    value = editedQuantity,
                    onValueChange = { editedQuantity = it },
                    label = "Quantidade",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                
                Spacer(modifier = Modifier.height(TrollySpacing.md))
                
                TrollyTextField(
                    value = editedPrice,
                    onValueChange = { editedPrice = it },
                    label = "Preço Unitário",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    )
                )
                
                Spacer(modifier = Modifier.height(TrollySpacing.lg))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(TrollySpacing.sm)
                ) {
                    TrollySecondaryButton(
                        text = "Cancelar",
                        onClick = { showEditSheet = false },
                        modifier = Modifier.weight(1f)
                    )
                    
                    TrollyPrimaryButton(
                        text = "Salvar",
                        onClick = {
                            val quantity = editedQuantity.toIntOrNull() ?: item.quantity
                            val price = editedPrice.toDoubleOrNull() ?: item.unitPrice
                            
                            if (quantity > 0 && price >= 0) {
                                val updatedItem = item.copy(
                                    quantity = quantity,
                                    unitPrice = price
                                )
                                viewModel.updateListItem(updatedItem)
                                showEditSheet = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = editedQuantity.isNotBlank() && editedPrice.isNotBlank()
                    )
                }
                
                Spacer(modifier = Modifier.height(TrollySpacing.md))
            }
        }
    }
} 