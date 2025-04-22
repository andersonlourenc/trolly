import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TermsAndPrivacyPolicyScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Termos de Uso e Política de Privacidade") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Termos de Uso
            Text(
                text = "Termos de Uso",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
                1. Coleta de Dados
                Coletamos informações como nome, e-mail e listas de compras para personalizar sua experiência.

                2. Uso das Informações
                As informações são usadas apenas para o funcionamento do aplicativo. Não vendemos ou compartilhamos seus dados com terceiros.

                3. Armazenamento
                Seus dados são armazenados com segurança através dos serviços do Firebase.

                4. Direitos do Usuário
                Você pode solicitar a exclusão ou alteração de seus dados a qualquer momento.

                5. Alterações
                Podemos atualizar esta política periodicamente. Recomendamos que você revise esta página com frequência.

                Ao continuar utilizando o Trolly, você concorda com esta política.
                """.trimIndent(),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Política de Privacidade
            Text(
                text = "Política de Privacidade",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = """
                1. Coleta de Dados
                Coletamos informações como nome, e-mail e listas de compras para personalizar sua experiência.

                2. Uso das Informações
                As informações são usadas apenas para o funcionamento do aplicativo. Não vendemos ou compartilhamos seus dados com terceiros.

                3. Armazenamento
                Seus dados são armazenados com segurança através dos serviços do Firebase.

                4. Direitos do Usuário
                Você pode solicitar a exclusão ou alteração de seus dados a qualquer momento.

                5. Alterações
                Podemos atualizar esta política periodicamente. Recomendamos que você revise esta página com frequência.

                Ao continuar utilizando o Trolly, você concorda com esta política.
                """.trimIndent(),
                fontSize = 16.sp
            )
        }
    }
}
