import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.auth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var key by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Введите ключ", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = key,
            onValueChange = { key = it },
            label = { Text("Ключ") },
            isError = errorMessage.isNotEmpty()
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                scope.launch {
                    if (key.isBlank()) {
                        errorMessage = "Ключ не может быть пустым"
                        return@launch
                    }

                    try {
                        val success = auth(key)
                        if (success) {
                            errorMessage = ""
                            onLoginSuccess(key)
                        } else {
                            errorMessage = "Неверный ключ"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Ошибка: ${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Войти")
        }
    }
}
