package com.lourenc.trolly.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.lourenc.trolly.presentation.theme.*
import com.lourenc.trolly.presentation.viewmodel.ShoppingListViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.lourenc.trolly.utils.ShoppingListFormatter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(navController: NavController, viewModel: ShoppingListViewModel) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user == null) {
        return
    }

    SetStatusBarColor(Color.White, darkIcons = true)

    val name = user.displayName ?: "Usuário"
    val photoUrl = user.photoUrl?.toString()
    val monthlyExpense by viewModel.monthlyExpense.observeAsState(0.0)
    val lastListValue by viewModel.lastListValue.observeAsState(0.0)
    val activeLists by viewModel.activeLists.observeAsState(emptyList())
    val completedLists by viewModel.completedLists.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    

    LaunchedEffect(monthlyExpense, lastListValue, activeLists.size, completedLists.size) {
        println("DEBUG: Insights - Gasto mensal: $monthlyExpense")
        println("DEBUG: Insights - Última lista: $lastListValue")
        println("DEBUG: Insights - Listas ativas: ${activeLists.size}")
        println("DEBUG: Insights - Listas concluídas: ${completedLists.size}")
    }
    

    LaunchedEffect(Unit) {
        viewModel.loadLists()
    }
    

    val totalLists = activeLists.size + completedLists.size
    val averageListValue = if (totalLists > 0) (monthlyExpense / totalLists) else 0.0
    

    val infiniteTransition = rememberInfiniteTransition(label = "insights")
    val pulseAnimation by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Scaffold(
        topBar = {
            TrollyTopBar(
                title = "Insights",
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
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
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(TrollySpacing.md))
                    Text(
                        text = "Carregando insights...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                    .padding(horizontal = TrollySpacing.md)
            ) {
                // Cards de estatísticas fixos no topo
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInHorizontally()
                ) {
                    StatisticsCards(
                        monthlyExpense = monthlyExpense,
                        lastListValue = lastListValue,
                        totalLists = totalLists,
                        averageListValue = averageListValue
                    )
                }
                
                Spacer(modifier = Modifier.height(TrollySpacing.xl))
                
                // Conteúdo rolável
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(TrollySpacing.lg)
                ) {
            

                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically()
                        ) {
                            DetailedExpenseAnalysis(
                                monthlyExpense = monthlyExpense,
                                lastListValue = lastListValue,
                                activeLists = activeLists.size,
                                completedLists = completedLists.size,
                                totalLists = totalLists
                            )
                        }
                    }
                    

                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically()
                        ) {
                            PersonalizedTipsCard(monthlyExpense, activeLists.size, completedLists.size)
                        }
                    }
                    

                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it * 1 })
                        ) {
                            TrendsAndInsightsCard(activeLists.size, completedLists.size)
                        }
                    }
                    

                    item {
                        Spacer(modifier = Modifier.height(TrollySpacing.xl))
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticsCards(
    monthlyExpense: Double,
    lastListValue: Double,
    totalLists: Int,
    averageListValue: Double
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(TrollySpacing.md)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(TrollySpacing.md)
        ) {
            StatCard(
                title = "Gasto Mensal",
                value = ShoppingListFormatter.formatValue(monthlyExpense),
                icon = Icons.Default.TrendingUp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "Última Lista",
                value = ShoppingListFormatter.formatValue(lastListValue),
                icon = Icons.Default.ShoppingCart,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(TrollySpacing.md)
        ) {
            StatCard(
                title = "Total de Listas",
                value = "$totalLists",
                icon = Icons.Default.List,
                color = Color(0xFF4facfe),
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "Média por Lista",
                value = ShoppingListFormatter.formatValue(averageListValue),
                icon = Icons.Default.Analytics,
                color = Color(0xFF43e97b),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(TrollySpacing.md),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                            Icon(
                    imageVector = icon,
                                contentDescription = null,
                    tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(TrollySpacing.xs))
                            Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            }
            
                        Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DetailedExpenseAnalysis(
    monthlyExpense: Double,
    lastListValue: Double,
    activeLists: Int,
    completedLists: Int,
    totalLists: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TrollySpacing.lg)
        ) {
            Text(
                text = "Análise Detalhada de Gastos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TrollySpacing.md),
                verticalAlignment = Alignment.Bottom
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val maxHeight = 120f
                    val heightRatio = if (monthlyExpense > 0) (monthlyExpense / (monthlyExpense + lastListValue)).toFloat().coerceIn(0.1f, 1f) else 0.1f
                    
                    Box(
                    modifier = Modifier
                            .width(40.dp)
                            .height((maxHeight * heightRatio).dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                    
                    Spacer(modifier = Modifier.height(TrollySpacing.xs))
                    Text(
                        text = "Mensal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                // Barra da última lista
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val maxHeight = 120f
                    val heightRatio = if (lastListValue > 0) (lastListValue / (monthlyExpense + lastListValue)).toFloat().coerceIn(0.1f, 1f) else 0.1f
                    
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height((maxHeight * heightRatio).dp)
                            .background(
                                MaterialTheme.colorScheme.secondary,
                                RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                    
                    Spacer(modifier = Modifier.height(TrollySpacing.xs))
                    Text(
                        text = "Última",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(TrollySpacing.lg))
            
            // Métricas detalhadas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExpenseMetric(
                    title = "Gasto Mensal",
                    value = ShoppingListFormatter.formatValue(monthlyExpense),
                    icon = Icons.Default.TrendingUp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                ExpenseMetric(
                    title = "Última Lista",
                    value = ShoppingListFormatter.formatValue(lastListValue),
                    icon = Icons.Default.ShoppingCart,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            

            ExpensePatternAnalysis(
                monthlyExpense = monthlyExpense,
                lastListValue = lastListValue,
                activeLists = activeLists,
                completedLists = completedLists,
                totalLists = totalLists
            )
            
                    Spacer(modifier = Modifier.height(TrollySpacing.md))

            if (monthlyExpense > 0 && lastListValue > 0) {
                val percentage = ((lastListValue / monthlyExpense) * 100).toFloat()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Última lista representa",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${percentage.roundToInt()}% do gasto mensal",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseMetric(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(TrollySpacing.xs))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ExpensePatternAnalysis(
    monthlyExpense: Double,
    lastListValue: Double,
    activeLists: Int,
    completedLists: Int,
    totalLists: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Padrões Identificados",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
                    Spacer(modifier = Modifier.height(TrollySpacing.sm))
        
        val patterns = mutableListOf<String>()

        when {
            monthlyExpense > 1000 -> {
                patterns.add("💰 Gasto mensal alto - considere compras em atacado")
            }
            monthlyExpense > 500 -> {
                patterns.add("📊 Gasto mensal moderado - bem equilibrado")
            }
            else -> {
                patterns.add("🎉 Gasto mensal baixo - excelente controle!")
            }
        }
        

        if (monthlyExpense > 0 && lastListValue > 0) {
            when {
                lastListValue > monthlyExpense * 0.8 -> {
                    patterns.add("⚠️ Última lista muito alta - revise itens")
                }
                lastListValue < monthlyExpense * 0.2 -> {
                    patterns.add("✅ Última lista econômica - continue assim!")
                }
                else -> {
                    patterns.add("📈 Última lista proporcional ao padrão")
                }
            }
        }
        

        when {
            activeLists > completedLists -> {
                patterns.add("📝 Mais listas ativas - finalize algumas")
            }
            completedLists > activeLists -> {
                patterns.add("🎯 Boa conclusão de listas")
            }
            else -> {
                patterns.add("⚖️ Equilíbrio entre listas ativas e concluídas")
            }
        }
        
        patterns.forEach { pattern ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = TrollySpacing.xs)
            ) {
                Text(
                    text = pattern,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun PersonalizedTipsCard(monthlyExpense: Double, activeLists: Int, completedLists: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TrollySpacing.lg)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(TrollySpacing.sm))
                Text(
                    text = "Dicas Personalizadas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            
            val tips = mutableListOf<String>()
            

            when {
                monthlyExpense > 1000 -> {
                    tips.add("💡 Considere fazer compras em atacado para economizar")
                    tips.add("📊 Faça um orçamento mensal para controlar gastos")
                }
                monthlyExpense > 500 -> {
                    tips.add("✅ Seus gastos estão equilibrados")
                    tips.add("🛒 Compare preços entre mercados")
                }
                else -> {
                    tips.add("🎉 Parabéns! Você está economizando muito!")
                    tips.add("💪 Continue com essa disciplina")
                }
            }
            
            // Dicas baseadas no número de listas
            when {
                activeLists > completedLists -> {
                    tips.add("📝 Tente fazer listas menores e mais específicas")
                    tips.add("⏰ Defina horários fixos para as compras")
                }
                completedLists > activeLists -> {
                    tips.add("👍 Você está progredindo bem!")
                    tips.add("📋 Revise suas listas antes de sair")
                }
                else -> {
                    tips.add("🏆 Você é um mestre da organização!")
                    tips.add("🌟 Compartilhe suas dicas com outros usuários")
                }
            }
            
            tips.take(3).forEach { tip ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = TrollySpacing.xs)
                ) {
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun TrendsAndInsightsCard(activeLists: Int, completedLists: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TrollySpacing.lg)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(TrollySpacing.sm))
                Text(
                    text = "Tendências e Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InsightItem(
                    icon = Icons.Default.Pending,
                    title = "Listas Ativas",
                    value = "$activeLists",
                    color = MaterialTheme.colorScheme.primary
                )
                
                InsightItem(
                    icon = Icons.Default.CheckCircle,
                    title = "Concluídas",
                    value = "$completedLists",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            
            // Insight baseado nos dados
            val insight = when {
                activeLists > completedLists -> {
                    "📈 Você tem mais listas ativas que concluídas. Que tal finalizar algumas?"
                }
                completedLists > activeLists -> {
                    "🎯 Excelente! Você está concluindo mais listas do que criando novas."
                }
                activeLists == 0 && completedLists == 0 -> {
                    "🚀 Comece criando sua primeira lista de compras!"
                }
                else -> {
                    "⚖️ Você está mantendo um bom equilíbrio entre listas ativas e concluídas."
                }
            }
            
            Text(
                text = insight,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun InsightItem(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(TrollySpacing.xs))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
} 