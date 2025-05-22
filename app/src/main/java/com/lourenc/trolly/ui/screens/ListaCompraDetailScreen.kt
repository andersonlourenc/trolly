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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCompraDetailScreen(navController: NavController, viewModel: ListaCompraViewModel, listaId: Int) {
    val coroutineScope = rememberCoroutineScope()
    var lista by remember { mutableStateOf<ListaCompra?>(null) }
    val itens by viewModel.itensLista.observeAsState(emptyList())
    var showBottomSheet by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(start = 16.dp, end = 16.dp, top = 64.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = lista?.name ?: "Detalhes da Lista",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    IconButton(
                        onClick = { showSearchDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Adicionar Produtos",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = lista?.name ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                if (!lista?.descricao.isNullOrBlank()) {
                    Text(
                        text = lista?.descricao ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (itens.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Nenhum item na lista ainda",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Button(
                            onClick = { showSearchDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buscar Produtos")
                        }
                    }
                }
            } else {
                // Calculando total da lista
                val total = itens.sumOf { it.quantidade * it.precoUnitario }
                val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total: ${currencyFormat.format(total)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Button(
                        onClick = { showSearchDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Adicionar Produto",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar Produto")
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(itens) { item ->
                        ItemCard(
                            item = item,
                            onToggleComprado = { 
                                val itemAtualizado = item.copy(comprado = !item.comprado)
                                viewModel.atualizarItemLista(itemAtualizado)
                            },
                            onDelete = {
                                viewModel.removerItemLista(item)
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Modal de opções da lista
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Gerenciar lista",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { showBottomSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar"
                        )
                    }
                }

                Divider()

                ListItem(
                    headlineContent = { Text("Editar") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        lista?.let { listaAtual ->
                            // Implementar navegação para a tela de edição
                        }
                    }
                )

                ListItem(
                    headlineContent = { Text("Excluir") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = Color(0xFFE53935)
                        )
                    },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        lista?.let { listaToDelete ->
                            viewModel.deleteLista(listaToDelete)
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
    
    // Diálogo de pesquisa/adicionar produtos
    if (showSearchDialog) {
        SearchProductDialog(
            viewModel = viewModel,
            listaId = listaId,
            onDismiss = { showSearchDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchProductDialog(
    viewModel: ListaCompraViewModel,
    listaId: Int,
    onDismiss: () -> Unit
) {
    var searchTerm by remember { mutableStateOf("") }
    val produtosFiltrados by viewModel.produtosFiltrados.observeAsState(emptyList())
    
    // Inicializar com alguns produtos sugeridos
    LaunchedEffect(Unit) {
        viewModel.pesquisarProdutos("")
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Buscar Produtos")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchTerm,
                    onValueChange = {
                        searchTerm = it
                        viewModel.pesquisarProdutos(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Buscar produto") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Produtos sugeridos:",
                    style = MaterialTheme.typography.titleSmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Lista de produtos
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(produtosFiltrados) { produto ->
                        ProductItemCard(
                            produto = produto,
                            onAdd = {
                                val item = viewModel.converterProdutoParaItem(produto, listaId)
                                viewModel.adicionarItemLista(item)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

@Composable
fun ProductItemCard(
    produto: ProdutoMercado, 
    onAdd: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = produto.nome,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = currencyFormat.format(produto.preco),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ItemCard(
    item: ItemLista,
    onToggleComprado: () -> Unit,
    onDelete: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val subtotal = item.quantidade * item.precoUnitario
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.comprado,
                    onCheckedChange = { onToggleComprado() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (item.comprado) 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else 
                            MaterialTheme.colorScheme.onSurface
                    )
                    
                    Row {
                        Text(
                            text = "${item.quantidade} ${item.unidade}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        
                        Text(
                            text = " • ${currencyFormat.format(item.precoUnitario)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currencyFormat.format(subtotal),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
} 