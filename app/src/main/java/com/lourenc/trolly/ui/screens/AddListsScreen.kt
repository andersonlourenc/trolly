package com.lourenc.trolly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lourenc.trolly.data.local.entity.ListaCompra
import com.lourenc.trolly.viewmodel.ListaCompraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListModal(
    onDismiss: () -> Unit,
    viewModel: ListaCompraViewModel
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Campo descrição
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                ),
                minLines = 2,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botões
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addLista(
                                ListaCompra(
                                    name = name.trim(),
                                    descricao = description.trim()
                                )
                            )
                            onDismiss()
                        }
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("Criar Lista")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Mantendo a função original para compatibilidade
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddListsScreen(navController: androidx.navigation.NavController, viewModel: ListaCompraViewModel) {
    AddListModal(
        onDismiss = { navController.popBackStack() },
        viewModel = viewModel
    )
} 