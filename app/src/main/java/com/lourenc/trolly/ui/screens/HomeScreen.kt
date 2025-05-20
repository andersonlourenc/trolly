package com.lourenc.trolly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lourenc.trolly.R
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.res.painterResource



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var user = FirebaseAuth.getInstance().currentUser

    val nomeCompleto = user?.displayName ?: "Usuário"
    val primeiroNome = nomeCompleto.split(" ").firstOrNull() ?: "Usuário"
    val photoUrl = user?.photoUrl?.toString()

    Box(modifier = Modifier.fillMaxSize()) {


        Image(
            painter = painterResource(id = R.drawable.backgroundtitlebar),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp),
            contentScale = ContentScale.Crop
        )

        CenterAlignedTopAppBar(
            title = {
                Text("Home", style = MaterialTheme.typography.titleMedium)
            },
            actions = {
                IconButton(onClick = {

                }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificações"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )


        Column(
            modifier = Modifier
                .padding(top = 120.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,

            ) {
                Text(text = "Oi, $primeiroNome!", style = MaterialTheme.typography.headlineMedium)

                if (photoUrl != null){
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Foto do usuário",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle, contentDescription = "Foto do usuário",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                        
                    )
                }
            }

            SummaryLabel(month = "Maio")

            MonthlySummaryCards(expense = "400,00", lastListValue = "105,00")

        }
    }
}


@Composable
fun SummaryLabel(month: String) {
    Text(
        text = "Resumo do mês de $month",
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun MonthlySummaryCards(expense: String, lastListValue: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),


    ) {
        SummaryCard(
            icon = Icons.Default.BarChart,
            title = "Despesa mensal",
            value = expense,

        )

        SummaryCard(
            icon = Icons.Default.ShoppingCart,
            title = "Última lista",
            value = lastListValue
        )
    }
}

@Composable
fun SummaryCard(icon: ImageVector, title: String, value: String) {
    Card(
        modifier = Modifier
            .width(182.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.labelMedium)
            }
            Text(value, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}




