package com.quranicvoices.app.models

data class PlayerModel(
    val currentSurah: String = "0",
    var surahProgress: Int = 0,
    val isPlayerShown: Boolean = false
)