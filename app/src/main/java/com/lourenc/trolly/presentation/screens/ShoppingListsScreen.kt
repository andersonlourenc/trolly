package com.lourenc.trolly.presentation.screens
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
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.lourenc.trolly.data.local.entity.ShoppingList
import com.google.firebase.auth.FirebaseAuth
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.lourenc.trolly.utils.ShoppingListFormatter
import com.lourenc.trolly.presentation.theme.*
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListsScreen(navController: NavController, viewModel: ShoppingListViewModel) {
    val user = FirebaseAuth.getInstance().currentUser
    var selectedTab by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showAddListModal by remember { mutableStateOf(false) }
    
    if (user == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("shoppingLists") { inclusive = true }
            }
        }
    } else {
        val fullName = user.displayName ?: "Usuário"
        val photoUrl = user.photoUrl?.toString()
        val activeLists by viewModel.activeLists.observeAsState(emptyList())
        val completedLists by viewModel.completedLists.observeAsState(emptyList())
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
                    viewModel.clearError()
                }
            }
        }
        
        // Carregar listas quando a tela for carregada
        LaunchedEffect(Unit) {
            viewModel.loadActiveLists()
            viewModel.loadCompletedLists()
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
                    currentRoute = "shoppingLists",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("shoppingLists") { inclusive = true }
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
                                "Ativas (${activeLists.size})",
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
                                "Concluídas (${completedLists.size})",
                                style = MaterialTheme.typography.labelMedium
                            ) 
                        },
                        icon = { Icon(Icons.Default.History, contentDescription = null) }
                    )
                }
                
                // Conteúdo das tabs
                when (selectedTab) {
                    0 -> ShoppingListsTabContent(
                        shoppingLists = activeLists,
                        isLoading = isLoading,
                        onNavigate = { listId -> navController.navigate("shoppingListDetail/$listId") },
                        onEdit = { viewModel.updateShoppingList(it) },
                        onDelete = { viewModel.deleteShoppingList(it) },
                        onComplete = { viewModel.markListAsCompleted(it.id) },
                        showCompleteButton = true,
                        viewModel = viewModel
                    )
                    1 -> ShoppingListsTabContent(
                        shoppingLists = completedLists,
                        isLoading = isLoading,
                        onNavigate = { listId -> navController.navigate("shoppingListDetail/$listId") },
                        onEdit = { viewModel.updateShoppingList(it) },
                        onDelete = { viewModel.deleteShoppingList(it) },
                        onComplete = { viewModel.markListAsActive(it.id) },
                        showCompleteButton = false,
                        viewModel = viewModel
                    )
                }
            }
        }
        
        // Modal de adicionar lista
        if (showAddListModal) {
            AddShoppingListModal(
                onDismiss = { showAddListModal = false },
                onAddList = { name ->
                    viewModel.addShoppingList(ShoppingList(name = name))
                    showAddListModal = false
                }
            )
        }
    }
}

@Composable
fun ShoppingListsTabContent(
    shoppingLists: List<ShoppingList>,
    isLoading: Boolean,
    onNavigate: (Int) -> Unit,
    onEdit: (ShoppingList) -> Unit,
    onDelete: (ShoppingList) -> Unit,
    onComplete: (ShoppingList) -> Unit,
    showCompleteButton: Boolean,
    viewModel: ShoppingListViewModel
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
        } else if (shoppingLists.isEmpty()) {
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
                            text = if (showCompleteButton) "Nenhuma lista ativa" else "Nenhuma lista concluída",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = if (showCompleteButton) 
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
            items(shoppingLists) { shoppingList ->
                ShoppingListCard(
                    shoppingList = shoppingList,
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onNavigate = onNavigate,
                    onComplete = onComplete,
                    showCompleteButton = showCompleteButton,
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListCard(
    shoppingList: ShoppingList,
    onEdit: (ShoppingList) -> Unit,
    onDelete: (ShoppingList) -> Unit,
    onNavigate: (Int) -> Unit,
    onComplete: (ShoppingList) -> Unit,
    showCompleteButton: Boolean,
    viewModel: ShoppingListViewModel
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(shoppingList.name) }
    var listValue by remember { mutableStateOf("") }
    
    val sheetState = rememberModalBottomSheetState()
    val editSheetState = rememberModalBottomSheetState()
    
    // Calcular valor da lista baseado no status
    LaunchedEffect(shoppingList) {
        if (shoppingList.status == "COMPLETED") {
            viewModel.calculateRealListValueAsync(shoppingList.id) { realValue ->
                listValue = viewModel.formatValueWithDash(realValue)
            }
        } else {
            listValue = ""
        }
    }
    
    TrollyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = shoppingList.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            supportingContent = {
                Text(
                    text = "Criada em ${viewModel.formatDate(shoppingList.creationDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(TrollySpacing.xs)
                ) {
                    if (listValue.isNotEmpty()) {
                        Text(
                            text = listValue,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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
            modifier = Modifier.clickable { onNavigate(shoppingList.id) }
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
                        editedName = shoppingList.name
                        showEditSheet = true
                    }
                )
                
                if (showCompleteButton) {
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
                            onComplete(shoppingList)
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
                            onComplete(shoppingList)
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
                                val updatedList = shoppingList.copy(name = editedName)
                                onEdit(updatedList)
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
                    "Tem certeza que deseja excluir a lista \"${shoppingList.name}\"? Esta ação não pode ser desfeita.",
                    style = MaterialTheme.typography.bodyMedium
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete(shoppingList)
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
fun AddShoppingListModal(
    onDismiss: () -> Unit,
    onAddList: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TrollySpacing.lg)
        ) {
            Text(
                text = "Nova Lista",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
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
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
                TrollyPrimaryButton(
                    text = "Criar",
                    onClick = {
                        if (name.isNotBlank()) {
                            onAddList(name)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank()
                )
            }
            Spacer(modifier = Modifier.height(TrollySpacing.md))
        }
    }
} 