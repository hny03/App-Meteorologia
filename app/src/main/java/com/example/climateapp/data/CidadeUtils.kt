package com.example.climateapp.data

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun carregarCidadesDoJson(context: Context): List<Cidade> {
    val inputStream = context.assets.open("municipios.json")
    val jsonString = inputStream.bufferedReader().use { it.readText() }
    return Json.decodeFromString(jsonString)
}

fun buscarCidadesPorNome(context: Context, query: String): List<Cidade> {
    if (query.isBlank()) return emptyList()
    val cidades = carregarCidadesDoJson(context)
    return cidades.filter { cidade ->
        cidade.city_name.contains(query, ignoreCase = true)
    }.take(10)
}
