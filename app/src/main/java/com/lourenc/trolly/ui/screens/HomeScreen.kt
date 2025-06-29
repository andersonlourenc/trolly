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
import com.lourenc.trolly.ui.theme.*
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
                TrollyTopBar(
                    title = "Olá, $nomeCompleto",
                    subtitle = "Resumo do mês de $mesAtual",
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = TrollySpacing.md),
                verticalArrangement = Arrangement.spacedBy(TrollySpacing.sm)
            ) {
                item {
                    Spacer(modifier = Modifier.height(TrollySpacing.md))
                    
                    // Cards de resumo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(TrollySpacing.sm)
                    ) {
                        TrollyCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(TrollySpacing.sm),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.BarChart,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(TrollySpacing.xs))
                                    Text(
                                        "Gasto mensal",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                                Text(
                                    viewModel.formatarValor(gastoMensal),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        TrollyCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(TrollySpacing.sm),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(TrollySpacing.xs))
                                    Text(
                                        "Última lista",
                                        style = MaterialTheme.typography.labelMedium
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
                                    Text(
                                        text = viewModel.formatarValor(lista.totalEstimado),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier.clickable {
                                    navController.navigate("listaDetail/${lista.id}")
                                }
                            )
                        }
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListModal(
    onDismiss: () -> Unit,
    onAddList: (String) -> Unit
) {
    var nomeLista by remember { mutableStateOf("") }
    val modalState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalState
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
            
            TrollyTextField(
                value = nomeLista,
                onValueChange = { nomeLista = it },
                label = "Nome da lista"
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
                        if (nomeLista.isNotBlank()) {
                            onAddList(nomeLista)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = nomeLista.isNotBlank()
                )
            }
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
        }
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
