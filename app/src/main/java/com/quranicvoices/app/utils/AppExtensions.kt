package com.quranicvoices.rabiulqaloob.utils

import android.app.Activity
import android.widget.Toast


//fun Activity.readJsonFromAssets(fileName: String): ArrayList<*> {
//    val jsonString: String
//    try {
//        val inputStream = assets.open(fileName)
//        jsonString = inputStream.bufferedReader().use { it.readText() }
//    } catch (e: Exception) {
//        e.printStackTrace()
//        return arrayListOf<>()
//    }
//
//    val gson = Gson()
//    val listType = object : TypeToken<ArrayList<>>() {}.type
//    return gson.fromJson(jsonString, listType)
//}

fun Activity.showToast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun String.capitalizeWords(): String = split(" ").joinToString(" ") {
    it.replaceFirstChar {
        if (it.isLowerCase()) {
            it.titlecase()
        } else it.toString()
    }
}