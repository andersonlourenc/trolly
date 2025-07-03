package com.lourenc.trolly.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.lourenc.trolly.data.local.entity.ShoppingList
import androidx.compose.foundation.layout.Spacer as Spacer
import androidx.compose.foundation.clickable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.lourenc.trolly.utils.ShoppingListFormatter
import com.lourenc.trolly.presentation.theme.*
import java.util.*
import androidx.compose.material3.ListItem
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.toArgb
import android.app.Activity
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: ShoppingListViewModel) {
    val blue = MaterialTheme.colorScheme.primary
    val context = LocalContext.current
    
    SideEffect {
        // Controle nativo da cor da status bar
        val window = (context as? Activity)?.window
        window?.statusBarColor = blue.toArgb()
    }
    val user = FirebaseAuth.getInstance().currentUser
    var activeCardId by remember { mutableStateOf<Int?>(null) }
    var showAddListModal by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    if (user == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    } else {
        val name = user.displayName ?: "Usuário"
        val photoUrl = user.photoUrl?.toString()
        val activeLists by viewModel.activeLists.observeAsState(emptyList())
        val currentMonth = viewModel.getCurrentMonthInPortuguese()
        val monthlyExpense by viewModel.monthlyExpense.observeAsState(0.0)
        val lastListValue by viewModel.lastListValue.observeAsState(0.0)
        val isLoading by viewModel.isLoading.observeAsState(false)
        val errorMessage by viewModel.errorMessage.observeAsState(null)
        
        // Logs para debug
        LaunchedEffect(monthlyExpense) {
            println("DEBUG: HomeScreen - Gasto mensal atualizado: R$ $monthlyExpense")
        }
        
        LaunchedEffect(lastListValue) {
            println("DEBUG: HomeScreen - Valor última lista atualizado: R$ $lastListValue")
        }
        
        LaunchedEffect(errorMessage) {
            errorMessage?.let { message ->
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
                if (result == SnackbarResult.Dismissed) {
                    viewModel.clearError()
                }
            }
        }
        

        LaunchedEffect(Unit) {
            println("DEBUG: HomeScreen - Carregando dados...")
            viewModel.loadActiveLists()
            viewModel.loadCompletedLists()
            viewModel.calculateMonthlyExpense()
            viewModel.calculateLastListValue()
            println("DEBUG: HomeScreen - Dados carregados")
        }

        val lists = activeLists
        val month = currentMonth
        val expense = monthlyExpense
        val lastValue = lastListValue

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                TrollyBottomNavigation(
                    currentRoute = "home",
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("home") { inclusive = true }
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
                // Painel azul com TopBar e cards
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(bottom = 56.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = TrollySpacing.md)
                            .padding(top = 0.dp, bottom = 0.dp)
                    ) {
                        // TopBar customizada
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Olá, $name",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = "Resumo do mês de $month",
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
                }
                // Cards de resumo sobrepostos à barra azul
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TrollySpacing.md)
                        .offset(y = (0).dp),
                    horizontalArrangement = Arrangement.spacedBy(TrollySpacing.md)
                ) {
                    TrollyCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(85.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(TrollySpacing.md),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(TrollySpacing.xs))
                                Text(
                                    "Gasto mensal",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                ShoppingListFormatter.formatValue(expense),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    TrollyCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(85.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(TrollySpacing.md),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(TrollySpacing.xs))
                                Text(
                                    "Última lista",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                            Text(
                                ShoppingListFormatter.formatValue(lastValue),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                // Lista de listas logo após o painel azul
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = TrollySpacing.md),
                    verticalArrangement = Arrangement.spacedBy(TrollySpacing.sm)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(TrollySpacing.lg))
                        Text(
                            text = "Suas Listas",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(TrollySpacing.sm))
                    }
                    if (isLoading) {
                        item {
                            Text(
                                text = "Carregando...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    } else if (lists.isEmpty()) {
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
                                        text = "Nenhuma lista criada",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "Toque no botão + para criar sua primeira lista",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    } else {
                        items(lists) { shoppingList ->
                            HomeListCard(
                                shoppingList = shoppingList,
                                onEdit = { updatedList: ShoppingList ->
                                    viewModel.updateShoppingList(updatedList)
                                },
                                onDelete = { listToDelete: ShoppingList ->
                                    viewModel.deleteShoppingList(listToDelete)
                                },
                                onNavigate = { listId: Int ->
                                    navController.navigate("shoppingListDetail/$listId")
                                },
                                onComplete = { listToComplete: ShoppingList ->
                                    viewModel.markListAsCompleted(listToComplete.id)
                                },
                                showCompleteButton = true
                            )
                        }
                    }
                }
            }
        }
        

        if (showAddListModal) {
            AddShoppingListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeListCard(
    shoppingList: ShoppingList,
    onEdit: (ShoppingList) -> Unit,
    onDelete: (ShoppingList) -> Unit,
    onNavigate: (Int) -> Unit,
    onComplete: (ShoppingList) -> Unit,
    showCompleteButton: Boolean
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(shoppingList.name) }
    
    val sheetState = rememberModalBottomSheetState()
    val editSheetState = rememberModalBottomSheetState()
    
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
                    text = "Criada em ${ShoppingListFormatter.formatDate(shoppingList.creationDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            },
            trailingContent = {
                IconButton(
                    onClick = { showBottomSheet = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            modifier = Modifier.clickable { onNavigate(shoppingList.id) }
        )
    }

    // Bottom Sheet de opções
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
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
                    text = "Editar Lista",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(TrollySpacing.md))
                
                TrollyTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = "Nome da lista"
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
            title = { Text("Excluir Lista") },
            text = { Text("Tem certeza que deseja excluir a lista \"${shoppingList.name}\"? Esta ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(shoppingList)
                        showDeleteDialog = false
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

@Composable
fun getCurrentMonthInPortuguese(): String {
    val calendar = Calendar.getInstance()
    val meses = listOf(
        "January", "February", "March", "April", "May", "June", 
        "July", "August", "September", "October", "November", "December"
    )
    return meses[Calendar.getInstance().get(Calendar.MONTH)]
}


private fun formatarValor(valor: Double): String {
    return String.format(Locale("pt", "BR"), "%.2f", valor).replace(".", ",")
}
