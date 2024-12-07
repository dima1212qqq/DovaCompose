package com.example.myapplication.api

import com.example.myapplication.data.AddItemResponse
import com.example.myapplication.data.CreateListResponse
import com.example.myapplication.data.RemoveListResponse
import io.ktor.client.call.body
import io.ktor.client.request.get

suspend fun createTestKey():String{
    val client = getHttpClient()
    val url = "https://cyberprot.ru/shopping/v2/CreateTestKey?"
    return try {
        val result: String = client.get(url).body()
        result
    } catch (e: Exception) {
        "Ошибка: ${e.message}"
    }
}
suspend fun auth(key:String):Boolean{
    val client = getHttpClient()
    val url = "https://cyberprot.ru/shopping/v2/Authentication?key=$key"

    val response: Map<String, Boolean> = client.get(url).body()
    return response["success"]?:false
}

suspend fun createShoppingList(key: String, name: String): Pair<Boolean, Int?> {
    val client = getHttpClient()
    val url = "https://cyberprot.ru/shopping/v2/CreateShoppingList?key=$key&name=$name"

    val response: CreateListResponse = getHttpClient().get(url).body()
    return response.success to response.list_id
}
suspend fun removeShoppingList(listId: Int): Boolean {
    val client = getHttpClient()
    val url = "https://cyberprot.ru/shopping/v2/RemoveShoppingList?list_id=$listId"

    val response: RemoveListResponse = getHttpClient().get(url).body()
    return response.new_value ?: false
}
suspend fun addItemToShoppingList(listId: Int, itemName: String, quantity: Int): Pair<Boolean, Int?> {
    val client = getHttpClient()
    val url = "https://cyberprot.ru/shopping/v2/AddToShoppingList?id=$listId&value=$itemName&n=$quantity"

    val response: AddItemResponse = getHttpClient().get(url).body()
    return response.success to response.item_id
}
suspend fun removeItemFromList(listId: Int, itemId: Int): Boolean {
    val client = getHttpClient()
    val url = "https://cyberprot.ru/shopping/v2/RemoveFromList?list_id=$listId&item_id=$itemId"

    val response: Map<String, Boolean> = client.get(url).body()
    return response["success"] ?: false
}
suspend fun crossItemOff(itemId: Int): Boolean {
    val client = getHttpClient()
    val url = "https://cyberprot.ru/shopping/v2/CrossItOff?item_id=$itemId"

    val response: Map<String, Boolean> = client.get(url).body()
    return response["success"] ?: false
}

