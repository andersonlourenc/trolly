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
import com.lourenc.trolly.ui.theme.*
import androidx.compose.material3.ExperimentalMaterial3Api

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
                TrollyTopBar(
                    title = "Minhas Listas",
                    subtitle = "Gerencie suas listas de compras",
                    actions = {
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
                )
            },
            bottomBar = {
                TrollyBottomNavigation(
                    currentRoute = "listas",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("listas") { inclusive = true }
                        }
                    }
                )
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
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { 
                            Text(
                                "Ativas (${listasAtivas.size})",
                                style = MaterialTheme.typography.labelMedium
                            ) 
                        },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { 
                            Text(
                                "Concluídas (${listasConcluidas.size})",
                                style = MaterialTheme.typography.labelMedium
                            ) 
                        },
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
                onAddList = { nome ->
                    viewModel.addLista(ListaCompra(name = nome))
                    showAddListModal = false
                }
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
            .padding(horizontal = TrollySpacing.md),
        verticalArrangement = Arrangement.spacedBy(TrollySpacing.sm)
    ) {
        item {
            Spacer(modifier = Modifier.height(TrollySpacing.md))
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
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(TrollySpacing.md))
                        Text(
                            text = if (showConcluirButton) "Nenhuma lista ativa" else "Nenhuma lista concluída",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = if (showConcluirButton) 
                                "Crie sua primeira lista de compras" 
                            else 
                                "Nenhuma lista concluída ainda",
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
    
    val sheetState = rememberModalBottomSheetState()
    val editSheetState = rememberModalBottomSheetState()
    
    TrollyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = lista.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            supportingContent = {
                Text(
                    text = "Criada em ${ListaCompraFormatter.formatDate(lista.dataCriacao)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(TrollySpacing.xs)
                ) {
                    Text(
                        text = ListaCompraFormatter.formatarValor(lista.totalEstimado),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = { showBottomSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Mais opções",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            modifier = Modifier.clickable { onNavigate(lista.id) }
        )
    }

    // Bottom Sheet de opções
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(TrollySpacing.lg)
            ) {
                Text(
                    text = "Opções da Lista",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(TrollySpacing.md))
                
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
                        editedName = lista.name
                        showEditSheet = true
                    }
                )
                
                if (showConcluirButton) {
                    ListItem(
                        headlineContent = { Text("Marcar como concluída") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable {
                            showBottomSheet = false
                            onConcluir(lista)
                        }
                    )
                } else {
                    ListItem(
                        headlineContent = { Text("Marcar como ativa") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable {
                            showBottomSheet = false
                            onConcluir(lista)
                        }
                    )
                }
                
                ListItem(
                    headlineContent = { 
                        Text(
                            "Excluir",
                            color = MaterialTheme.colorScheme.error
                        ) 
                    },
                    leadingContent = {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    modifier = Modifier.clickable {
                        showBottomSheet = false
                        showDeleteDialog = true
                    }
                )
                
                Spacer(modifier = Modifier.height(TrollySpacing.md))
            }
        }
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
                    .padding(TrollySpacing.lg)
            ) {
                Text(
                    text = "Editar Lista",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(TrollySpacing.md))
                
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nome da lista") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = TrollyShapes.medium,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
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
                            if (editedName.isNotBlank()) {
                                val updatedLista = lista.copy(name = editedName)
                                onEdit(updatedLista)
                                showEditSheet = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = editedName.isNotBlank()
                    )
                }
                
                Spacer(modifier = Modifier.height(TrollySpacing.md))
            }
        }
    }
    
    // Dialog de confirmação de exclusão
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { 
                Text(
                    "Excluir lista",
                    style = MaterialTheme.typography.headlineSmall
                ) 
            },
            text = { 
                Text(
                    "Tem certeza que deseja excluir a lista \"${lista.name}\"? Esta ação não pode ser desfeita.",
                    style = MaterialTheme.typography.bodyMedium
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(lista)
                    }
                ) {
                    Text(
                        "Excluir",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListItemCard(
    lista: ListaCompra,
    onNavigate: (Int) -> Unit,
    onEdit: (ListaCompra) -> Unit,
    onDelete: (ListaCompra) -> Unit,
    onConcluir: (ListaCompra) -> Unit,
    showConcluirButton: Boolean
) {
    // ... existing code ...
} 