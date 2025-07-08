package com.lourenc.trolly.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lourenc.trolly.data.local.entity.ListItem
import com.lourenc.trolly.data.local.entity.MarketProduct
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModel
import com.lourenc.trolly.presentation.theme.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: ShoppingListViewModel,
    shoppingListId: Int
) {
    var searchTerm by remember { mutableStateOf("") }
    val filteredProducts by viewModel.filteredProducts.observeAsState(emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    SetStatusBarColor(Color.White, darkIcons = true)


    LaunchedEffect(Unit) {
        viewModel.searchProducts("")
    }

    Scaffold(
        topBar = {
            TrollyTopBar(
                title = "Adicionar Produtos",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = TrollySpacing.lg)
        ) {
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            
            TrollyTextField(
                value = searchTerm,
                onValueChange = {
                    searchTerm = it
                    viewModel.searchProducts(it)
                },
                label = "Buscar produto",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(TrollySpacing.sm),
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = TrollySpacing.lg)
            ) {
                items(filteredProducts) { product ->
                    ProductItemCard(
                        product = product,
                        onAdd = {
                            val item = ListItem(
                                shoppingListId = shoppingListId,
                                name = product.name,
                                quantity = 1,
                                unit = product.unit,
                                unitPrice = product.price,
                                purchased = false
                            )
                            viewModel.addOrIncrementListItem(item)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${product.name} adicionado à lista!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItemCard(
    product: MarketProduct,
    onAdd: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    TrollyCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TrollySpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = currencyFormat.format(product.price),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
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