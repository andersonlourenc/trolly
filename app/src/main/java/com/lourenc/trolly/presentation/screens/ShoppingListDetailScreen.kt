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
import com.lourenc.trolly.presentation.components.ProductSuggestionsCard
import com.lourenc.trolly.domain.usecase.ProductSuggestion
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import java.util.*
import com.lourenc.trolly.presentation.theme.*
import com.lourenc.trolly.presentation.theme.EditBlue
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
    var productSuggestions by remember { mutableStateOf<List<ProductSuggestion>>(emptyList()) }
    var isLoadingSuggestions by remember { mutableStateOf(false) }
    var currentSuggestionIndex by remember { mutableStateOf(0) }
    val sheetState = rememberModalBottomSheetState()

    // Carregar a lista e seus itens pelo ID
    LaunchedEffect(listId) {
        coroutineScope.launch {
            shoppingList = viewModel.getShoppingListById(listId)
            viewModel.loadListItems(listId)
        }
    }
    
    // Carregar sugestões sempre que a tela for carregada ou os itens mudarem
    LaunchedEffect(listId, items) {
        isLoadingSuggestions = true
        try {
            val suggestions = viewModel.getProductSuggestions(items)
            productSuggestions = suggestions
            currentSuggestionIndex = 0
        } catch (e: Exception) {
            println("Erro ao carregar sugestões: ${e.message}")
            productSuggestions = emptyList()
        } finally {
            isLoadingSuggestions = false
        }
    }

    // Função para adicionar produto da sugestão e remover da lista
    fun addProductFromSuggestion(suggestion: ProductSuggestion) {
        coroutineScope.launch {
            viewModel.addProductFromSuggestion(listId, suggestion)
            // Remove a sugestão da lista
            productSuggestions = productSuggestions.filter { it != suggestion }
            // Ajusta o índice se necessário
            if (currentSuggestionIndex >= productSuggestions.size && productSuggestions.isNotEmpty()) {
                currentSuggestionIndex = 0
            }
        }
    }

    Scaffold(
        topBar = {
            TrollyTopBar(
                title = shoppingList?.name ?: "Detalhes da Lista",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            // Barra inferior com total e botão concluir
            AnimatedVisibility(
                visible = items.isNotEmpty(),
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Total da lista à esquerda
                        Column {
                            Text(
                                text = "Total da Lista",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            val total = items.sumOf { it.quantity * it.unitPrice }
                            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                            Text(
                                text = currencyFormat.format(total),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Botão "Concluir Lista" à direita (quando todos os itens estão comprados)
                        if (items.all { it.purchased }) {
                            TrollyPrimaryButton(
                                text = "Concluir Lista",
                                onClick = {
                                    viewModel.markListAsCompleted(listId)
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                modifier = Modifier.width(160.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Seção de sugestões com carrossel
            AnimatedVisibility(
                visible = isLoadingSuggestions || productSuggestions.isNotEmpty(),
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400)),
                exit = slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            ) {
                if (isLoadingSuggestions) {
                    TrollyCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Analisando seu histórico...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else if (productSuggestions.isNotEmpty()) {
                    TrollyCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Sugestões Inteligentes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Baseado no seu histórico de compras",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Carrossel de sugestões - mostra apenas 1 por vez
                            if (productSuggestions.isNotEmpty()) {
                                val currentSuggestion = productSuggestions[currentSuggestionIndex]
                                AnimatedVisibility(
                                    visible = true,
                                    enter = slideInHorizontally(
                                        initialOffsetX = { 100 },
                                        animationSpec = tween(300)
                                    ) + fadeIn(animationSpec = tween(300)),
                                    exit = slideOutHorizontally(
                                        targetOffsetX = { -100 },
                                        animationSpec = tween(300)
                                    ) + fadeOut(animationSpec = tween(300))
                                ) {
                                    SuggestionListItem(
                                        suggestion = currentSuggestion,
                                        onAdd = { addProductFromSuggestion(currentSuggestion) }
                                    )
                                }
                                
                                // Navegação do carrossel
                                if (productSuggestions.size > 1) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                currentSuggestionIndex = if (currentSuggestionIndex > 0) {
                                                    currentSuggestionIndex - 1
                                                } else {
                                                    productSuggestions.size - 1
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ChevronLeft,
                                                contentDescription = "Anterior",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.width(16.dp))
                                        
                                        // Indicadores de página
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            repeat(productSuggestions.size) { index ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .background(
                                                            color = if (index == currentSuggestionIndex) {
                                                                MaterialTheme.colorScheme.primary
                                                            } else {
                                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                                            },
                                                            shape = CircleShape
                                                        )
                                                )
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.width(16.dp))
                                        
                                        IconButton(
                                            onClick = {
                                                currentSuggestionIndex = if (currentSuggestionIndex < productSuggestions.size - 1) {
                                                    currentSuggestionIndex + 1
                                                } else {
                                                    0
                                                }
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = "Próximo",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Botão "Adicionar Produto" sempre visível
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("addProduct/$listId") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Adicionar Produto",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Estado vazio com animação
            AnimatedVisibility(
                visible = items.isEmpty() && !isLoadingSuggestions && productSuggestions.isEmpty(),
                enter = slideInVertically(
                    initialOffsetY = { 50 },
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            ) {
                TrollyCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum item na lista ainda",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Adicione produtos para começar suas compras",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            // Lista de itens com animação
            if (items.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    items(items) { item ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically(
                                initialOffsetY = { 30 },
                                animationSpec = tween(300)
                            ) + fadeIn(animationSpec = tween(300))
                        ) {
                        ItemCard(
                            item = item,
                            navController = navController,
                            viewModel = viewModel,
                            onTogglePurchased = { viewModel.updateListItem(item.copy(purchased = !item.purchased)) },
                            onDelete = { viewModel.removeListItem(item) }
                        )
                        }
                    }
                    
                    // Espaço extra no final para a barra inferior
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
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
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (item.purchased) {
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Subtotal: ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(item.quantity * item.unitPrice)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
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
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                            tint = EditBlue
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
            sheetState = editSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Editar Item",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                    IconButton(
                        onClick = { showEditSheet = false }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TrollyTextField(
                    value = editedQuantity,
                    onValueChange = { editedQuantity = it },
                    label = "Quantidade",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TrollyTextField(
                    value = editedPrice,
                    onValueChange = { editedPrice = it },
                    label = "Preço Unitário",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done
                    )
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SuggestionListItem(
    suggestion: ProductSuggestion,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = suggestion.product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = suggestion.reason,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "R$ %.2f".format(suggestion.product.price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                
                FloatingActionButton(
                    onClick = onAdd,
                    modifier = Modifier.size(40.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
} 