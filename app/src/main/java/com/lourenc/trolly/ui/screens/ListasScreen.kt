package com.lourenc.trolly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lourenc.trolly.viewmodel.ListaCompraViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.lourenc.trolly.data.local.entity.ListaCompra
import com.google.firebase.auth.FirebaseAuth
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.lourenc.trolly.utils.ListaCompraFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListasScreen(navController: NavController, viewModel: ListaCompraViewModel) {
    val user = FirebaseAuth.getInstance().currentUser
    var selectedTab by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showAddListModal by remember { mutableStateOf(false) }
    
    if (user == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("listas") { inclusive = true }
            }
        }
    } else {
        val nomeCompleto = user.displayName ?: "Usuário"
        val photoUrl = user.photoUrl?.toString()
        val listasAtivas by viewModel.listasAtivas.observeAsState(emptyList())
        val listasConcluidas by viewModel.listasConcluidas.observeAsState(emptyList())
        val isLoading by viewModel.isLoading.observeAsState(false)
        val errorMessage by viewModel.errorMessage.observeAsState(null)
        
        // Observar mensagens de erro
        LaunchedEffect(errorMessage) {
            errorMessage?.let { message ->
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.Dismissed) {
                    viewModel.limparErro()
                }
            }
        }
        
        // Carregar listas quando a tela for carregada
        LaunchedEffect(Unit) {
            viewModel.carregarListasAtivas()
            viewModel.carregarListasConcluidas()
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
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
                        Column {
                            Text(
                                text = "Histórico de Listas",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Gerencie suas listas de compras",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                            )
                        }
                        
                        if (photoUrl != null) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Foto do usuário",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Foto do usuário",
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = false,
                        onClick = { navController.navigate("home") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Listas") },
                        label = { Text("Listas") },
                        selected = true,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Insights") },
                        label = { Text("Insights") },
                        selected = false,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") },
                        selected = false,
                        onClick = { navController.navigate("profile") }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddListModal = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar lista",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tabs
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Ativas (${listasAtivas.size})") },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Concluídas (${listasConcluidas.size})") },
                        icon = { Icon(Icons.Default.History, contentDescription = null) }
                    )
                }
                
                // Conteúdo das tabs
                when (selectedTab) {
                    0 -> ListasTabContent(
                        listas = listasAtivas,
                        isLoading = isLoading,
                        onNavigate = { listaId -> navController.navigate("listaDetail/$listaId") },
                        onEdit = { viewModel.updateLista(it) },
                        onDelete = { viewModel.deleteLista(it) },
                        onConcluir = { viewModel.marcarListaComoConcluida(it.id) },
                        showConcluirButton = true
                    )
                    1 -> ListasTabContent(
                        listas = listasConcluidas,
                        isLoading = isLoading,
                        onNavigate = { listaId -> navController.navigate("listaDetail/$listaId") },
                        onEdit = { viewModel.updateLista(it) },
                        onDelete = { viewModel.deleteLista(it) },
                        onConcluir = { viewModel.marcarListaComoAtiva(it.id) },
                        showConcluirButton = false
                    )
                }
            }
        }

        // Modal de adicionar lista
        if (showAddListModal) {
            AddListModal(
                onDismiss = { showAddListModal = false },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun ListasTabContent(
    listas: List<ListaCompra>,
    isLoading: Boolean,
    onNavigate: (Int) -> Unit,
    onEdit: (ListaCompra) -> Unit,
    onDelete: (ListaCompra) -> Unit,
    onConcluir: (ListaCompra) -> Unit,
    showConcluirButton: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (isLoading) {
            item {
                Text(
                    text = "Carregando...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else if (listas.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (showConcluirButton) Icons.Default.ShoppingCart else Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (showConcluirButton) "Nenhuma lista ativa" else "Nenhuma lista concluída",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = if (showConcluirButton) 
                                "Toque no botão + para criar sua primeira lista" 
                            else 
                                "Complete suas listas para vê-las aqui",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        } else {
            items(listas) { lista ->
                ListaCard(
                    lista = lista,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onNavigate = onNavigate,
                    onConcluir = onConcluir,
                    showConcluirButton = showConcluirButton
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCard(
    lista: ListaCompra,
    onEdit: (ListaCompra) -> Unit,
    onDelete: (ListaCompra) -> Unit,
    onNavigate: (Int) -> Unit,
    onConcluir: (ListaCompra) -> Unit,
    showConcluirButton: Boolean
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(lista.name) }
    var editedDescricao by remember { mutableStateOf(lista.descricao) }
    
    val sheetState = rememberModalBottomSheetState()
    val editSheetState = rememberModalBottomSheetState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate(lista.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when {
                    lista.name.contains("casa", ignoreCase = true) -> Icons.Default.Home
                    lista.name.contains("farmacia", ignoreCase = true) -> Icons.Default.LocalPharmacy
                    else -> Icons.Default.ShoppingCart
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = lista.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (lista.descricao.isNotEmpty()) {
                    Text(
                        text = lista.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Criada em ${ListaCompraFormatter.formatDate(lista.dataCriacao)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            
            IconButton(
                onClick = { showBottomSheet = true }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Mais opções",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
    
    // Bottom Sheet com opções
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                ListItem(
                    headlineContent = { Text("Editar lista") },
                    leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        showEditSheet = true
                    }
                )
                
                if (showConcluirButton) {
                    ListItem(
                        headlineContent = { Text("Marcar como concluída") },
                        leadingContent = { Icon(Icons.Default.Check, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showBottomSheet = false
                            onConcluir(lista)
                        }
                    )
                }
                
                ListItem(
                    headlineContent = { Text("Excluir lista") },
                    leadingContent = { Icon(Icons.Default.Delete, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        showDeleteDialog = true
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Bottom Sheet de edição
    if (showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            sheetState = editSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Editar Lista",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nome da lista") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = editedDescricao,
                    onValueChange = { editedDescricao = it },
                    label = { Text("Descrição (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { showEditSheet = false }
                    ) {
                        Text("Cancelar")
                    }
                    TextButton(
                        onClick = {
                            onEdit(lista.copy(name = editedName, descricao = editedDescricao))
                            showEditSheet = false
                        }
                    ) {
                        Text("Salvar")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // Dialog de confirmação de exclusão
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Excluir lista") },
            text = { Text("Tem certeza que deseja excluir a lista \"${lista.name}\"? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(lista)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
} 