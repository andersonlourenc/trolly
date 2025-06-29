package com.lourenc.trolly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lourenc.trolly.data.local.entity.ItemLista
import com.lourenc.trolly.data.repository.ProdutoMercado
import com.lourenc.trolly.viewmodel.ListaCompraViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    viewModel: ListaCompraViewModel,
    listaId: Int
) {
    var searchTerm by remember { mutableStateOf("") }
    val produtosFiltrados by viewModel.produtosFiltrados.observeAsState(emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Inicializar com alguns produtos sugeridos
    LaunchedEffect(Unit) {
        viewModel.pesquisarProdutos("")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Produtos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchTerm,
                onValueChange = {
                    searchTerm = it
                    viewModel.pesquisarProdutos(it)
                },
                label = { Text("Buscar produto") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(produtosFiltrados) { produto ->
                    ProductItemCard(
                        produto = produto,
                        onAdd = {
                            val item = ItemLista(
                                idLista = listaId,
                                name = produto.nome,
                                quantidade = 1,
                                unidade = produto.unidade,
                                precoUnitario = produto.preco,
                                comprado = false
                            )
                            viewModel.adicionarItemLista(item)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "${produto.nome} adicionado!",
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