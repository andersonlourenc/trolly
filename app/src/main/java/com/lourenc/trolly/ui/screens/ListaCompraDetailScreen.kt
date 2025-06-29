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
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.align(Alignment.CenterStart)
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
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.align(Alignment.Center),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!lista?.descricao.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
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
        bottomBar = {
            if (itens.isNotEmpty() && itens.all { it.comprado }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 48.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            viewModel.marcarListaComoConcluida(listaId)
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Concluir Lista",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Concluir Lista", color = Color.White)
                    }
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
                            onClick = { navController.navigate("addProduct/$listaId") },
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
                        onClick = { navController.navigate("addProduct/$listaId") },
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
                            navController = navController,
                            onToggleComprado = { 
                                val itemAtualizado = item.copy(comprado = !item.comprado)
                                viewModel.atualizarItemLista(itemAtualizado)
                            },
                            onDelete = {
                                viewModel.removerItemLista(item)
                            },
                            onEdit = { itemAtualizado ->
                                viewModel.atualizarItemLista(itemAtualizado)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCard(
    item: ItemLista,
    navController: NavController,
    onToggleComprado: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (ItemLista) -> Unit
) {
    val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR"))
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (item.comprado)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(
                        text = "${item.quantidade} ${item.unidade}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "  ·  ${currencyFormat.format(item.precoUnitario)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = { 
                navController.navigate("editItem/${item.id}")
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Editar/Excluir",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
} 