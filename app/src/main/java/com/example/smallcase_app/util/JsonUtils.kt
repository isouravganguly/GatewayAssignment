package com.example.smallcase_app.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

fun prettyPrintJson(raw: String): String = try {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val obj = gson.fromJson(raw, JsonObject::class.java)
    gson.toJson(obj)
} catch (e: Exception) { raw }