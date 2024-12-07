package com.example.myapplication.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

actual fun createHttpClient(): HttpClient {
    return HttpClient(Android)
}
