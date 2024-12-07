package com.example.myapplication.android.view

import LoginScreen
import ShoppingListDetailsScreen
import ShoppingScreen
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun AppNavigator() {
    var userKey by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedListId by rememberSaveable { mutableStateOf<Int?>(null) }

    when {
        userKey == null -> {
            LoginScreen(onLoginSuccess = { key -> userKey = key })
        }
        selectedListId == null -> {
            ShoppingScreen(
                userKey = userKey!!,
                onListSelected = { listId -> selectedListId = listId }
            )
        }
        else -> {
            ShoppingListDetailsScreen(
                userKey = userKey!!,
                listId = selectedListId!!,
                onBack = { selectedListId = null }
            )
        }
    }
}

