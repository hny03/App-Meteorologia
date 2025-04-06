package com.example.climateapp.data

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun carregarCidadesDoJson(context: Context): List<Cidade> {
    val inputStream = context.assets.open("municipios.json")
    val jsonString = inputStream.bufferedReader().use { it.readText() }
    return Json.decodeFromString(jsonString)
}
