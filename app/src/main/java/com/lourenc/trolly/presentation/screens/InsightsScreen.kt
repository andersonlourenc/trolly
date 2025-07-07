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
    
    val name = user.displayName ?: "Usuário"
    val photoUrl = user.photoUrl?.toString()
    val monthlyExpense by viewModel.monthlyExpense.observeAsState(0.0)
    val lastListValue by viewModel.lastListValue.observeAsState(0.0)
    val activeLists by viewModel.activeLists.observeAsState(emptyList())
    val completedLists by viewModel.completedLists.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    
    // Debug: Log dos dados carregados
    LaunchedEffect(monthlyExpense, lastListValue, activeLists.size, completedLists.size) {
        println("DEBUG: Insights - Gasto mensal: $monthlyExpense")
        println("DEBUG: Insights - Última lista: $lastListValue")
        println("DEBUG: Insights - Listas ativas: ${activeLists.size}")
        println("DEBUG: Insights - Listas concluídas: ${completedLists.size}")
    }
    
    // Carregar dados quando a tela for exibida
    LaunchedEffect(Unit) {
        viewModel.loadLists() // Isso carrega tudo: listas ativas, concluídas, gasto mensal e última lista
    }
    
    // Dados calculados a partir das listas reais
    val totalLists = activeLists.size + completedLists.size
    val averageListValue = if (totalLists > 0) (monthlyExpense / totalLists) else 0.0
    
    // Animações
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = TrollySpacing.md),
                verticalArrangement = Arrangement.spacedBy(TrollySpacing.lg)
            ) {
            // Cards de estatísticas principais
            item {
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
            }
            
            // Análise detalhada de gastos
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
            
            // Dicas personalizadas
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    PersonalizedTipsCard(monthlyExpense, activeLists.size, completedLists.size)
                }
            }
            
            // Tendências e insights
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it * 1 })
                ) {
                    TrendsAndInsightsCard(activeLists.size, completedLists.size)
                }
            }
            
            // Espaçamento final
            item {
                Spacer(modifier = Modifier.height(TrollySpacing.xl))
            }
        }
        }
    }
}

@Composable
fun WelcomeHeader(name: String, photoUrl: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(TrollySpacing.lg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar do usuário
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUrl != null) {
                        // AsyncImage para foto do usuário
                        Text(
                            text = name.firstOrNull()?.uppercase() ?: "U",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(TrollySpacing.md))
                
                Column {
                    Text(
                        text = "Olá, $name! 👋",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Aqui estão seus insights de compras",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
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
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                ),
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "Última Lista",
                value = ShoppingListFormatter.formatValue(lastListValue),
                icon = Icons.Default.ShoppingCart,
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFf093fb),
                        Color(0xFFf5576c)
                    )
                ),
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
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4facfe),
                        Color(0xFF00f2fe)
                    )
                ),
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "Média por Lista",
                value = ShoppingListFormatter.formatValue(averageListValue),
                icon = Icons.Default.Analytics,
                gradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF43e97b),
                        Color(0xFF38f9d7)
                    )
                ),
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
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(TrollySpacing.md)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
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
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
            
            // Gráfico de barras comparativo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TrollySpacing.md),
                verticalAlignment = Alignment.Bottom
            ) {
                // Barra do gasto mensal
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
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF667eea),
                                        Color(0xFF764ba2)
                                    )
                                ),
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
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFf093fb),
                                        Color(0xFFf5576c)
                                    )
                                ),
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
                    color = Color(0xFF667eea)
                )
                
                ExpenseMetric(
                    title = "Última Lista",
                    value = ShoppingListFormatter.formatValue(lastListValue),
                    icon = Icons.Default.ShoppingCart,
                    color = Color(0xFFf093fb)
                )
            }
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            
            // Análise de padrões
            ExpensePatternAnalysis(
                monthlyExpense = monthlyExpense,
                lastListValue = lastListValue,
                activeLists = activeLists,
                completedLists = completedLists,
                totalLists = totalLists
            )
            
            Spacer(modifier = Modifier.height(TrollySpacing.md))
            
            // Comparação percentual
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
        
        // Análise de gastos
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
        
        // Análise de comparação
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
        
        // Análise de listas
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
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
            
            // Dicas baseadas no gasto mensal
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
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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