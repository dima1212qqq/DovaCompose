import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.addItemToShoppingList
import com.example.myapplication.api.createShoppingList
import com.example.myapplication.api.crossItemOff
import com.example.myapplication.api.removeItemFromList
import com.example.myapplication.api.removeShoppingList
import com.example.myapplication.data.ShoppingItem
import com.example.myapplication.data.ShoppingList
import com.example.myapplication.data.getAllMyShopLists
import com.example.myapplication.data.getShoppingList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    userKey: String,
    onListSelected: (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    var shopLists by remember { mutableStateOf<List<ShoppingList>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }
    var newListName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Списки покупок") },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            try {
                                shopLists = getAllMyShopLists(userKey)
                                errorMessage = ""
                            } catch (e: Exception) {
                                errorMessage = "Ошибка загрузки: ${e.message}"
                            }
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = newListName,
                onValueChange = { newListName = it },
                label = { Text("Название нового списка") }
            )
            Button(onClick = {
                scope.launch {
                    try {
                        val (success, _) = createShoppingList(userKey, newListName)
                        if (success) shopLists = getAllMyShopLists(userKey)
                        newListName = ""
                    } catch (e: Exception) {
                        errorMessage = "Ошибка создания списка: ${e.message}"
                    }
                }
            }) {
                Text("Создать список")
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(shopLists) { list ->
                    ShoppingListItem(
                        list = list,
                        onLoad = { onListSelected(list.id) },
                        onDelete = {
                            scope.launch {
                                try {
                                    if (removeShoppingList(list.id)) {
                                        shopLists = getAllMyShopLists(userKey)
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Ошибка удаления: ${e.message}"
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListDetailsScreen(
    listId: Int,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedItems by remember { mutableStateOf<List<ShoppingItem>>(emptyList()) }
    var newItemName by remember { mutableStateOf("") }
    var newItemQuantity by remember { mutableStateOf("1") }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(listId) {
        try {
            selectedItems = getShoppingList(listId)
        } catch (e: Exception) {
            errorMessage = "Ошибка загрузки: ${e.message}"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список товаров") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            OutlinedTextField(
                value = newItemName,
                onValueChange = { newItemName = it },
                label = { Text("Название товара") }
            )
            OutlinedTextField(
                value = newItemQuantity,
                onValueChange = { newItemQuantity = it },
                label = { Text("Количество") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(onClick = {
                scope.launch {
                    try {
                        val (success, _) = addItemToShoppingList(
                            listId, newItemName, newItemQuantity.toInt()
                        )
                        if (success) selectedItems = getShoppingList(listId)
                        newItemName = ""
                        newItemQuantity = "1"
                    } catch (e: Exception) {
                        errorMessage = "Ошибка добавления: ${e.message}"
                    }
                }
            }) {
                Text("Добавить товар")
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(selectedItems) { item ->
                    ShoppingItemCard(
                        item = item,
                        onCross = {
                            scope.launch {
                                try {
                                    if (crossItemOff(item.id)) {
                                        selectedItems = getShoppingList(listId)
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Ошибка вычеркивания: ${e.message}"
                                }
                            }
                        },
                        onDelete = {
                            scope.launch {
                                try {
                                    if (removeItemFromList(listId, item.id)) {
                                        selectedItems = getShoppingList(listId)
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Ошибка удаления: ${e.message}"
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun ShoppingListItem(
    list: ShoppingList,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Список: ${list.name}")
        Row {
            IconButton(onClick = onLoad) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Загрузить")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}

@Composable
fun ShoppingItemCard(
    item: ShoppingItem,
    onCross: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${item.name} (Зачеркнут: ${item.is_crossed})")
        Row {
            IconButton(onClick = onCross) {
                Icon(Icons.Default.Check, contentDescription = "Вычеркнуть")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить")
            }
        }
    }
}



