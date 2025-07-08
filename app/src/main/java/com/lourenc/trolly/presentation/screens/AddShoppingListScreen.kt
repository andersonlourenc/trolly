package com.lourenc.trolly.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lourenc.trolly.data.local.entity.ShoppingList
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListModal(
    onDismiss: () -> Unit,
    viewModel: ShoppingListViewModel
) {
    var name by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                .padding(16.dp)
            ) {
            // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                Text(
                    text = "Nova Lista",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar"
                    )
            }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campo nome
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome da lista") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão criar
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addShoppingList(
                            ShoppingList(
                                    name = name.trim()
                            )
                        )
                            onDismiss()
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Criar Lista")
        }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Mantendo a função original para compatibilidade
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListsScreen(navController: androidx.navigation.NavController, viewModel: ShoppingListViewModel) {
    AddListModal(
        onDismiss = { navController.popBackStack() },
        viewModel = viewModel
    )
}

// Função esperada pelo MainActivity
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShoppingListScreen(navController: androidx.navigation.NavController, viewModel: ShoppingListViewModel) {
    AddListModal(
        onDismiss = { navController.popBackStack() },
        viewModel = viewModel
    )
} 

// Função para uso como modal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShoppingListModal(
    onDismiss: () -> Unit,
    viewModel: ShoppingListViewModel
) {
    AddListModal(
        onDismiss = onDismiss,
        viewModel = viewModel
    )
} 