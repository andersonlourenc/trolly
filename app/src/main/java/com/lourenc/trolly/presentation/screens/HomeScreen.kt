package com.lourenc.trolly.presentation.screens


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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
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
import androidx.compose.foundation.Image
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.res.painterResource
import com.lourenc.trolly.R
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.layout.ContentScale
import com.google.accompanist.pager.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import com.lourenc.trolly.presentation.theme.CoralPrimary
import com.lourenc.trolly.presentation.theme.EditBlue
import com.lourenc.trolly.presentation.theme.CompleteGreen
import com.lourenc.trolly.presentation.screens.AddShoppingListModal

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: ShoppingListViewModel) {
    val context = LocalContext.current
    
    SideEffect {
        val window = (context as? Activity)?.window
        @Suppress("DEPRECATION")
        window?.statusBarColor = CoralPrimary.toArgb()
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
        return
    }
        val name = user.displayName ?: "Usuário"
        val photoUrl = user.photoUrl?.toString()
        val activeLists by viewModel.activeLists.observeAsState(emptyList())
        val currentMonth = viewModel.getCurrentMonthInPortuguese()
        val monthlyExpense by viewModel.monthlyExpense.observeAsState(0.0)
        val lastListValue by viewModel.lastListValue.observeAsState(0.0)
        val isLoading by viewModel.isLoading.observeAsState(false)
        val errorMessage by viewModel.errorMessage.observeAsState(null)
        
    val carouselImages = listOf(R.drawable.churras, R.drawable.niver, R.drawable.receita)
    val pagerState = rememberPagerState()
    val cardHeight = 200.dp
    val cardOffset = -(cardHeight.value * 0.4).dp
        
        
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
            viewModel.loadActiveLists()
            viewModel.loadCompletedLists()
            viewModel.calculateMonthlyExpense()
            viewModel.calculateLastListValue()

        }

        val lists = activeLists

    Box(modifier = Modifier.fillMaxSize()) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            color = CoralPrimary,
            shadowElevation = 4.dp,
            shape = RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = TrollySpacing.md, vertical = 24.dp)
                    .offset(y= -48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Olá, $name",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = 140.dp)
                .zIndex(1f)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight),
                shape = RoundedCornerShape(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                HorizontalPager(
                    count = carouselImages.size,
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight),
                    itemSpacing = 16.dp
                ) { page ->
                    Image(
                        painter = painterResource(id = carouselImages[page]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardHeight)
                            .clip(RoundedCornerShape(32.dp))
                            .clickable { /* ação futura */ }
                    )
                }
            }

            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 12.dp),
                activeColor = MaterialTheme.colorScheme.primary,
                inactiveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
        }


        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            containerColor = Color.Transparent,
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {

                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar lista",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },

        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(
                        top = 350.dp,
                        start = TrollySpacing.md,
                        end = TrollySpacing.md
                    )

            ) {
                        Text(
                            text = "Suas Listas",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(TrollySpacing.sm))

                    if (isLoading) {
                            Text(
                                text = "Carregando...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                    } else if (lists.isEmpty()) {
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
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "Toque no botão + para criar sua primeira lista",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(TrollySpacing.md)
                        ) {
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
            AddShoppingListModal(
                onDismiss = { showAddListModal = false },
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
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            supportingContent = {
                Text(
                    text = "Criada em ${ShoppingListFormatter.formatDate(shoppingList.creationDate)}",
                    style = MaterialTheme.typography.bodyMedium,
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
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            modifier = Modifier
                .clickable { onNavigate(shoppingList.id) }
                .padding(vertical = 8.dp)
        )
    }

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
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(TrollySpacing.md))
                
                ListItem(
                    headlineContent = { Text("Editar") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = EditBlue
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
                                tint = CompleteGreen
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
                    style = MaterialTheme.typography.headlineMedium,
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
