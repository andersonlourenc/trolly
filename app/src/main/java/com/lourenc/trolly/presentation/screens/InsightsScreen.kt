package com.lourenc.trolly.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import com.lourenc.trolly.presentation.theme.*
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.lourenc.trolly.utils.ShoppingListFormatter
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(navController: NavController, viewModel: ShoppingListViewModel) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        // Redirecionar para login se não estiver autenticado
        return
    }
    val name = user.displayName ?: "Usuário"
    val photoUrl = user.photoUrl?.toString()
    val monthlyExpense by viewModel.monthlyExpense.observeAsState(0.0)
    val lastListValue by viewModel.lastListValue.observeAsState(0.0)

    Scaffold(
        topBar = {
            TrollyTopBar(
                title = "Insights",
                subtitle = "Análises e statistics",

            )
        },
        bottomBar = {
            TrollyBottomNavigation(
                currentRoute = "insights",
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo("insights") { inclusive = true }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = TrollySpacing.md),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cards de valores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = TrollySpacing.lg, bottom = TrollySpacing.lg),
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
                            ShoppingListFormatter.formatValue(monthlyExpense),
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
                            ShoppingListFormatter.formatValue(lastListValue),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            // Conteúdo antigo de construção
            Spacer(modifier = Modifier.height(TrollySpacing.lg))
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "Em construção",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(TrollySpacing.lg))
            Text(
                text = "Página em Construção",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            Text(
                text = "Estamos trabalhando para trazer análises e insights incríveis sobre suas compras. Em breve você poderá ver estatísticas detalhadas sobre seus gastos e hábitos de consumo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = TrollySpacing.lg)
            )
            Spacer(modifier = Modifier.height(TrollySpacing.xl))
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
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Insights",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(TrollySpacing.md))
                    Text(
                        text = "Funcionalidades Futuras",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(TrollySpacing.sm))
                    Text(
                        text = "• Gráficos de gastos mensais\n• Categorias mais compradas\n• Comparativo entre meses\n• Dicas de economia\n• Relatórios personalizados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
} 