package com.lourenc.trolly.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lourenc.trolly.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    Scaffold(
        topBar = {

            Box {
                Image(
                    painter = painterResource(id = R.drawable.backgroundtitlebar),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp),
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
                                imageVector = Icons.Default.Notifications, // ícone de sino
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
            }
        },
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp) //
            ) {


            }
        }
    )


}



