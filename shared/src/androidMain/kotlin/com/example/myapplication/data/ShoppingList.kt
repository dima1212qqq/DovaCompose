package com.example.myapplication.data

import com.example.myapplication.api.getHttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingList(
    val created: String,
    val name: String,
    val id: Int
)

@Serializable
data class ShoppingListsResponse(
    val shop_list: List<ShoppingList>,
    val success: Boolean
)

suspend fun getAllMyShopLists(key: String): List<ShoppingList> {
    val client = getHttpClient()
    val url = "https://cyberprot.ru/shopping/v2/GetAllMyShopLists?key=$key"

    val response: ShoppingListsResponse = client.get(url).body()
    return if (response.success) response.shop_list else emptyList()
}
@Serializable
data class ShoppingItem(
    val created: String,
    val name: String,
    val id: Int,
    val is_crossed: Boolean = false
)

@Serializable
data class ShoppingListDetailsResponse(
    val success: Boolean,
    val item_list: List<ShoppingItem>
)

suspend fun getShoppingList(listId: Int): List<ShoppingItem> {
    val client = getHttpClient()
    val url = "https://cyberprot.ru/shopping/v2/GetShoppingList?list_id=$listId"

    val response: ShoppingListDetailsResponse = client.get(url).body()
    return if (response.success) response.item_list else emptyList()
}
@Serializable
data class CreateListResponse(
    val success: Boolean,
    val list_id: Int? = null
)
@Serializable
data class RemoveListResponse(
    val success: Boolean,
    val new_value: Boolean? = null
)

@Serializable
data class AddItemResponse(
    val success: Boolean,
    val item_id: Int? = null
)
