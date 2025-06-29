package com.lourenc.trolly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import com.lourenc.trolly.viewmodel.ListaCompraViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.lourenc.trolly.data.local.entity.ListaCompra
import androidx.compose.foundation.layout.Spacer as Spacer
import androidx.compose.foundation.clickable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.lourenc.trolly.utils.ListaCompraFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: ListaCompraViewModel) {
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
        val nomeCompleto = user.displayName ?: "Usuário"
        val photoUrl = user.photoUrl?.toString()
        val listas by viewModel.listasAtivas.observeAsState(emptyList())
        val mesAtual = viewModel.getMesAtualEmPortugues()
        val gastoMensal by viewModel.gastoMensal.observeAsState(0.0)
        val valorUltimaLista by viewModel.valorUltimaLista.observeAsState(0.0)
        val isLoading by viewModel.isLoading.observeAsState(false)
        val errorMessage by viewModel.errorMessage.observeAsState(null)
        
        // Observar mensagens de erro
        LaunchedEffect(errorMessage) {
            errorMessage?.let { message ->
                val result = snackbarHostState.showSnackbar(
                    message = message,
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
                if (result == SnackbarResult.Dismissed) {
                    viewModel.limparErro()
                }
            }
        }
        
        // Calcular os valores quando a tela for carregada
        LaunchedEffect(Unit) {
            viewModel.carregarListasAtivas()
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
                                text = "Olá, $nomeCompleto",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Resumo do mês de $mesAtual",
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cards de resumo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.BarChart,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Gasto mensal",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Text(
                                    viewModel.formatarValor(gastoMensal),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Última lista",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Text(
                                    viewModel.formatarValor(valorUltimaLista),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = true,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Listas") },
                        label = { Text("Listas") },
                        selected = false,
                        onClick = { navController.navigate("listas") }
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Suas Listas",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
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
                items(listas) { lista ->
                        ListaCard(
                        lista = lista,
                            onEdit = { updatedLista ->
                                viewModel.updateLista(updatedLista)
                        },
                        onDelete = { listaToDelete ->
                            viewModel.deleteLista(listaToDelete)
                            },
                            onNavigate = { listaId ->
                                navController.navigate("listaDetail/$listaId")
                            }
                        )
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCard(
    lista: ListaCompra,
    onEdit: (ListaCompra) -> Unit,
    onDelete: (ListaCompra) -> Unit,
    onNavigate: (Int) -> Unit
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
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                    text = ListaCompraFormatter.formatarNomeLista(lista.name),
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                    text = "Criada em ${ListaCompraFormatter.formatDate(lista.dataCriacao)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                if (lista.descricao.isNotBlank()) {
                    Text(
                        text = ListaCompraFormatter.formatarDescricao(lista.descricao),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    }
                }
            Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Mais opções",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
            }
        }
    }

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
                        editedName = lista.name
                        editedDescricao = lista.descricao
                        showEditSheet = true
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
                        showDeleteDialog = true
                    }
                )
            }
        }
    }
    
    // Modal de edição no estilo BottomSheet
    if (showEditSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            sheetState = editSheetState
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
                        text = "Editar Lista",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { showEditSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar"
                        )
                    }
                }

                Divider()
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nome da lista") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    OutlinedTextField(
                        value = editedDescricao,
                        onValueChange = { editedDescricao = it },
                        label = { Text("Descrição (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showEditSheet = false }) {
                            Text("Cancelar")
                        }
                        TextButton(
                            onClick = {
                                showEditSheet = false
                                if (editedName.isNotBlank()) {
                                    val updatedLista = lista.copy(
                                        name = editedName,
                                        descricao = editedDescricao
                                    )
                                    onEdit(updatedLista)
                                }
                            }
                        ) {
                            Text("Salvar")
                        }
                    }
                }
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
                        showDeleteDialog = false
                        onDelete(lista)
                    }
                ) {
                    Text("Excluir", color = Color(0xFFE53935))
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

// Função para obter o nome do mês atual em português
@Composable
fun getMesAtualEmPortugues(): String {
    val calendar = Calendar.getInstance()
    val meses = listOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", 
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )
    return meses[Calendar.getInstance().get(Calendar.MONTH)]
}

// Função para formatar valores monetários
private fun formatarValor(valor: Double): String {
    return String.format(Locale("pt", "BR"), "%.2f", valor).replace(".", ",")
}
